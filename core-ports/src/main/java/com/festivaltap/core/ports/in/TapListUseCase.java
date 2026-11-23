package com.festivaltap.core.ports.in;

import com.festivaltap.core.model.Tap;
import java.util.List;

/**
 * Inbound port for retrieving all registered tap configurations.
 */
public interface TapListUseCase {

    /**
     * Returns an unmodifiable list of all registered tap configurations.
     */
    List<Tap> findAll();
}
