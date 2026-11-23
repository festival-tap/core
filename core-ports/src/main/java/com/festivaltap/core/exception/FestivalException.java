package com.festivaltap.core.exception;

/**
 * <h1>Abstract Base for All Domain Exceptions</h1>
 *
 * <p>All business-rule violations and domain-specific errors in the Beer Tap Dispenser
 * system must be expressed as subclasses of {@code FestivalException}. This ensures
 * clear separation between expected business errors and technical failures.</p>
 *
 * <h2>HTTP Mapping Convention</h2>
 * <ul>
 *   <li>Subclasses representing client errors (bad input) → 400 Bad Request</li>
 *   <li>Subclasses representing resource not found → 404 Not Found</li>
 *   <li>Subclasses representing state conflicts → 409 Conflict</li>
 * </ul>
 */
public abstract class FestivalException extends RuntimeException {

    protected FestivalException(String message) {
        super(message);
    }

    protected FestivalException(String message, Throwable cause) {
        super(message, cause);
    }
}
