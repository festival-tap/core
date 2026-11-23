package com.festivaltap.core.ports.in;

import com.festivaltap.core.model.Tap;
import com.festivaltap.core.ports.in.command.CreateTapCommand;

/**
 * Inbound port for registering a new tap dispenser asset.
 */
public interface TapCreationUseCase {

    /**
     * Creates a new tap dispenser from the given validated command.
     */
    Tap create(CreateTapCommand command);
}
