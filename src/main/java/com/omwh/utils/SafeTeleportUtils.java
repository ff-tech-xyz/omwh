package com.omwh.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SafeTeleportUtils {
    private static final int MIN_VERTICAL_OFFSET = -2;
    private static final int MAX_VERTICAL_OFFSET = 10;

    private SafeTeleportUtils() { }

    public static BlockPos findSafeLocation(ServerLevel world, BlockPos targetFeet,
                                            int maxRadius, boolean isSpawn) {
        SafeLocationPlanner.Selection selection = findSafeSelectionForSize(
                world, targetFeet, maxRadius, 1, 2);
        return selection.feet() == null ? null : toBlockPos(selection.feet());
    }

    public static BlockPos findSafeLocationForSize(ServerLevel world, BlockPos targetFeet,
                                                   int maxRadius, int widthBlocks,
                                                   int heightBlocks, boolean isSpawn) {
        SafeLocationPlanner.Selection selection = findSafeSelectionForSize(
                world, targetFeet, maxRadius, widthBlocks, heightBlocks);
        return selection.feet() == null ? null : toBlockPos(selection.feet());
    }

    public static SafeLocationPlanner.Selection findSafeSelectionForSize(
            ServerLevel world, BlockPos targetFeet, int maxRadius, int widthBlocks, int heightBlocks) {
        SafeLocationPlanner.Pos target = toPlannerPos(targetFeet);
        SafeLocationPlanner.Footprint footprint = SafeLocationPlanner.Footprint.centered(
                Math.max(1, widthBlocks));
        return SafeLocationPlanner.select(target, Math.max(0, maxRadius),
                MIN_VERTICAL_OFFSET, MAX_VERTICAL_OFFSET, footprint, Math.max(1, heightBlocks),
                new SafeLocationPlanner.CellProbe() {
                    @Override
                    public boolean isClear(SafeLocationPlanner.Pos pos) {
                        if (pos.y() < world.getMinY() || pos.y() >= world.getMaxY()) return false;
                        BlockPos blockPos = toBlockPos(pos);
                        var state = world.getBlockState(blockPos);
                        return state.getFluidState().isEmpty()
                                && state.getCollisionShape(world, blockPos).isEmpty()
                                && !isDangerousBlock(state.getBlock().getDescriptionId());
                    }

                    @Override
                    public boolean isSafeSupport(SafeLocationPlanner.Pos pos) {
                        if (pos.y() < world.getMinY() || pos.y() >= world.getMaxY()) return false;
                        BlockPos blockPos = toBlockPos(pos);
                        var state = world.getBlockState(blockPos);
                        return state.getFluidState().isEmpty()
                                && state.isCollisionShapeFullBlock(world, blockPos)
                                && !isDangerousBlock(state.getBlock().getDescriptionId());
                    }
                });
    }

    static SafeLocationPlanner.Pos toPlannerPos(BlockPos pos) {
        return new SafeLocationPlanner.Pos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos toBlockPos(SafeLocationPlanner.Pos pos) {
        return new BlockPos(pos.x(), pos.y(), pos.z());
    }

    private static boolean isSafeLocation(ServerLevel world, BlockPos feet) {
        return findSafeSelectionForSize(world, feet, 0, 1, 2).outcome()
                == SafeLocationPlanner.Outcome.EXACT;
    }

    private static boolean isDangerousBlock(String blockId) {
        if (blockId == null) return false;
        String id = blockId.toLowerCase(Locale.ROOT);
        return id.contains("lava") || id.contains("fire") || id.contains("magma")
                || id.contains("cactus") || id.contains("sweet_berry_bush")
                || id.contains("wither_rose") || id.contains("powder_snow");
    }

    private static boolean isPassableForWalking(ServerLevel world, BlockPos feet) {
        return isSafeLocation(world, feet);
    }

    public static boolean hasNavigablePath(ServerLevel world, BlockPos start, BlockPos goal, int maxSteps) {
        if (start.equals(goal)) return true;
        HashSet<BlockPos> visited = new HashSet<>();
        ArrayDeque<Map.Entry<BlockPos, Integer>> queue = new ArrayDeque<>();
        queue.add(Map.entry(start, 0));
        visited.add(start);
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty() && visited.size() <= maxSteps) {
            Map.Entry<BlockPos, Integer> entry = queue.removeFirst();
            if (entry.getValue() >= maxSteps) continue;
            for (int[] direction : directions) {
                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos next = entry.getKey().offset(direction[0], dy, direction[1]);
                    if (visited.contains(next) || !isPassableForWalking(world, next)) continue;
                    if (next.equals(goal)) return true;
                    visited.add(next);
                    queue.add(Map.entry(next, entry.getValue() + 1));
                }
            }
        }
        return false;
    }

    public static BlockPos findSafeLocationWithPath(ServerLevel world, BlockPos targetFeet,
                                                     int maxRadius, boolean isSpawn,
                                                     BlockPos pathTarget) {
        BlockPos candidate = findSafeLocation(world, targetFeet, maxRadius, isSpawn);
        return candidate != null && hasNavigablePath(world, candidate, pathTarget, 2048)
                ? candidate : null;
    }

    public static boolean isSafeForTeleport(ServerLevel world, BlockPos feet) {
        return isSafeLocation(world, feet);
    }

    public static boolean safeTeleport(net.minecraft.server.level.ServerPlayer player,
                                       ServerLevel world, BlockPos targetFeet,
                                       int maxRadius, boolean isSpawn) {
        BlockPos safeFeet = findSafeLocation(world, targetFeet, maxRadius, isSpawn);
        if (safeFeet == null) return false;
        player.teleportTo(world, safeFeet.getX() + 0.5, safeFeet.getY(), safeFeet.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        return true;
    }

    public static Map.Entry<Boolean, List<net.minecraft.server.level.ServerPlayer>>
    safeTeleportWithVehicleCollect(net.minecraft.server.level.ServerPlayer player,
                                   ServerLevel world, BlockPos targetFeet,
                                   int maxRadius, boolean isSpawn) {
        var root = player.getRootVehicle();
        int width = root == player ? 1 : (int) Math.max(1, Math.ceil(root.getBbWidth()));
        int height = root == player ? 2 : (int) Math.max(3, Math.ceil(root.getBbHeight()) + 2);
        BlockPos safeFeet = findSafeLocationForSize(
                world, targetFeet, maxRadius, width, height, isSpawn);
        if (safeFeet == null) return Map.entry(false, List.of());
        TeleportVehicles.Result result = TeleportVehicles.teleportWithMount(player, world, safeFeet);
        return Map.entry(result.success, result.passengerPlayers);
    }

    /** Kept for source compatibility; selection is now deterministic and nearest-first. */
    public static BlockPos findRandomSafeLocation(ServerLevel world, BlockPos centerFeet,
                                                   int radius, int width, int height,
                                                   boolean preferGrassAndSky) {
        return findSafeLocationForSize(world, centerFeet, radius, width, height, true);
    }
}
