package com.festivaltap.core.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record PourSession(
        String tapId,
        double tapFlowVolume,
        Instant openedAt,
        Instant closedAt,
        double beerPrice
) {

    public PourSession {
        if (openedAt == null) {
            throw new IllegalArgumentException("OpenedAt timestamp is mandatory for a pour session.");
        }
        if (closedAt != null && closedAt.isBefore(openedAt)) {
            throw new IllegalArgumentException("ClosedAt cannot be before OpenedAt.");
        }
    }

    public boolean isOpen() {
        return Objects.isNull(closedAt);
    }

    public long durationMillis(Instant snapshotAt) {
        if (isOpen() && snapshotAt.isBefore(openedAt)) {
            throw new IllegalArgumentException("snapshotAt cannot be before openedAt");
        }
        Instant end = isOpen() ? snapshotAt : closedAt;
        return Duration.between(openedAt, end).toMillis();
    }

    public double totalVolumeDispensed(Instant snapshotAt) {
        return (durationMillis(snapshotAt) / 1000.0) * tapFlowVolume;
    }

    public double totalSpent(Instant snapshotAt) {
        return totalVolumeDispensed(snapshotAt) * beerPrice;
    }
}
