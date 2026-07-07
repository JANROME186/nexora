# Healthcare Operations Platform — Ordered Development Guide

## Purpose

This guide tells any agent how to work inside this project in a predictable, incremental order.

The project root intentionally contains only numbered folders plus project control files. Agents must use the numbers as the execution sequence.

## Project Folder Order

| Step | Folder | Use |
| --- | --- | --- |
| 00 | `00-intake` | Understand user needs, project brief, assumptions and constraints. |
| 01 | `01-product-definition` | Define product scope, capability map, dependency map and MVP boundary. |
| 02 | `02-domain-definition` | Define domain, actors, processes, business rules, vocabulary and migration model. |
| 03 | `03-architecture` | Define application, data, security, integration, AI and technology architecture. |
| 04 | `04-requirements` | Define stories, UI/mobile flows, business workflows and acceptance criteria. |
| 05 | `05-contracts` | Define OpenAPI, events, adapter and import/export contracts. |
| 06 | `06-delivery` | Define MVP modules, implementation packages, releases and traceability. |
| 07 | `07-implementation` | Place code or generated implementation assets when implementation is colocated. |
| 08 | `08-qa` | Define and store tests, fixtures and quality evidence. |
| 09 | `09-operations` | Define engineering governance, deployment, observability and runbooks. |
| 10 | `10-generated` | Store generated diagrams, indexes and context packages. |
| 99 | `99-legacy` | Preserve old readmes or migration notes only. Do not use for new work. |

## Agent Loading Order

1. `BUSINESS_REQUIREMENT.md`
2. `PROJECT_BRIEF.md`
3. `SOURCE_OF_TRUTH.yaml`
4. `PROJECT_STATE.yaml`
5. `01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
6. `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
7. `02-domain-definition/actors/acm-001/actor-catalog.yaml`
8. `02-domain-definition/processes/hrp-001/healthcare-reference-processes.yaml`
9. `02-domain-definition/business-rules/brm-001/business-rules-catalog.yaml`
10. `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
11. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`

## Development Start Point

The first implementation target is:

`06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

Start by creating the implementation baseline in:

`07-implementation/`

## Incremental Rule

Each implementation iteration must:

1. Select one MVP module.
2. Load the module definition package from `06-delivery`.
3. Confirm actors, rules, processes and contracts.
4. Implement only the selected module slice.
5. Add or update tests in `08-qa`.
6. Add operations notes in `09-operations` when runtime behavior changes.
7. Update `PROJECT_STATE.yaml` and `SOURCE_OF_TRUTH.yaml`.

## Boundary Rule

Do not add new unnumbered folders at the project root.

If a new artifact does not fit the current sequence, add it under the closest numbered folder and update `SOURCE_OF_TRUTH.yaml`.
