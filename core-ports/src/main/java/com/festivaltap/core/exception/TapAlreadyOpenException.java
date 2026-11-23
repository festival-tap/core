package com.festivaltap.core.exception;

/**
 * <h1>Tap Already Open Exception</h1>
 * <p>Thrown when attempting to open an already open tap. Maps to HTTP 409 (TAP_ALREADY_OPEN).</p>
 */
public class TapAlreadyOpenException extends FestivalException {
    public TapAlreadyOpenException(String tapId) { super("Tap already open: " + tapId); }
}
