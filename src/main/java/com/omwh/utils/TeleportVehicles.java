package com.omwh.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
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
        ServerLevel rootLevel = WorldCompat.getLevel(root);
        if (rootLevel == null || !rootLevel.dimension().equals(targetLevel.dimension())) {
            player.sendSystemMessage(Component.literal(CROSS_DIMENSION_BLOCKED_MESSAGE), false);
            return new Result(false, Collections.emptyList());
        }

        MountGraph graph = flatten(root, player);
        int widthBlocks = (int) Math.max(1, Math.ceil(root.getBbWidth()));
        SafeLocationPlanner.Footprint footprint = SafeLocationPlanner.Footprint.centered(widthBlocks);
        double x = targetFeet.getX() + footprint.centerOffset();
        double y = targetFeet.getY();
        double z = targetFeet.getZ() + footprint.centerOffset();

        // Load the destination before changing entity state. Minecraft 26.2 recursively teleports
        // the attached passenger tree when the root receives one same-dimension transition.
        targetLevel.getChunk(targetFeet.getX() >> 4, targetFeet.getZ() >> 4);
        Entity moved;
        try {
            moved = root.teleport(new TeleportTransition(
                    targetLevel,
                    new Vec3(x, y, z),
                    Vec3.ZERO,
                    root.getYRot(),
                    root.getXRot(),
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
