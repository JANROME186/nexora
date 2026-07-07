# Nexora Meta Model (NMM)

The Nexora Meta Model defines the computable structure of Nexora's product knowledge.

Nexora artifacts must be understandable by humans and consumable by tools, validators and AI agents.

Every major artifact should have two views:

- Markdown (`.md`) for human review.
- YAML or JSON (`.yaml` / `.json`) for automation.

The NMM is the foundation for:

- Product Knowledge Graph.
- Impact analysis.
- Specification-driven development.
- Contract-first APIs.
- Automated code generation.
- Automated test generation.
- Agent-agnostic engineering workflows.

## Core Artifact Types

| Type | Prefix | Purpose |
|---|---:|---|
| Business Capability | CAP | Business capability reusable across channels |
| Business Process | BPR | End-to-end process or subprocess |
| Business Rule | BR | Rule or invariant that governs behavior |
| Domain | DOM | DDD bounded context or domain area |
| Domain Entity | ENT | Domain/data entity |
| Domain Event | EVT | Event emitted by the domain |
| User Story | US | Product backlog item |
| API Contract | API | OpenAPI contract |
| UI Screen | UI | Web screen or flow |
| Mobile Screen | MOB | Mobile screen or flow |
| QA Test | QA | Test case or test suite |
| Playbook | PB | Repeatable execution guide |
| ADR | ADR | Architecture decision record |
| RFC | RFC | Request for comments |

## Golden Rule

No implementation artifact should exist without traceability to a business capability, business process, rule, story, contract and validation artifact when applicable.
