package com.omwh.utils;

/** Pure policy for accepting the exact destination selected by vanilla respawn logic. */
public final class HomeRespawnDecision {
    public enum Outcome { ACCEPT, NO_HOME, CROSS_DIMENSION, VEHICLE_TOO_BIG }

    private HomeRespawnDecision() { }

    public static Outcome decide(boolean hasRespawnConfig, boolean missingRespawnBlock,
                                 boolean sameDimension, boolean mounted, boolean rootFits) {
        if (!hasRespawnConfig) return Outcome.NO_HOME;
        if (missingRespawnBlock) return Outcome.NO_HOME;
        if (!sameDimension) return Outcome.CROSS_DIMENSION;
        if (mounted && !rootFits) return Outcome.VEHICLE_TOO_BIG;
        return Outcome.ACCEPT;
    }
}
