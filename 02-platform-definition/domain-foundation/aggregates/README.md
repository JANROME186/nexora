# E03.04 — Aggregate Catalog

The Aggregate Catalog defines the official Aggregate Roots of Nexora.

## Purpose

Prevent duplicated ownership and uncontrolled mutations across bounded contexts.

## Core Rule

Every aggregate has one owner and only that bounded context may mutate it.

Other contexts must interact through:
- commands,
- queries,
- published language,
- domain events,
- snapshots,
- anti-corruption layers.
