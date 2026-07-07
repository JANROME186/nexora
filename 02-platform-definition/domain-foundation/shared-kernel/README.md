# E03.03 — Shared Kernel

The Shared Kernel defines the minimal set of concepts allowed to be reused across bounded contexts.

## Why it exists

Nexora has multiple contexts that need common identifiers, contact values, money values, audit metadata and localization concepts.

Without a governed Shared Kernel, each context would define these concepts differently, causing duplicated logic and inconsistent contracts.

## Main Rule

Only stable, context-neutral concepts belong here.

Business behavior remains inside bounded contexts.
