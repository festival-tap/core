package com.festivaltap.core.ports.in;

import com.festivaltap.core.ports.in.command.TapEventCommand;

/**
 * Inbound port for processing operational state transitions ({@code "open"} / {@code "close"}).
 */
public interface TapEventIngressUseCase {

    /**
     * Processes a tap state change command, validates it against the current operational
     * state derived from the event stream, and appends a new immutable event.
     */
    void registerTapState(TapEventCommand command);
}
