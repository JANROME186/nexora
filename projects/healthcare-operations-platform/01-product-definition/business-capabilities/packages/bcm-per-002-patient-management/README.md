# BCM-PER-002 Patient Management Capability Package

Human-readable companion for the Patient Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PER-002
- Domain: DOM-02 People
- Bounded context: `patient-management`
- Primary aggregate: `Patient` (AGG-001)
- Roadmap group: MVP-MOD-003 People and Clinical Master Data
- Priority: Critical

## Purpose

Owns the Patient aggregate lifecycle including identity, contact, address,
consents, documents, emergency contacts and representative relationships.
Publishes the authoritative `PatientSnapshot` consumed by orders, laboratory,
results, imaging and billing.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | Patient aggregate entities, invariants, snapshot |
| `business-rules.yaml` | Numbered rules RN-001..RN-010 |
| `processes.yaml` | Registration, update, consent, representative, merge, deactivation |
| `events.yaml` | Domain and integration events with published language |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal screens and states |
| `mobile-model.yaml` | Mobile scope (patient_profile_later) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom implementation
covers duplicate-detection integration, aggregate boundary enforcement, patient
merge with projection rewiring, representative time-bound authorization and
consent append-only revocation.
