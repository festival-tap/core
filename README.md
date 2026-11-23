# festival-tap/core

The pure **Java 24** hexagon core of the **festival-tap** self-service beer dispenser platform.

This repository houses all domain models, immutable event-sourced history, business rules (pricing, flow calculations, state transition validation), inbound use cases, and outbound port contracts with **zero framework or cloud SDK dependencies**.

---

## Architectural Design: Two-Module Split (`core-ports` & `core-domain`)

To maintain strict Hexagonal Architecture purity while testing domain behavior directly against the real JDK persistence adapter from `festival-tap/persistence` (`Persistence.jdk()` wrapping `sugarcoat-labs/persistence-engines-core`)—without circular dependencies or duplicated adapter wrappers—this repository is structured into two isolated Maven / JPMS modules:

```text
  festival-tap/core-ports        (Domain models, exceptions, inbound & outbound ports, commands)
          ▲
          │  (compile scope)
          │
  festival-tap/persistence       (Wraps sugarcoat-labs engines once -> exposes Persistence.jdk() & Persistence.aws())
          ▲
          │  (test scope)
          │
  festival-tap/core-domain       (Domain services & JUnit 5 tests backed by Persistence.jdk())
```

### Why This Split Exists
1. **Zero Duplicated Engine Wrappers:** `sugarcoat-labs` persistence engines are wrapped once inside `festival-tap/persistence`.
2. **Real JDK Engine Testing in `core`:** `core-domain` imports `festival-tap/persistence` strictly in `<scope>test</scope>` so domain services are verified against the exact same `Persistence.jdk()` adapter used in `ingress` Pact tests and `cloud-deploy` E2E suites—with zero throwaway fakes.
3. **Zero Framework or Cloud Leakage:** Neither `core-ports` nor `core-domain` ever imports Spring, Micronaut, Quarkus, HTTP servers, serialization annotations, or AWS SDK classes.
4. **Acyclic Build & Release Graph:** Separating port and model contracts (`core-ports`) from domain service implementations and tests (`core-domain`) eliminates the `core <-> persistence` Maven cycle.

---

## Module Breakdown

### 1. `core-ports` (`com.festivaltap:core-ports` / JPMS: `com.festivaltap.core.ports`)
The dependency-free contract and API surface of the hexagon (`src/main` compiles strictly against `java.base`):
* **Domain Models (`com.festivaltap.core.model`):** Immutable Java 24 `record` value objects (`Tap`, `TapEvent`, `PourSession`, `TapUsageReport`).
* **Domain Exceptions (`com.festivaltap.core.exception`):** `FestivalException` hierarchy (`InvalidTapCommandException`, `TapNotFoundException`, `TapAlreadyOpenException`, `TapAlreadyClosedException`, `CorruptedEventStreamException`) and `SequenceConflictException`.
* **Inbound Use-Case Interfaces (`com.festivaltap.core.ports.in` & `command`):**
    * `TapCreationUseCase` (`CreateTapCommand`) — Register a new tap dispenser asset.
    * `TapEventIngressUseCase` (`TapEventCommand`) — Process `"open"` / `"close"` operational state transitions.
    * `TapListUseCase` — Retrieve all registered tap configurations.
    * `TapUsageReportFetchingUseCase` (`FetchUsageReportCommand`) — Materialize pour sessions and spending totals.
* **Outbound Port Interfaces (`com.festivaltap.core.ports.out`):**
    * `TapRegistryPort` — Tap configuration storage and lookup.
    * `TapEventFetchingPort` & `TapEventPublishingPort` — Append-only event stream persistence and replay.
    * `PricingPort` — Current beer price per litre (€/L).
    * `TimeProviderPort` & `IdGeneratorPort` — Deterministic time and identifier generation.

### 2. `core-domain` (`com.festivaltap:core-domain` / JPMS: `com.festivaltap.core.domain`)
The pure business logic and behavioral verification layer (`requires transitive com.festivaltap.core.ports`):
* **Domain Services (`com.festivaltap.core.domain.service`):** Pure constructor-injected implementations:
    * `TapService` — Implements `TapCreationUseCase` and `TapListUseCase`.
    * `TapEventIngressService` — Implements `TapEventIngressUseCase`, deriving state from event history and enforcing monotonic optimistic concurrency (`nextSeq = currentSeq + 1`).
    * `TapUsageReportService` — Implements `TapUsageReportFetchingUseCase`, pairing `"open"` / `"close"` events into `PourSession` entries and calculating dispensed volume (`durationSeconds × flowVolume`) and cost (`volume × pricePerLiter`).
* **Deterministic Behavioral Test Suite (`src/test`):** JUnit 5 tests wiring `core-domain` services to `Persistence.jdk()` and fixed `TimeProviderPort` / `IdGeneratorPort` implementations.

---

## Repository Structure

```text
festival-tap/core/
├── pom.xml                         # Parent POM (Java 24, JUnit 5, GitHub Packages distribution)
├── core-ports/
│   ├── pom.xml                     # Zero external dependencies (java.base only)
│   └── src/main/java/
│       ├── module-info.java        # module com.festivaltap.core.ports
│       └── com/festivaltap/core/
│           ├── model/              # Tap, TapEvent, PourSession, TapUsageReport
│           ├── exception/          # Domain & optimistic concurrency exceptions
│           └── ports/
│               ├── in/             # Inbound use-case interfaces & command/ records
│               └── out/            # Outbound SPI port interfaces
└── core-domain/
    ├── pom.xml                     # Compile: core-ports | Test: festival-tap/persistence, JUnit 5
    ├── src/main/java/
    │   ├── module-info.java        # module com.festivaltap.core.domain
    │   └── com/festivaltap/core/domain/
    │       └── service/            # TapService, TapEventIngressService, TapUsageReportService
    └── src/test/java/
        └── com/festivaltap/core/domain/
            └── ...                 # JUnit 5 behavioral tests using Persistence.jdk()
```

---

## Build & Release Lifecycle

Because `festival-tap/persistence` sits between `core-ports` and `core-domain`, releases follow a strict linear order:

1. **Publish `core-ports`:**
   ```bash
   mvn -pl core-ports clean deploy
   ```
2. **Build & Publish `festival-tap/persistence`:**
   `persistence` compiles against the published `com.festivaltap:core-ports` version and publishes `com.festivaltap:persistence` to GitHub Packages.
3. **Verify & Publish `core-domain`:**
   ```bash
   mvn -pl core-domain clean deploy
   ```
   *(When only `core-domain` internal logic changes without modifying `core-ports`, `mvn -pl core-domain clean deploy` runs immediately using the existing `persistence` artifact.)*

---

## Guardrails for Contributors & AI Coding Agents

* **No External Imports in `src/main`:** Both `core-ports` and `core-domain` (`src/main`) must compile using only the Java 24 standard library (`java.base`) and `core-ports` (for `core-domain`). Never add Jackson, DynamoDB, HTTP, or logging framework dependencies.
* **No `Instant.now()` or `UUID.randomUUID()` in Domain Services:** Always inject `TimeProviderPort` and `IdGeneratorPort` via constructor parameters so report snapshots and generated IDs remain 100% deterministic.
* **Immutable Types Only:** All domain models, commands, and events must be Java `record` types.
* **Port Changes Require SemVer Discipline:** Any breaking change to `core-ports` requires incrementing the `MAJOR` version and propagating through `persistence` before releasing `core-domain`.
