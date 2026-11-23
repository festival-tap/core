package com.festivaltap.core.ports.in.command;

import com.festivaltap.core.exception.InvalidTapCommandException;

/**
 * Immutable command carrying the tap identifier for which to fetch the usage and spending report.
 *
 * @param tapId the unique identifier of the tap (non-null, non-blank)
 */
public record FetchUsageReportCommand(String tapId) {

    public FetchUsageReportCommand {
        if (tapId == null || tapId.isBlank()) {
            throw new InvalidTapCommandException("tapId must not be null or blank");
        }
    }
}
