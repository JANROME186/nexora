# ADR-027 — Business Capability Map as Product Index

## Status

Approved

## Context

Nexora requires a single product index to prevent module-driven development and duplicated functionality.

## Decision

The Business Capability Map (BCM-001) becomes the official product index.

All future capabilities, user stories, APIs, screens, mobile flows, AI functions and tests must trace back to one or more capabilities.

## Consequences

- No standalone modules are allowed.
- Product development is capability-driven.
- The backlog becomes a derived artifact.
