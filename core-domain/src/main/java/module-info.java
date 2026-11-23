/**
 * Implements the pure business logic and use cases of the {@code festival-tap} hexagon.
 *
 * <p>This JPMS module depends exclusively on {@code java.base} and
 * {@link com.festivaltap.core.ports} at compile and runtime, ensuring complete isolation from
 * HTTP frameworks, dependency-injection containers, and cloud infrastructure.</p>
 *
 * <h2>Core Responsibilities</h2>
 * <ul>
 *   <li>Registering and querying physical tap dispenser assets via {@code TapService}.</li>
 *   <li>Validating operational state transitions ({@code "open"} / {@code "close"}) and
 *       enforcing optimistic concurrency via {@code TapEventIngressService}.</li>
 *   <li>Reconstructing chronological pour sessions and calculating dispensed volume and
 *       spending totals via {@code TapUsageReportService}.</li>
 * </ul>
 *
 * <h2>Exported Packages</h2>
 * <ul>
 *   <li>{@link com.festivaltap.core.domain.service} — Pure constructor-injected domain
 *       service implementations ({@code TapService}, {@code TapEventIngressService},
 *       {@code TapUsageReportService}).</li>
 * </ul>
 */
module com.festivaltap.core.domain {
    requires transitive com.festivaltap.core.ports;

    exports com.festivaltap.core.domain.service;
}
