package com.omwh.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RegressionTestSuite {
    public static void main(String[] args) {
        mountedTreeSnapshotRequiresEveryOriginalParentToRemainAttached();
        footprintIsCenteredForOddAndEvenWidths();
        safeSelectionUsesFeetFullSupportHeadroomAndNearestBoundedCandidate();
        safeSelectionReportsExactNearbyAndBlockedOutcomes();
        homeRejectsAbsentRespawnConfiguration();
        homeRejectsVanillaMissingRespawnBlockTransition();
        homeRejectsCrossDimensionVanillaTransition();
        homeRejectsMountedTreeWhenRootCannotFitExactVanillaDestination();
        vehicleClearanceAddsHalfBlockHorizontallyAndTwoAndAHalfAbove();
        System.out.println("OMWH regression tests passed");
    }

    private static void mountedTreeSnapshotRequiresEveryOriginalParentToRemainAttached() {
        Node root = new Node("root");
        Node saddle = new Node("saddle");
        Node player = new Node("player");
        Node wrongVehicle = new Node("wrong");
        Map<Node, Node> currentParents = new HashMap<>();
        currentParents.put(saddle, root);
        currentParents.put(player, saddle);
        MountTreeSnapshot<Node> snapshot = new MountTreeSnapshot<>(List.of(
                new MountTreeSnapshot.Edge<>(root, saddle),
                new MountTreeSnapshot.Edge<>(saddle, player)));

        assertTrue(snapshot.isIntact(currentParents::get), "captured tree should be intact");
        currentParents.put(player, wrongVehicle);
        assertTrue(!snapshot.isIntact(currentParents::get),
                "a passenger on the wrong parent must not count as preserved");
    }

    private static void footprintIsCenteredForOddAndEvenWidths() {
        SafeLocationPlanner.Footprint odd = SafeLocationPlanner.Footprint.centered(3);
        SafeLocationPlanner.Footprint even = SafeLocationPlanner.Footprint.centered(2);
        assertEquals(-1, odd.minX(), "odd footprint minimum");
        assertEquals(1, odd.maxX(), "odd footprint maximum");
        assertEquals(3, odd.width(), "odd footprint width");
        assertEquals(0.5, odd.centerOffset(), "odd footprint center offset");
        assertEquals(-1, even.minX(), "even footprint minimum");
        assertEquals(0, even.maxX(), "even footprint maximum");
        assertEquals(2, even.width(), "even footprint width");
        assertEquals(0.0, even.centerOffset(), "even footprint center offset");
    }

    private static void safeSelectionUsesFeetFullSupportHeadroomAndNearestBoundedCandidate() {
        SafeLocationPlanner.Pos targetFeet = new SafeLocationPlanner.Pos(0, 64, 0);
        SafeLocationPlanner.Footprint footprint = SafeLocationPlanner.Footprint.centered(2);
        Set<SafeLocationPlanner.Pos> support = new HashSet<>();
        addSupport(support, -1, 64, footprint);
        addSupport(support, 3, 64, footprint);
        Set<SafeLocationPlanner.Pos> blocked = Set.of(new SafeLocationPlanner.Pos(-1, 66, 0));
        List<SafeLocationPlanner.Pos> inspected = new ArrayList<>();

        SafeLocationPlanner.Selection selection = SafeLocationPlanner.select(
                targetFeet, 3, -2, 10, footprint, 3,
                new SafeLocationPlanner.CellProbe() {
                    public boolean isClear(SafeLocationPlanner.Pos pos) {
                        inspected.add(pos);
                        return !blocked.contains(pos);
                    }
                    public boolean isSafeSupport(SafeLocationPlanner.Pos pos) {
                        inspected.add(pos);
                        return support.contains(pos);
                    }
                });

        assertEquals(SafeLocationPlanner.Outcome.NEARBY, selection.outcome(), "selection outcome");
        assertEquals(new SafeLocationPlanner.Pos(3, 64, 0), selection.feet(),
                "selection did not return the nearest valid feet coordinate");
        assertTrue(inspected.stream().allMatch(pos -> Math.abs(pos.x() - targetFeet.x()) <= 4
                        && Math.abs(pos.z() - targetFeet.z()) <= 4
                        && pos.y() >= targetFeet.y() - 3 && pos.y() <= targetFeet.y() + 12),
                "search inspected cells outside its bounded candidate volumes");
    }

    private static void safeSelectionReportsExactNearbyAndBlockedOutcomes() {
        SafeLocationPlanner.Pos target = new SafeLocationPlanner.Pos(10, 70, 10);
        SafeLocationPlanner.Footprint one = SafeLocationPlanner.Footprint.centered(1);
        SafeLocationPlanner.CellProbe exactProbe = probeForFeet(Set.of(target), one, 2);
        assertEquals(SafeLocationPlanner.Outcome.EXACT,
                SafeLocationPlanner.select(target, 2, -1, 1, one, 2, exactProbe).outcome(),
                "safe target should be reported as exact");

        SafeLocationPlanner.Pos nearby = new SafeLocationPlanner.Pos(11, 70, 10);
        SafeLocationPlanner.CellProbe nearbyProbe = probeForFeet(Set.of(nearby), one, 2);
        SafeLocationPlanner.Selection moved = SafeLocationPlanner.select(target, 2, -1, 1, one, 2, nearbyProbe);
        assertEquals(SafeLocationPlanner.Outcome.NEARBY, moved.outcome(), "fallback should be understandable");
        assertEquals(nearby, moved.feet(), "nearby fallback feet coordinate");

        SafeLocationPlanner.Selection blocked = SafeLocationPlanner.select(
                target, 2, -1, 1, one, 2, probeForFeet(Set.of(), one, 2));
        assertEquals(SafeLocationPlanner.Outcome.BLOCKED, blocked.outcome(), "blocked search outcome");
        assertEquals(null, blocked.feet(), "blocked search must not invent a fallback");
    }

    private static void homeRejectsAbsentRespawnConfiguration() {
        assertEquals(HomeRespawnDecision.Outcome.NO_HOME,
                HomeRespawnDecision.decide(false, false, true, false, true),
                "a player without a configured bed or anchor must not fall back to world spawn");
    }

    private static void homeRejectsVanillaMissingRespawnBlockTransition() {
        assertEquals(HomeRespawnDecision.Outcome.NO_HOME,
                HomeRespawnDecision.decide(true, true, true, false, true),
                "a missing respawn block transition must not be used by /home");
    }

    private static void homeRejectsCrossDimensionVanillaTransition() {
        assertEquals(HomeRespawnDecision.Outcome.CROSS_DIMENSION,
                HomeRespawnDecision.decide(true, false, false, false, true),
                "/home must not follow a vanilla transition into another dimension");
    }

    private static void homeRejectsMountedTreeWhenRootCannotFitExactVanillaDestination() {
        assertEquals(HomeRespawnDecision.Outcome.VEHICLE_TOO_BIG,
                HomeRespawnDecision.decide(true, false, true, true, false),
                "mounted /home must fail instead of searching away from vanilla's exact destination");
        assertEquals(HomeRespawnDecision.Outcome.ACCEPT,
                HomeRespawnDecision.decide(true, false, true, false, false),
                "an unmounted player uses vanilla's already-validated destination");
    }

    private static void vehicleClearanceAddsHalfBlockHorizontallyAndTwoAndAHalfAbove() {
        VehicleClearanceBox.Bounds clearance = VehicleClearanceBox.around(
                new VehicleClearanceBox.Bounds(10.25, 64.0, -4.75, 11.75, 65.5, -3.25));

        assertEquals(new VehicleClearanceBox.Bounds(9.75, 64.0, -5.25, 12.25, 68.0, -2.75),
                clearance,
                "vehicle clearance must use the exact requested horizontal and upper margins");
    }

    private static SafeLocationPlanner.CellProbe probeForFeet(
            Set<SafeLocationPlanner.Pos> validFeet, SafeLocationPlanner.Footprint footprint, int height) {
        Set<SafeLocationPlanner.Pos> supports = new HashSet<>();
        Set<SafeLocationPlanner.Pos> clear = new HashSet<>();
        for (SafeLocationPlanner.Pos feet : validFeet) {
            addSupport(supports, feet.x(), feet.y(), footprint, feet.z());
            for (int dx = footprint.minX(); dx <= footprint.maxX(); dx++) {
                for (int dz = footprint.minZ(); dz <= footprint.maxZ(); dz++) {
                    for (int dy = 0; dy < height; dy++) {
                        clear.add(new SafeLocationPlanner.Pos(feet.x() + dx, feet.y() + dy, feet.z() + dz));
                    }
                }
            }
        }
        return new SafeLocationPlanner.CellProbe() {
            public boolean isClear(SafeLocationPlanner.Pos pos) { return clear.contains(pos); }
            public boolean isSafeSupport(SafeLocationPlanner.Pos pos) { return supports.contains(pos); }
        };
    }

    private static void addSupport(Set<SafeLocationPlanner.Pos> support, int feetX, int feetY,
                                   SafeLocationPlanner.Footprint footprint) {
        addSupport(support, feetX, feetY, footprint, 0);
    }

    private static void addSupport(Set<SafeLocationPlanner.Pos> support, int feetX, int feetY,
                                   SafeLocationPlanner.Footprint footprint, int feetZ) {
        for (int dx = footprint.minX(); dx <= footprint.maxX(); dx++) {
            for (int dz = footprint.minZ(); dz <= footprint.maxZ(); dz++) {
                support.add(new SafeLocationPlanner.Pos(feetX + dx, feetY - 1, feetZ + dz));
            }
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message + "; expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private record Node(String name) { }
}
