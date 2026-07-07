# Actor Catalog Validator

## Purpose

Validate ACM-001 before implementation agents generate IAM, authorization, API or UI work.

## Inputs

- `02-platform-definition/actors/acm-001/actor-catalog.yaml`
- `02-platform-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `05-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`

## Required Checks

1. `artifact.id` must equal `ACM-001`.
2. Every actor must have `id`, `name`, `group`, `description`, `primary_portals`, `mvp_modules`, `capabilities`, `permissions`, `data_scope` and `audit_level`.
3. Every actor group must exist in `actor_groups`.
4. Every capability reference must exist in BCM-001.
5. Every MVP module reference must exist in HOP-MVP-FWK-001.
6. Every data scope must exist in `access_model.scopes`.
7. `audit_level` must be one of `low`, `medium`, `high`, `critical`.
8. Every minimum MVP role must map to at least one actor or implementation role.

## Failure Handling

Unknown actor groups, capability ids, module ids or scopes block implementation.
