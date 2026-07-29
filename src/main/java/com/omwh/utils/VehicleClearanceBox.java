package com.omwh.utils;

/** Builds the exact free-space volume required around a mounted vehicle at /home. */
public final class VehicleClearanceBox {
    public static final double HORIZONTAL_MARGIN = 0.5;
    public static final double UPPER_MARGIN = 2.5;

    public record Bounds(double minX, double minY, double minZ,
                         double maxX, double maxY, double maxZ) { }

    private VehicleClearanceBox() { }

    public static Bounds around(Bounds vehicle) {
        return new Bounds(
                vehicle.minX() - HORIZONTAL_MARGIN,
                vehicle.minY(),
                vehicle.minZ() - HORIZONTAL_MARGIN,
                vehicle.maxX() + HORIZONTAL_MARGIN,
                vehicle.maxY() + UPPER_MARGIN,
                vehicle.maxZ() + HORIZONTAL_MARGIN);
    }
}
