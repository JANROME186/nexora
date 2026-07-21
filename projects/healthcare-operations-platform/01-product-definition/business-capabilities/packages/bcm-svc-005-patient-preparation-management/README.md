# BCM-SVC-005 Patient Preparation Management Capability Package

Human-readable companion for the Patient Preparation Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-005
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration`
- Primary aggregate: `TestDefinition` (AGG-006)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: High

## Purpose

Defines patient preparation instructions (fasting, medication, activity, timing,
hydration) associated with tests and panels, and publishes them as localized
patient-facing guidance.

## Mobile note

Unlike the other MVP-MOD-002 packages, mobile scope is `deferred` rather than
`not_required`, because published preparation guidance is patient-facing and will appear
read-only in the patient mobile app once patient channels are delivered
(COM-MOD-009 / MVP-MOD-004). See `mobile-model.yaml`.

## COM-MOD-011 reuse

A new `getPublishedPreparationSnapshot` operation (mirroring BCM-SVC-001/002/003) is exposed,
unauthenticated and rate-limited, to the COM-MOD-011 Public Website and Digital Growth module.
No new capability package, aggregate or schema was created for this reuse; see
`traceability.yaml`'s `cross_module_reuse` entry.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are generated
outputs declared in `generation-plan.yaml`. Custom rules (assignment target validation,
immutable versioning, patient-facing snapshot projection) are implemented in later
backlog items.
