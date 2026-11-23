package com.festivaltap.core.ports.in.command;

import com.festivaltap.core.exception.InvalidTapCommandException;
import java.time.Instant;
import java.util.Set;

/**
 * Immutable command payload representing a request to change the operational state of a tap.
 *
 * @param eventId    unique identifier for idempotency
 * @param tapId      identifier of the target tap
 * @param status     requested new status (case-insensitive, must be {@code "open"} or {@code "close"})
 * @param occurredAt timestamp of the state change
 */
public record TapEventCommand(
        String eventId,
        String tapId,
        String status,
        Instant occurredAt
) {
    private static final Set<String> ALLOWED_STATUSES = Set.of("open", "close");

    public TapEventCommand {
        if (eventId == null || eventId.isBlank()) {
            throw new InvalidTapCommandException("eventId must not be null or blank");
        }
        if (tapId == null || tapId.isBlank()) {
            throw new InvalidTapCommandException("tapId must not be null or blank");
        }
        if (status == null || status.isBlank()) {
            throw new InvalidTapCommandException("status must not be null or blank");
        }
        if (occurredAt == null) {
            throw new InvalidTapCommandException("occurredAt must not be null");
        }
        String normalized = status.toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new InvalidTapCommandException("status must be 'open' or 'close', got: " + status);
        }
    }

    public String normalizedStatus() {
        return status.toLowerCase();
    }
}
