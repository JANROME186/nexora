# ADR-029 — Capability Dependency Map

## Status

Accepted

## Context

BCM-001 defines the Healthcare Operations Platform capability map, but implementation agents need dependency sequencing before they can safely generate services, contracts, portals, tests and deployment work.

The repository also has an approved Architecture Freeze v1.0. This means dependency planning must not redesign bounded contexts or aggregate ownership.

## Decision

Introduce BCM-002 as the source artifact for capability dependencies.

BCM-002 will:

- Cover every capability from BCM-001.
- Map each capability to a dependency profile, MVP phase and product area.
- Reference existing bounded contexts and aggregates.
- Define downstream implementation dependencies.
- Preserve provider, cloud, runtime and AI-agent agnosticism.
- Treat AI as an overlay unless explicitly promoted through governance.

## Consequences

Implementation agents can now plan work by capability without relying on prior chat context.

MVP1 is constrained to the laboratory operations spine: foundation, master data, catalog, care delivery, revenue cycle, clinical operations and results delivery.

Imaging, advanced inventory/quality and AI assistants remain sequenced after MVP1 unless an ADR changes their scope.

## Non-Goals

- BCM-002 does not introduce a new architecture.
- BCM-002 does not replace the context map or aggregate catalog.
- BCM-002 does not define API schemas, database schemas or UI screens.

## Related Artifacts

- `01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
- `02-domain-definition/domain-foundation/context-map/context-map.yaml`
- `02-domain-definition/domain-foundation/aggregates/aggregate-catalog.yaml`
