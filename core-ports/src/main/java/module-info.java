/**
 * Defines the dependency-free contract and API surface of the {@code festival-tap}
 * Hexagonal Architecture.
 *
 * <p>This JPMS module compiles strictly against {@code java.base} with zero framework,
 * serialization, or cloud SDK dependencies. It establishes the shared boundary between
 * the pure domain logic ({@code com.festivaltap.core.domain}) and external adapters
 * such as {@code festival-tap/persistence}, {@code festival-tap/ingress}, and
 * {@code festival-tap/adapters}.</p>
 *
 * <h2>Exported Packages</h2>
 * <ul>
 *   <li>{@link com.festivaltap.core.model} — Immutable domain value objects
 *       ({@code Tap}, {@code TapEvent}, {@code PourSession}, {@code TapUsageReport}).</li>
 *   <li>{@link com.festivaltap.core.exception} — Domain-specific business rule,
 *       stream integrity, and optimistic concurrency exceptions.</li>
 *   <li>{@link com.festivaltap.core.ports.in} — Inbound driving use-case interfaces
 *       for tap management, event ingress, listing, and usage reporting.</li>
 *   <li>{@link com.festivaltap.core.ports.in.command} — Validated immutable command
 *       records passed into inbound use cases.</li>
 *   <li>{@link com.festivaltap.core.ports.out} — Outbound driven port interfaces for
 *       tap persistence, append-only event streaming, pricing, clock, and ID generation.</li>
 * </ul>
 */
module com.festivaltap.core.ports {
        exports com.festivaltap.core.model;
        exports com.festivaltap.core.exception;
        exports com.festivaltap.core.ports.in;
        exports com.festivaltap.core.ports.in.command;
        exports com.festivaltap.core.ports.out;
}