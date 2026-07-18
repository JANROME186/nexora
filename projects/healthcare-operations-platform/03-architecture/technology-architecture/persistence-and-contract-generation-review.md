# HOP Persistence Architecture and Contract-First Generation Review

Machine-readable source: `persistence-and-contract-generation-review.yaml`. Produced by
`HOP-ENT-FOUND-001`.

## Persistence architecture

**Current**: plain `JdbcTemplate` (no JPA/Hibernate), strict hexagonal pattern — SQL confined to
`adapter/out/jdbc/JdbcXxxRepository` classes implementing domain-owned repository ports, with an
unconditional `InMemoryXxxRepository` sibling. Spot-checked: no SQL exists in domain/application
packages anywhere in the codebase.

**JPA/Hibernate**: evaluated, **not adopted this iteration**. The existing pattern already
satisfies the standard's actual requirement (SQL isolated behind adapters); migrating 45+ tables
and their repositories simultaneously would be a large, high-risk, cross-cutting change out of
scope for a foundation-alignment slice, and JdbcTemplate gives precise control over the
intentional snapshot/immutability denormalizations (see `normalization-report.md`) that ORM
identity-map semantics would fight against. Registered as **TD-STACK-002** (revisit if a future
module needs complex object-graph persistence).

**Migrations**: still additive `schema.sql`, no Flyway/Liquibase — already tracked by the
pre-existing `TD-STACK-001`.

## Contract-first / OpenAPI generation

**Current**: SpringDoc auto-generates a live OpenAPI document from annotated controllers;
separately, 34 hand-authored `openapi-source.yaml` files (one per capability package) are the MDPE
source of truth, and controllers are hand-written to match them by convention (each controller's
Javadoc states which contract it renders). No drift found in this review's spot check.

| Tool | Decision | Reason |
|---|---|---|
| OpenAPI Generator | Not adopted this iteration | Regenerating ~34 already-closed packages' controllers is high-risk; recommend starting with client generation, not server |
| SpringDoc | Keep as-is | Already working, already used by ZAP API scans |
| MapStruct | Not adopted | Mapping today is small, explicit, legible |
| Lombok | Not adopted | Domain model is built on Java 21 records — no getter/setter boilerplate to eliminate |

**Recommended next step**: evaluate OpenAPI-Generator-based TypeScript client generation for the
employee portal first (lower risk, single consumer) before considering server-side generation.
Registered as **TD-STACK-003**.

## Closure gate compliance

Both persistence strategy and contract-generation posture are explicitly decided with stated
reasoning and revisit triggers, not left implicit.
