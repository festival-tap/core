package com.festivaltap.core.exception;

/**
 * <h1>Corrupted Event Stream Exception</h1>
 * <p>Thrown on invalid event stream sequences. Maps to HTTP 500 (CORRUPTED_EVENT_STREAM).</p>
 */
public class CorruptedEventStreamException extends FestivalException {
    public CorruptedEventStreamException(String message) { super(message); }
    public CorruptedEventStreamException(String message, Throwable cause) { super(message, cause); }
}
