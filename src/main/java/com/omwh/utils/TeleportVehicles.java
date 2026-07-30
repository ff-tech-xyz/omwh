package com.omwh.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class TeleportVehicles {
    public static final class Result {
        public final boolean success;
        public final List<ServerPlayer> passengerPlayers;

        public Result(boolean success, List<ServerPlayer> passengerPlayers) {
            this.success = success;
            this.passengerPlayers = passengerPlayers;
        }
    }

    private record MountGraph(MountTreeSnapshot<Entity> snapshot,
                              List<ServerPlayer> passengerPlayers) { }

    private static final Logger LOGGER = LoggerFactory.getLogger("omwh:teleport");
    private static final String CROSS_DIMENSION_BLOCKED_MESSAGE =
            "§cYou are not powerful enough to bend space between dimensions. Use a portal first, then try again!";

    private TeleportVehicles() { }

    public static Result teleportWithMount(ServerPlayer player, ServerLevel targetLevel, BlockPos targetFeet) {
        Entity root = player.getRootVehicle();
        int widthBlocks = (int) Math.max(1, Math.ceil(root.getBbWidth()));
        SafeLocationPlanner.Footprint footprint = SafeLocationPlanner.Footprint.centered(widthBlocks);
        Vec3 targetPosition = new Vec3(
                targetFeet.getX() + footprint.centerOffset(),
                targetFeet.getY(),
                targetFeet.getZ() + footprint.centerOffset());
        return teleportWithMount(player, targetLevel, targetPosition, root.getYRot(), root.getXRot());
    }

    public static boolean hasExactRoom(Entity root, ServerLevel targetLevel, Vec3 targetPosition,
                                       BlockPos homeBlock) {
        int blockX = net.minecraft.util.Mth.floor(targetPosition.x);
        int blockZ = net.minecraft.util.Mth.floor(targetPosition.z);
        targetLevel.getChunk(blockX >> 4, blockZ >> 4);
        Vec3 offset = targetPosition.subtract(root.position());
        AABB vehicleBounds = root.getBoundingBox().move(offset);
        VehicleClearanceBox.Bounds clearance = VehicleClearanceBox.around(
                new VehicleClearanceBox.Bounds(
                        vehicleBounds.minX, vehicleBounds.minY, vehicleBounds.minZ,
                        vehicleBounds.maxX, vehicleBounds.maxY, vehicleBounds.maxZ));
        AABB requiredSpace = new AABB(
                clearance.minX(), clearance.minY(), clearance.minZ(),
                clearance.maxX(), clearance.maxY(), clearance.maxZ());
        CollisionContext collisionContext = CollisionContext.of(root);
        for (BlockPos blockPos : BlockPos.betweenClosed(requiredSpace)) {
            var state = targetLevel.getBlockState(blockPos);
            boolean homeBedPart = isHomeBedPart(targetLevel, blockPos, homeBlock);
            for (AABB localBounds : state.getCollisionShape(targetLevel, blockPos, collisionContext).toAabbs()) {
                AABB obstacle = localBounds.move(blockPos);
                if (VehicleClearanceBox.blocks(clearance,
                        new VehicleClearanceBox.Bounds(
                                obstacle.minX, obstacle.minY, obstacle.minZ,
                                obstacle.maxX, obstacle.maxY, obstacle.maxZ),
                        homeBedPart)) {
                    return false;
                }
            }
        }
        return targetLevel.noBorderCollision(root, requiredSpace);
    }

    private static boolean isHomeBedPart(ServerLevel level, BlockPos candidate, BlockPos homeBlock) {
        var homeState = level.getBlockState(homeBlock);
        if (!(homeState.getBlock() instanceof BedBlock)) return false;
        if (candidate.equals(homeBlock)) return true;
        BlockPos otherPart = homeBlock.relative(BedBlock.getConnectedDirection(homeState));
        return candidate.equals(otherPart)
                && level.getBlockState(otherPart).getBlock() instanceof BedBlock;
    }

    public static Result teleportWithMount(ServerPlayer player, ServerLevel targetLevel,
                                           Vec3 targetPosition, float yRot, float xRot) {
        Entity root = player.getRootVehicle();
        ServerLevel rootLevel = WorldCompat.getLevel(root);
        if (rootLevel == null || !rootLevel.dimension().equals(targetLevel.dimension())) {
            player.sendSystemMessage(Component.literal(CROSS_DIMENSION_BLOCKED_MESSAGE), false);
            return new Result(false, Collections.emptyList());
        }

        MountGraph graph = flatten(root, player);

        // Load the destination before changing entity state. Minecraft 26.2 recursively teleports
        // the attached passenger tree when the root receives one same-dimension transition.
        int blockX = net.minecraft.util.Mth.floor(targetPosition.x);
        int blockZ = net.minecraft.util.Mth.floor(targetPosition.z);
        targetLevel.getChunk(blockX >> 4, blockZ >> 4);
        Entity moved;
        try {
            moved = root.teleport(new TeleportTransition(
                    targetLevel,
                    targetPosition,
                    Vec3.ZERO,
                    yRot,
                    xRot,
                    TeleportTransition.DO_NOTHING));
        } catch (RuntimeException exception) {
            LOGGER.error("Mounted teleport failed before Minecraft completed the attached tree transition", exception);
            return new Result(false, Collections.emptyList());
        }

        if (moved == null) {
            LOGGER.warn("Mounted teleport returned no entity for {}", player.getGameProfile().name());
            return new Result(false, Collections.emptyList());
        }
        moved.setDeltaMovement(Vec3.ZERO);
        if (!graph.snapshot().isIntact(Entity::getVehicle)) {
            LOGGER.error("Minecraft returned from a mounted teleport with a changed passenger tree for {}",
                    player.getGameProfile().name());
            return new Result(false, Collections.emptyList());
        }

        return new Result(true, graph.passengerPlayers());
    }

    private static MountGraph flatten(Entity root, ServerPlayer sourcePlayer) {
        List<MountTreeSnapshot.Edge<Entity>> edges = new ArrayList<>();
        List<ServerPlayer> passengerPlayers = new ArrayList<>();
        Deque<Entity> queue = new ArrayDeque<>();
        Map<Entity, Boolean> seen = new IdentityHashMap<>();
        queue.add(root);
        seen.put(root, Boolean.TRUE);
        while (!queue.isEmpty()) {
            Entity parent = queue.removeFirst();
            for (Entity child : parent.getPassengers()) {
                if (seen.put(child, Boolean.TRUE) != null) continue;
                edges.add(new MountTreeSnapshot.Edge<>(parent, child));
                if (child instanceof ServerPlayer passenger && passenger != sourcePlayer) {
                    passengerPlayers.add(passenger);
                }
                queue.addLast(child);
            }
        }
        return new MountGraph(new MountTreeSnapshot<>(edges), List.copyOf(passengerPlayers));
    }
}
