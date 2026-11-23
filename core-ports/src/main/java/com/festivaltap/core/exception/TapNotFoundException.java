package com.festivaltap.core.exception;

/**
 * <h1>Tap Not Found Exception</h1>
 * <p>Thrown when an operation references a non-existent tap identifier. Maps to HTTP 404 (TAP_NOT_FOUND).</p>
 */
public class TapNotFoundException extends FestivalException {
    public TapNotFoundException(String tapId) { super("Tap not found: " + tapId); }
    public TapNotFoundException(String tapId, Throwable cause) { super("Tap not found: " + tapId, cause); }
}
