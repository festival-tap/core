package com.festivaltap.core.exception;

/**
 * <h1>Tap Already Closed Exception</h1>
 * <p>Thrown when attempting to close an already closed tap. Maps to HTTP 409 (TAP_ALREADY_CLOSED).</p>
 */
public class TapAlreadyClosedException extends FestivalException {
    public TapAlreadyClosedException(String tapId) { super("Tap already closed: " + tapId); }
}
