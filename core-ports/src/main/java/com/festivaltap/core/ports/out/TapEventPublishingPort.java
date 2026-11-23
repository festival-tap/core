package com.festivaltap.core.ports.out;

import com.festivaltap.core.exception.SequenceConflictException;
import com.festivaltap.core.model.TapEvent;

/**
 * Outbound write-side port for atomically appending immutable domain events
 * with optimistic concurrency control.
 */
public interface TapEventPublishingPort {

    /**
     * Atomically appends an immutable domain event to the tap's event stream if
     * the store's next sequence number matches {@code expectedSequenceNumber}.
     */
    void append(TapEvent event, long expectedSequenceNumber);
}
