package com.festivaltap.core.exception;

/**
 * <h1>Sequence Conflict Exception</h1>
 * <p>Thrown on sequence number mismatches during event append. Maps to HTTP 409 (SEQUENCE_CONFLICT).</p>
 */
public class SequenceConflictException extends RuntimeException {
    public SequenceConflictException(String tapId, long expectedSequence, long actualSequence) {
        super(String.format("Concurrent modification on tap %s: expected sequence %d but current is %d",
                tapId, expectedSequence, actualSequence));
    }
    public SequenceConflictException(String message, Throwable cause) { super(message, cause); }
}
