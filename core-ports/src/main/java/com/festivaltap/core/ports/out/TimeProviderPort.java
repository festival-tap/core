package com.festivaltap.core.ports.out;

import java.time.Instant;

/**
 * Outbound clock port for obtaining the current timestamp, enabling deterministic testing.
 */
public interface TimeProviderPort {

    /**
     * Returns the current instant (defaults to {@link Instant#now()}).
     */
    default Instant now() {
        return Instant.now();
    }
}
