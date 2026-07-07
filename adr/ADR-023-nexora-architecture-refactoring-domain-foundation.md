# ADR-023: Nexora Architecture Refactoring and Domain Foundation

## Status
Approved

## Context
Nexora accumulated multiple iterations of architecture, capabilities, prompts, platform engineering and AI governance. Some early terms became obsolete after adopting Agent Agnostic, Anywhere First and Compute Agnostic principles.

## Decision
Before adding more capabilities, Nexora will consolidate its domain foundation and refactor the specification.

The official next phase is E03 Domain Foundation. The first deliverable is the Bounded Context Catalog.

## Consequences
- Bounded contexts become mandatory before modeling additional capabilities.
- Deprecated provider-specific concepts are replaced by agnostic abstractions.
- YAML source artifacts become preferred source of truth for agents.
- Generated artifacts must be separated from editable source artifacts.
- Data Migration & Portability becomes a platform bounded context.
