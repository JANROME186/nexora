# ADR-031: Model Driven Product Engineering and Capability Packages

Status: Approved

Date: 2026-07-08

## Context

Nexora started as an agent-ready definition repository and HOP started with module-based delivery packages. That was enough to move the project into implementation, but it still allowed delivery work to be framed as manual screens, APIs, DTOs, repositories and repetitive tests.

The target architecture is stronger: Nexora must become a Model Driven Product Engineering system.

In this model, durable business knowledge lives in editable models. Repetitive platform artifacts are generated, compiled or derived from those models.

## Decision

Nexora adopts Model Driven Product Engineering as a governing architecture.

The repository is divided conceptually into:

- Nexora Definition: editable product knowledge and models.
- Nexora Platform: generated, compiled, implemented or runtime artifacts.

Nexora develops Business Capabilities, not standalone modules.

Modules may remain as roadmap groupings, but the primary unit of development is the Business Capability Package.

## Rules

Do not manually write repetitive artifacts that must come from models:

- CRUD scaffolding.
- DTOs.
- Controllers.
- Repositories.
- Swagger documentation.
- SDKs.
- Repetitive documentation.
- Duplicate models.
- Repetitive tests.

Manually author only high-value knowledge:

- Business models.
- Business rules.
- Business processes.
- Domain decisions.
- OpenAPI source contracts.
- Non-generatable custom rule implementation.
- Compiler templates and generators.
- Architecture decisions.

The official execution flow is:

```text
Model -> Compile -> Implement Rules -> Validate -> Release
```

## Consequences

Every HOP capability must become a versionable Capability Package.

The Business Capability Map becomes the master product index. The commercial backlog must be interpreted as capability sequencing, not manual module construction.

Generated artifacts must include traceability to their source models. Manual implementation must be limited to explicitly declared custom implementation points.

Before continuing HOP development after `MVP-MOD-001`, the next backlog item must be reframed from module definition to capability package modeling for `MVP-MOD-002 Diagnostic Catalog` capabilities.
