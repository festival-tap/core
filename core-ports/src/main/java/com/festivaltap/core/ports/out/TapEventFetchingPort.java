package com.festivaltap.core.ports.out;

import com.festivaltap.core.model.TapEvent;
import java.util.List;

/**
 * Outbound read-side port for querying sequence numbers and streaming immutable
 * domain events in ascending chronological order.
 */
public interface TapEventFetchingPort {

    /**
     * Retrieves the highest persisted sequence number for a given tap, or {@code 0} if none.
     */
    long getCurrentSequenceNumber(String tapId);

    /**
     * Returns all events for a tap in strictly ascending chronological order (oldest first).
     */
    List<TapEvent> streamForTap(String tapId);
}
