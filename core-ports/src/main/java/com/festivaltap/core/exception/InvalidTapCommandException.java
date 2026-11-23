package com.festivaltap.core.exception;

/**
 * <h1>Invalid Tap Command Exception</h1>
 *
 * <p>Thrown when a command object fails structural or semantic validation.
 * Examples: negative flow volume, invalid status string, missing required field.</p>
 *
 * <h2>HTTP Mapping</h2>
 * <ul>
 *   <li>Status Code: <b>400 Bad Request</b></li>
 *   <li>Error Code: <b>INVALID_TAP_COMMAND</b></li>
 * </ul>
 */
public class InvalidTapCommandException extends FestivalException {

    public InvalidTapCommandException(String message) {
        super(message);
    }

    public InvalidTapCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
