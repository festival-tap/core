package com.festivaltap.core.domain.service;

import com.festivaltap.core.model.Tap;
import com.festivaltap.core.ports.in.TapCreationUseCase;
import com.festivaltap.core.ports.in.TapListUseCase;
import com.festivaltap.core.ports.in.command.CreateTapCommand;
import com.festivaltap.core.ports.out.IdGeneratorPort;
import com.festivaltap.core.ports.out.TapRegistryPort;

import java.util.List;
import java.util.Objects;

/**
 * Pure Java 24 application service implementing {@link TapCreationUseCase} and {@link TapListUseCase}.
 */
public class TapService implements TapCreationUseCase, TapListUseCase {

    private final TapRegistryPort tapRegistry;
    private final IdGeneratorPort idGenerator;

    public TapService(TapRegistryPort tapRegistry, IdGeneratorPort idGenerator) {
        this.tapRegistry = Objects.requireNonNull(tapRegistry, "tapRegistry must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
    }

    @Override
    public Tap create(CreateTapCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("CreateTapCommand must not be null");
        }
        String id = idGenerator.nextId();
        Tap tap = new Tap(id, command.flowVolume());
        tapRegistry.save(tap);
        return tap;
    }

    @Override
    public List<Tap> findAll() {
        return List.copyOf(tapRegistry.findAll());
    }
}
