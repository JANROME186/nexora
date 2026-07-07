# ADR-030 — Actor Catalog and Access Scopes

## Status

Accepted

## Context

The Healthcare Operations Platform MVP requires identity, access control, portals, audit and clinical traceability. Implementation cannot start safely without a stable actor and access scope model.

## Decision

Introduce ACM-001 as the source of truth for MVP actors, role intentions, data scopes, permissions and audit levels.

Actors are platform-agnostic. Implementations may use JWT claims, groups, roles, policies or ACLs, but they must trace back to ACM-001.

## Consequences

- MVP endpoints must map to actors and permissions.
- Audit expectations are known before code generation.
- Patient, doctor, staff and system access are separated from the start.
- Country-specific actor variants remain country-pack extensions.

## Related Artifacts

- `02-domain-definition/actors/acm-001/actor-catalog.yaml`
- `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
