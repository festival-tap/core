package com.festivaltap.core.model;

import java.time.Instant;
import java.util.UUID;

public record TapEvent(
        long sequenceNumber,
        UUID eventId,
        String tapId,
        String status,
        Instant occurredAt
) {

    public TapEvent {
        if (sequenceNumber < 1) {
            throw new IllegalArgumentException("sequenceNumber must be >= 1");
        }
        if (eventId == null) {
            throw new IllegalArgumentException("eventId must not be null");
        }
        if (tapId == null || tapId.isBlank()) {
            throw new IllegalArgumentException("tapId must not be null or blank");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status must not be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt must not be null");
        }
    }
}
