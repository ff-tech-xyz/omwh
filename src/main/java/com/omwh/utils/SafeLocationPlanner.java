package com.omwh.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Pure, deterministic safe-location geometry and nearest-first search. */
public final class SafeLocationPlanner {
    private SafeLocationPlanner() { }

    public record Pos(int x, int y, int z) {
        public Pos offset(int dx, int dy, int dz) {
            return new Pos(x + dx, y + dy, z + dz);
        }
    }

    public record Footprint(int minX, int maxX, int minZ, int maxZ) {
        public Footprint {
            if (minX > maxX || minZ > maxZ) throw new IllegalArgumentException("empty footprint");
        }

        public static Footprint centered(int width) {
            if (width < 1) throw new IllegalArgumentException("width must be positive");
            int minimum = -(width / 2);
            int maximum = minimum + width - 1;
            return new Footprint(minimum, maximum, minimum, maximum);
        }

        public int width() {
            return maxX - minX + 1;
        }

        /** Block-center offset that keeps this footprint geometrically centered. */
        public double centerOffset() {
            return width() % 2 == 0 ? 0.0 : 0.5;
        }
    }

    public interface CellProbe {
        boolean isClear(Pos pos);
        boolean isSafeSupport(Pos pos);
    }

    public enum Outcome { EXACT, NEARBY, BLOCKED }

    public record Selection(Outcome outcome, Pos feet) {
        public Selection {
            Objects.requireNonNull(outcome, "outcome");
            if (outcome == Outcome.BLOCKED && feet != null) {
                throw new IllegalArgumentException("blocked selection cannot contain feet");
            }
            if (outcome != Outcome.BLOCKED && feet == null) {
                throw new IllegalArgumentException("successful selection requires feet");
            }
        }
    }

    public static Selection select(Pos targetFeet, int maxRadius, int minYOffset, int maxYOffset,
                                   Footprint footprint, int height, CellProbe probe) {
        Objects.requireNonNull(targetFeet, "targetFeet");
        Objects.requireNonNull(footprint, "footprint");
        Objects.requireNonNull(probe, "probe");
        if (maxRadius < 0) throw new IllegalArgumentException("maxRadius cannot be negative");
        if (minYOffset > maxYOffset) throw new IllegalArgumentException("invalid vertical range");
        if (height < 1) throw new IllegalArgumentException("height must be positive");

        List<Offset> candidates = new ArrayList<>((maxRadius * 2 + 1) * (maxRadius * 2 + 1)
                * (maxYOffset - minYOffset + 1));
        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                for (int dy = minYOffset; dy <= maxYOffset; dy++) {
                    candidates.add(new Offset(dx, dy, dz));
                }
            }
        }
        candidates.sort(Comparator
                .comparingLong(Offset::distanceSquared)
                .thenComparingInt(offset -> Math.abs(offset.dy()))
                .thenComparingInt(Offset::dy)
                .thenComparingInt(Offset::dx)
                .thenComparingInt(Offset::dz));

        for (Offset offset : candidates) {
            Pos feet = targetFeet.offset(offset.dx(), offset.dy(), offset.dz());
            if (isSafe(feet, footprint, height, probe)) {
                return new Selection(offset.distanceSquared() == 0 ? Outcome.EXACT : Outcome.NEARBY, feet);
            }
        }
        return new Selection(Outcome.BLOCKED, null);
    }

    private static boolean isSafe(Pos feet, Footprint footprint, int height, CellProbe probe) {
        for (int dx = footprint.minX(); dx <= footprint.maxX(); dx++) {
            for (int dz = footprint.minZ(); dz <= footprint.maxZ(); dz++) {
                if (!probe.isSafeSupport(feet.offset(dx, -1, dz))) return false;
                for (int dy = 0; dy < height; dy++) {
                    if (!probe.isClear(feet.offset(dx, dy, dz))) return false;
                }
            }
        }
        return true;
    }

    private record Offset(int dx, int dy, int dz) {
        long distanceSquared() {
            return (long) dx * dx + (long) dy * dy + (long) dz * dz;
        }
    }
}
