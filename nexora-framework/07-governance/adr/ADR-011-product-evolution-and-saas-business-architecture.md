# ADR-011: Product Evolution and SaaS Business Architecture

## Status

Accepted

## Context

Nexora is intended to evolve as a SaaS platform across countries, healthcare verticals, deployment environments, AI providers and commercial models.

Hard-coding modules, plans, country-specific rules or provider-specific behavior would make the platform difficult to evolve.

## Decision

Nexora will adopt a Product Evolution & SaaS Business Architecture based on:

- Product lifecycle states.
- Licensing and entitlement model.
- Feature flags.
- Marketplace extensions.
- Country packs.
- Healthcare packs.
- API compatibility and deprecation policy.
- Progressive rollout strategy.

## Consequences

- Every major capability must include evolution metadata.
- Plan checks must not be hard-coded in domain or UI logic.
- New features must consider rollout and rollback.
- Country and healthcare vertical differences must be extension-based.
- API deprecation must be documented in OpenAPI.
