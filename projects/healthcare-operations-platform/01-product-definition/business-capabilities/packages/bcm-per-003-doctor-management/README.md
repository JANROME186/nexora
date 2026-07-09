# BCM-PER-003 Doctor Management Capability Package

Human-readable companion for the Doctor Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PER-003
- Domain: DOM-02 People
- Bounded context: `medical-staff`
- Primary aggregate: `Doctor` (AGG-005)
- Roadmap group: MVP-MOD-003 People and Clinical Master Data
- Priority: High

## Purpose

Owns the Doctor aggregate lifecycle including professional identity,
credentials, specialty assignments and doctor portal readiness baseline.
Publishes the authoritative `DoctorSnapshot` consumed by order intake,
laboratory results and imaging.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | Doctor aggregate entities, invariants, snapshot |
| `business-rules.yaml` | Numbered rules RN-001..RN-009 |
| `processes.yaml` | Registration, credential verification, updates, suspension, retirement, portal readiness |
| `events.yaml` | Domain and integration events with published language |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal screens and states |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom implementation
covers duplicate-detection integration, aggregate boundary enforcement,
activation cascade on credential verification, credential expiration watcher,
eligibility filtering and portal access baseline preparation without granting
portal identity.
