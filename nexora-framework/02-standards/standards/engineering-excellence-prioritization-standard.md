# Engineering Excellence Prioritization Standard

**Artifact ID:** `NXF-ENG-EXCELLENCE-001`
**Status:** Approved
**Machine-readable source:** `engineering-excellence-prioritization-standard.yaml`
**Version:** `1.0.0`

This standard prevents the framework from becoming either too weak or too heavy.

It classifies engineering practices into three groups:

- `P0 minimum enterprise baseline`: mandatory for the changed scope before backlog closure.
- `P1 incremental quality backlog`: important, but managed through technical debt and debt-first execution.
- `P2 contextual or desirable`: valuable when the product, scale, risk or maturity justifies it; does not block normal delivery.

## Rule

Use P0 to protect product safety, security, maintainability and reproducibility.

Use P1 to improve the product continuously without freezing delivery.

Use P2 only when it clearly fits the context.

Any P1 or P2 item can be promoted to P0 when regulatory, security, patient safety, financial
integrity or production reliability risk requires it.

## P0 Examples

- Clean/hexagonal boundaries for changed code.
- DDD for core business capabilities.
- SOLID, DRY, KISS, YAGNI.
- No dead code or unnecessary duplication in changed scope.
- Automated tests for changed behavior.
- OWASP Top 10 controls for exposed surfaces.
- All-severity vulnerability management.
- Structured logging, correlation IDs and audit for sensitive actions.
- Controlled data migrations and referential integrity.
- OpenAPI-first stable contracts and consistent errors.
- Reproducible scripts and local runbook.
- Definition of Ready, Definition of Done and acceptance criteria.

## P1 Examples

- Event-driven architecture for cross-boundary business events.
- CQRS when read/write models diverge.
- Mutation testing for core domain logic.
- Threat modeling for new sensitive flows.
- Distributed tracing and production metrics.
- Infrastructure as Code.
- Backward compatibility tests.
- RAG, MCP, AI observability and prompt evaluation when AI features are in scope.

## P2 Examples

- Event Sourcing unless temporal replay is truly required.
- Chaos testing before production resilience maturity.
- Blue/Green or Canary deployment before deployment maturity requires it.
- Six Sigma unless operational measurement and scale justify it.
- AI features before a modeled business need exists.

Markdown is for people. YAML is the executable source for agents.
