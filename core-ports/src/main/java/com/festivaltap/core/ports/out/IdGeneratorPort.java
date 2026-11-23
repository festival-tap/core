package com.festivaltap.core.ports.out;

import java.util.UUID;

/**
 * Outbound identifier generator port, decoupling domain services from random UUID calls
 * for deterministic behavioral testing.
 */
public interface IdGeneratorPort {

    /**
     * Generates a unique identifier string (defaults to {@link UUID#randomUUID()}).
     */
    default String nextId() {
        return UUID.randomUUID().toString();
    }
}
