package com.festivaltap.core.domain.service;

import com.festivaltap.core.exception.TapAlreadyClosedException;
import com.festivaltap.core.exception.TapAlreadyOpenException;
import com.festivaltap.core.exception.TapNotFoundException;
import com.festivaltap.core.model.TapEvent;
import com.festivaltap.core.ports.in.TapEventIngressUseCase;
import com.festivaltap.core.ports.in.command.TapEventCommand;
import com.festivaltap.core.ports.out.IdGeneratorPort;
import com.festivaltap.core.ports.out.TapEventFetchingPort;
import com.festivaltap.core.ports.out.TapEventPublishingPort;
import com.festivaltap.core.ports.out.TapRegistryPort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java 24 write-side application service implementing {@link TapEventIngressUseCase}.
 */
public class TapEventIngressService implements TapEventIngressUseCase {

    private final TapRegistryPort tapRegistry;
    private final TapEventFetchingPort eventFetchingPort;
    private final TapEventPublishingPort eventPublishingPort;
    private final IdGeneratorPort idGenerator;

    public TapEventIngressService(
            TapRegistryPort tapRegistry,
            TapEventFetchingPort eventFetchingPort,
            TapEventPublishingPort eventPublishingPort,
            IdGeneratorPort idGenerator
    ) {
        this.tapRegistry = Objects.requireNonNull(tapRegistry, "tapRegistry must not be null");
        this.eventFetchingPort = Objects.requireNonNull(eventFetchingPort, "eventFetchingPort must not be null");
        this.eventPublishingPort = Objects.requireNonNull(eventPublishingPort, "eventPublishingPort must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
    }

    @Override
    public void registerTapState(TapEventCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("TapEventCommand must not be null");
        }

        if (!tapRegistry.exists(command.tapId())) {
            throw new TapNotFoundException(command.tapId());
        }

        List<TapEvent> events = eventFetchingPort.streamForTap(command.tapId());
        String currentStatus = deriveCurrentStatus(events);
        String requestedStatus = command.normalizedStatus();

        if ("open".equals(requestedStatus) && "open".equals(currentStatus)) {
            throw new TapAlreadyOpenException(command.tapId());
        }
        if ("close".equals(requestedStatus) && "close".equals(currentStatus)) {
            throw new TapAlreadyClosedException(command.tapId());
        }

        long currentSeq = eventFetchingPort.getCurrentSequenceNumber(command.tapId());
        long nextSeq = currentSeq + 1;

        TapEvent event = new TapEvent(
                nextSeq,
                UUID.fromString(idGenerator.nextId()),
                command.tapId(),
                requestedStatus,
                command.occurredAt()
        );

        eventPublishingPort.append(event, nextSeq);
    }

    private String deriveCurrentStatus(List<TapEvent> events) {
        if (events.isEmpty()) {
            return "close";
        }
        return events.getLast().status().toLowerCase();
    }
}
