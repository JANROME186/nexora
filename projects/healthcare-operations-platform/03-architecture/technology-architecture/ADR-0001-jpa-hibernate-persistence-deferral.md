---
id: ADR-0001-jpa-hibernate-persistence-deferral
format: markdown_structured_payload
type: architecture-decision-record
name: JPA/Hibernate adoption deferred; JdbcTemplate-behind-ports remains the accepted persistence baseline
version: 1.0.0
status: accepted
---

# ADR-0001: JPA/Hibernate Adoption Deferred; JdbcTemplate-Behind-Ports Remains the Accepted Persistence Baseline

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: ADR-0001-jpa-hibernate-persistence-deferral
  type: architecture-decision-record
  name: JPA/Hibernate adoption deferred; JdbcTemplate-behind-ports remains the accepted persistence
    baseline
  version: 1.0.0
  status: accepted
  decided_date: 2026-07-26
  owner: backend_platform_team
context:
  origin: Formalizes 03-architecture/technology-architecture/persistence-and-contract-generation-review.md's
    jpa_hibernate_evaluation, produced by HOP-ENT-FOUND-001, as a dedicated, standalone
    architecture decision record. That review already evaluated and deferred JPA/Hibernate;
    this ADR exists so TD-STACK-002's acceptance criterion ("a dedicated ADR exists before any
    JPA migration begins") is satisfied ahead of, not only at, any future migration attempt.
  driver: HOP-HARD-DATA-001 (database, reference data, localization data and persistence
    hardening) is closing or materially reducing every mapped technical-debt item in
    HOP-FINAL-HARDENING, including TD-STACK-002.
  current_state: Every one of the 45+ existing tables is accessed through a
    JdbcTemplate-backed adapter/out/jdbc/JdbcXxxRepository implementing a domain-owned
    repository port, paired with an unconditional adapter/out/memory/InMemoryXxxRepository used
    under every non-local profile. No spring-boot-starter-data-jpa or Hibernate dependency
    exists in pom.xml. SQL is confirmed confined to these adapter classes; it never appears in
    domain or application packages.
decision: Do not adopt JPA/Hibernate in this iteration. Keep JdbcTemplate-behind-ports as the
  accepted backend persistence baseline.
reasoning:
- The current pattern already satisfies the enterprise-product-foundation-standard's actual
  requirement (raw SQL isolated in infrastructure adapters); JPA is a possible modernization,
  not a fix for a defect.
- Migrating would require touching every one of the 45+ existing tables' repositories
  simultaneously (or running two persistence paradigms side by side), a large, high-risk,
  cross-cutting change with no corresponding functional requirement driving it today.
- JdbcTemplate's explicit SQL gives HOP precise control over intentional snapshot/immutability
  denormalizations (diagnostic order patient/doctor/branch snapshots, sale line pricing,
  organization.countries/locales/currencies' name_es_mx/name_en_us columns) that are central to
  several already-closed modules' correctness guarantees; an ORM's identity-map/dirty-checking
  semantics would work against those deliberate denormalizations rather than with them.
alternatives_considered:
- option: Adopt Spring Data JPA + Hibernate ORM platform-wide.
  rejected_because: Full-stack migration blast radius (45+ tables) with no concrete driving
    requirement; would also require re-verifying every closed module's persistence-dependent
    correctness guarantees.
- option: Adopt JPA only for new modules going forward, leaving existing modules on JdbcTemplate.
  rejected_because: Would leave two permanent, divergent persistence paradigms in the same
    codebase indefinitely, doubling the patterns every future engineer must learn, without a
    concrete module today whose aggregate graph actually needs JPA's mapping layer.
consequences:
  positive:
  - No new dependency, learning curve, or migration risk introduced.
  - Existing modules' snapshot/immutability correctness guarantees remain untouched.
  negative:
  - Hand-written JdbcTemplate mapping code continues to carry normal boilerplate/verbosity cost
    per repository; accepted as already priced into the existing baseline.
revisit_trigger: A future module's aggregate graph becomes complex enough (deep, bidirectional
  object graphs) that hand-written JdbcTemplate mapping becomes measurably error-prone or
  verbose, at which point JPA adoption should be re-evaluated for that module specifically, not
  retrofitted platform-wide speculatively.
supersedes_or_extends: 03-architecture/technology-architecture/persistence-and-contract-generation-review.md
  (jpa_hibernate_evaluation section) -- that review's evaluation and reasoning are the source
  this ADR formalizes; both documents remain consistent and this one does not contradict it.
related_technical_debt:
- id: TD-STACK-002
  disposition: closed
  reason: Acceptance criterion was "a dedicated ADR exists before any JPA migration begins";
    this ADR is that record, produced ahead of any migration attempt (none is underway or
    planned), so the criterion is satisfied.
```
