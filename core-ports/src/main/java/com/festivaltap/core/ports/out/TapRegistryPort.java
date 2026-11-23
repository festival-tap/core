package com.festivaltap.core.ports.out;

import com.festivaltap.core.model.Tap;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for storing and retrieving static tap dispenser configurations.
 *
 * <p>Unlike the event store, which records operational history (open/close events),
 * the registry holds immutable attributes: the tap's unique identifier and its fixed
 * flow volume (litres per second).</p>
 */
public interface TapRegistryPort {

    /**
     * Stores a tap configuration in the registry.
     *
     * @param tap the immutable tap configuration to store; must not be {@code null}
     * @throws IllegalArgumentException if {@code tap} is {@code null}
     */
    void save(Tap tap);

    /**
     * Retrieves a tap configuration by its unique identifier.
     *
     * @param id the tap identifier (non-null, non-blank)
     * @return an {@link Optional} containing the tap if found, otherwise empty
     * @throws IllegalArgumentException if {@code id} is {@code null} or blank
     */
    Optional<Tap> findById(String id);

    /**
     * Returns all registered tap configurations as an unmodifiable list.
     *
     * @return a list of all taps; never {@code null} (empty list if none)
     */
    List<Tap> findAll();

    /**
     * Checks whether a tap with the given identifier exists in the registry.
     *
     * @param id the tap identifier (non-null, non-blank)
     * @return {@code true} if a tap with the given ID exists, otherwise {@code false}
     * @throws IllegalArgumentException if {@code id} is {@code null} or blank
     */
    boolean exists(String id);
}
