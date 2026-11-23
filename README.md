# festival-tap/core

The pure **Java 24** hexagon core of the **festival-tap** self-service beer dispenser platform.

This repository houses all domain models, immutable event-sourced history, business rules (pricing, flow calculations, `0.15 L` safety auto-shutoff), inbound use cases, and outbound port contracts with **zero framework or cloud SDK dependencies**.

---

## Architectural Design: Two-Module Split (`core-ports` & `core-domain`)

To maintain strict Hexagonal Architecture purity while testing domain behavior directly against the real JDK persistence adapter from `festival-tap/persistence` (`Persistence.jdk()` wrapping `sugarcoat-labs/persistence-engines-core`)—without circular dependencies or duplicated adapter wrappers—this repository is structured into two isolated Maven / JPMS modules:

```text
  festival-tap/core-ports        (Domain models, sealed events, outbound ports, domain exceptions)
          ▲
          │  (compile scope)
          │
  festival-tap/persistence       (Wraps sugarcoat-labs engines once -> exposes Persistence.jdk() & Persistence.aws())
          ▲
          │  (test scope)
          │
  festival-tap/core-domain       (Inbound use cases, domain services & JUnit 5 tests backed by Persistence.jdk())
```

### Why This Split Exists
1. **Zero Duplicated Engine Wrappers:** `sugarcoat-labs` persistence engines are wrapped once inside `festival-tap/persistence`.
2. **Real JDK Engine Testing in `core`:** `core-domain` imports `festival-tap/persistence` strictly in `<scope>test</scope>` so domain services are verified against the exact same `Persistence.jdk()` adapter used in `ingress` Pact tests and `cloud-deploy` E2E suites—with zero throwaway fakes.
3. **Zero Framework or Cloud Leakage:** Neither `core-ports` nor `core-domain` ever imports Spring, Micronaut, Quarkus, HTTP servers, serialization annotations, or AWS SDK classes.
4. **Acyclic Build & Release Graph:** Separating port contracts (`core-ports`) from domain service tests (`core-domain`) eliminates the `core <-> persistence` Maven cycle.

---

## Module Breakdown

### 1. `core-ports` (`com.festivaltap:core-ports`)
The dependency-free inner contract of the hexagon (`src/main` has **zero external dependencies**):
* **Domain Models & Value Objects:** Immutable Java 24 `record` types (`TapRegistration`, `TapState`, `PourSummary`, `SpendingReport`).
* **Domain Events:** Sealed event hierarchy (`TapEvent` permitting `TapRegistered`, `PourStarted`, `PourClosed`, and `CloseReason`).
* **Outbound Port Interfaces:**
    * `TapRegistryPort` — Tap registration and lookup.
    * `TapEventPublishingPort` & `TapEventFetchingPort` — Append-only event stream persistence and replay.
    * `ClockPort` & `IdGeneratorPort` — Deterministic time and identifier generation.
    * `TelemetryPort` — Domain-level operational observability events.
* **Domain Exceptions:** `DuplicateTapException`, `SequenceConflictException`, `TapNotFoundException`, `TapAlreadyOpenException`, `TapNotOpenException`.

### 2. `core-domain` (`com.festivaltap:core-domain`)
The pure business logic and behavioral verification layer:
* **Inbound Use-Case Interfaces (`src/main`):** `RegisterTapUseCase`, `OpenTapUseCase`, `CloseTapUseCase`, `QueryTapsUseCase`, `SpendingReportUseCase`.
* **Domain Services (`src/main`):** Pure constructor-injected implementations (`TapCommandService`, `TapQueryService`) enforcing:
    * Event-sourced state reconstruction by replaying `TapEvent` streams.
    * Optimistic concurrency sequence incrementation (`expectedSequence = currentSequence + 1`).
    * Live volume and cost calculation (`elapsedSeconds × flowRateLitersPerSec × pricePerLiter`).
    * Automatic safety shut-off clamping at **0.15 L** (`CloseReason.AUTO_SHUTOFF_LIMIT`).
* **Deterministic Behavioral Test Suite (`src/test`):** JUnit 5 tests wiring `core-domain` services to `Persistence.jdk()` and fixed `ClockPort` / `IdGeneratorPort` implementations.

---

## Repository Structure

```text
festival-tap/core/
├── pom.xml                         # Parent POM (Java 24, JUnit 5, GitHub Packages distribution)
├── core-ports/
│   ├── pom.xml                     # Zero external dependencies
│   └── src/main/java/
│       └── com/festivaltap/core/ports/
│           ├── model/              # Domain records & sealed TapEvent
│           ├── out/                # Outbound port interfaces
│           └── exception/          # Domain exceptions
└── core-domain/
    ├── pom.xml                     # Compile: core-ports | Test: festival-tap/persistence, JUnit 5
    ├── src/main/java/
    │   └── com/festivaltap/core/domain/
    │       ├── in/                 # Inbound use-case interfaces
    │       └── service/            # Pure domain services (TapCommandService, TapQueryService)
    └── src/test/java/
        └── com/festivaltap/core/domain/
            └── ...                 # JUnit 5 tests using Persistence.jdk()
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
* **No `Instant.now()` or `UUID.randomUUID()` in Domain Services:** Always inject `ClockPort` and `IdGeneratorPort` via constructor parameters so time-based pour calculations and auto-shutoff limits remain 100% deterministic.
* **Immutable Types Only:** All domain models, commands, queries, and events must be Java `record` types or `sealed` interfaces.
* **Port Changes Require SemVer Discipline:** Any breaking change to `core-ports` requires incrementing the `MAJOR` version and propagating through `persistence` before releasing `core-domain`.
