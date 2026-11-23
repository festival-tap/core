package com.festivaltap.core.model;

/**
 * Immutable configuration of a physical tap dispenser asset.
 *
 * @param id         unique identifier of the tap (non-null, non-blank)
 * @param flowVolume fixed dispensing rate in litres per second (strictly positive)
 */
public record Tap(String id, double flowVolume) {

    public Tap {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Tap id must not be null or blank");
        }
        if (flowVolume <= 0 || Double.isNaN(flowVolume) || Double.isInfinite(flowVolume)) {
            throw new IllegalArgumentException("Tap flowVolume must be a finite positive number");
        }
    }
}
