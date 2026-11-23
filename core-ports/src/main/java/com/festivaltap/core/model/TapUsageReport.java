package com.festivaltap.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record TapUsageReport(
        UUID reportId,
        Instant snapshotAt,
        String tapId,
        List<PourSession> pourSessionList
) {

    public TapUsageReport {
        if (tapId == null || tapId.isBlank()) {
            throw new IllegalArgumentException("Tap ID cannot be null or empty inside a usage report.");
        }
        Objects.requireNonNull(pourSessionList, "Pour session list cannot be null.");
        Objects.requireNonNull(snapshotAt, "Snapshot timestamp cannot be null.");
        Objects.requireNonNull(reportId, "Report ID cannot be null.");

        boolean openFound = false;
        for (int i = 0; i < pourSessionList.size(); i++) {
            PourSession session = pourSessionList.get(i);
            if (session.isOpen()) {
                if (openFound) {
                    throw new IllegalArgumentException("Multiple open sessions found in report.");
                }
                if (i != pourSessionList.size() - 1) {
                    throw new IllegalArgumentException("Open session must be the last element in the list.");
                }
                openFound = true;
            }
        }

        pourSessionList = List.copyOf(pourSessionList);
    }

    public double amount() {
        return pourSessionList.stream()
                .mapToDouble(session -> session.totalSpent(snapshotAt))
                .sum();
    }

    public boolean isTapActive() {
        return pourSessionList.stream().anyMatch(PourSession::isOpen);
    }
}
