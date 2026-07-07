# ADR-008: Security and Compliance Architecture as First-Class Product Layer

## Status

Accepted

## Context

Nexora manages sensitive clinical, personal, financial, operational, and identity data. It must support multiple countries, deployment environments, identity providers, and compliance requirements without coupling the core product to one provider or regulation.

## Decision

Security and compliance will be treated as a first-class architecture layer. The product core will provide reusable security, privacy, audit, and compliance capabilities. Country-specific requirements will be implemented through Country Packs.

## Consequences

- Every module must define permissions, audit events, and privacy considerations.
- OpenAPI contracts must include security requirements.
- Sensitive operations must be traceable.
- Country-specific compliance must not leak into core domain logic.
- Security and compliance agents will validate artifacts before implementation.
