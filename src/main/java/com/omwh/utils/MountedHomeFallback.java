package com.omwh.utils;

/** Pure policy and coordinate calculation for mounted bed-home fallback placement. */
public final class MountedHomeFallback {
    public enum Choice { VANILLA, ABOVE_BED, DENY }

    public record Position(double x, double y, double z) { }

    private MountedHomeFallback() { }

    public static Choice choose(boolean mounted, boolean homeIsBed, boolean forcedHome,
                                boolean exactFits, boolean fallbackFits) {
        if (!mounted || exactFits) return Choice.VANILLA;
        if (homeIsBed && !forcedHome && fallbackFits) return Choice.ABOVE_BED;
        return Choice.DENY;
    }

    public static Position aboveBed(int homeX, int homeY, int homeZ) {
        return new Position(homeX + 0.5, homeY + 1.0, homeZ + 0.5);
    }
}
