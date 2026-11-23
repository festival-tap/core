package com.festivaltap.core.domain.service;

import com.festivaltap.core.exception.CorruptedEventStreamException;
import com.festivaltap.core.exception.TapNotFoundException;
import com.festivaltap.core.model.PourSession;
import com.festivaltap.core.model.Tap;
import com.festivaltap.core.model.TapEvent;
import com.festivaltap.core.model.TapUsageReport;
import com.festivaltap.core.ports.in.TapUsageReportFetchingUseCase;
import com.festivaltap.core.ports.in.command.FetchUsageReportCommand;
import com.festivaltap.core.ports.out.IdGeneratorPort;
import com.festivaltap.core.ports.out.PricingPort;
import com.festivaltap.core.ports.out.TapEventFetchingPort;
import com.festivaltap.core.ports.out.TapRegistryPort;
import com.festivaltap.core.ports.out.TimeProviderPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java 24 read-side application service implementing {@link TapUsageReportFetchingUseCase}.
 */
public class TapUsageReportService implements TapUsageReportFetchingUseCase {

    private final TapRegistryPort tapRegistry;
    private final TapEventFetchingPort eventFetchingPort;
    private final PricingPort pricingPort;
    private final TimeProviderPort timeProvider;
    private final IdGeneratorPort idGenerator;

    public TapUsageReportService(
            TapRegistryPort tapRegistry,
            TapEventFetchingPort eventFetchingPort,
            PricingPort pricingPort,
            TimeProviderPort timeProvider,
            IdGeneratorPort idGenerator
    ) {
        this.tapRegistry = Objects.requireNonNull(tapRegistry, "tapRegistry must not be null");
        this.eventFetchingPort = Objects.requireNonNull(eventFetchingPort, "eventFetchingPort must not be null");
        this.pricingPort = Objects.requireNonNull(pricingPort, "pricingPort must not be null");
        this.timeProvider = Objects.requireNonNull(timeProvider, "timeProvider must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
    }

    @Override
    public TapUsageReport fetchReportFor(FetchUsageReportCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("FetchUsageReportCommand must not be null");
        }
        String tapId = command.tapId();

        Tap tap = tapRegistry.findById(tapId)
                .orElseThrow(() -> new TapNotFoundException(tapId));

        List<TapEvent> events = eventFetchingPort.streamForTap(tapId);
        List<PourSession> sessions = buildPourSessions(tapId, events, tap.flowVolume());
        Instant snapshot = timeProvider.now();

        return new TapUsageReport(
                UUID.fromString(idGenerator.nextId()),
                snapshot,
                tapId,
                sessions
        );
    }

    private List<PourSession> buildPourSessions(String tapId, List<TapEvent> events, double flowVolume) {
        List<PourSession> sessions = new ArrayList<>();
        Instant openTime = null;

        for (TapEvent event : events) {
            String status = event.status().toLowerCase();
            if ("open".equals(status)) {
                if (openTime != null) {
                    throw new CorruptedEventStreamException(String.format(
                            "Corrupted event stream for tap %s: duplicate OPEN at %s (previous OPEN at %s)",
                            event.tapId(), event.occurredAt(), openTime));
                }
                openTime = event.occurredAt();
            } else if ("close".equals(status)) {
                if (openTime == null) {
                    throw new CorruptedEventStreamException(String.format(
                            "Corrupted event stream for tap %s: CLOSE without OPEN at %s",
                            event.tapId(), event.occurredAt()));
                }
                sessions.add(createSession(tapId, openTime, event.occurredAt(), flowVolume));
                openTime = null;
            }
        }

        if (openTime != null) {
            sessions.add(createSession(tapId, openTime, null, flowVolume));
        }

        return List.copyOf(sessions);
    }

    private PourSession createSession(String tapId, Instant openedAt, Instant closedAt, double flowVolume) {
        double pricePerLiter = pricingPort.getPricePerLiter();
        return new PourSession(tapId, flowVolume, openedAt, closedAt, pricePerLiter);
    }
}
