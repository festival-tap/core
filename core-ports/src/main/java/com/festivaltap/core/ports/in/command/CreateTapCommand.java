package com.festivaltap.core.ports.in.command;

import com.festivaltap.core.exception.InvalidTapCommandException;

/**
 * Immutable command carrying the required data to create a new tap dispenser.
 *
 * @param flowVolume the fixed flow rate of the tap in litres per second (must be &gt; 0)
 */
public record CreateTapCommand(Double flowVolume) {

    public CreateTapCommand {
        if (flowVolume == null) {
            throw new InvalidTapCommandException("flowVolume is required");
        }
        if (flowVolume <= 0.0 || Double.isNaN(flowVolume) || Double.isInfinite(flowVolume)) {
            throw new InvalidTapCommandException("flowVolume must be positive, got: " + flowVolume);
        }
    }
}
