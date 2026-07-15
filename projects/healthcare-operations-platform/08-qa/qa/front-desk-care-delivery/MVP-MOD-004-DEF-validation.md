# MVP-MOD-004-DEF Validation Evidence

Human-readable companion for `MVP-MOD-004-DEF-validation.yaml`.

## Scope

- Backlog item: MVP-MOD-004-DEF (Capability package models for Front Desk and Care Delivery)
- Module: MVP-MOD-004 Front Desk and Care Delivery (Release REL-001)
- Execution flow stage: model
- Business requirement version: v0.68.0 (impact assessment not required)

## Result summary

All five Front Desk and Care Delivery capability packages were modeled with
the full required artifact set (14 artifacts each, 70 total). All
validations passed and no blocking gaps remain.

| Capability | Package | Artifacts | Bounded context | Primary aggregate | Mobile scope |
| --- | --- | --- | --- | --- | --- |
| BCM-ATT-001 Appointment Scheduling | bcm-att-001-appointment-scheduling | 14 | orders-samples (secondary organization-management) | AGG-007 (owned by BCM-LAB-001) | check_in_later |
| BCM-ATT-003 Reception Management | bcm-att-003-reception-management | 14 | orders-samples (secondary patient-management) | AGG-007 (owned by BCM-LAB-001) | not_required |
| BCM-ATT-004 Admission Management | bcm-att-004-admission-management | 14 | orders-samples | AGG-007 (owned by BCM-LAB-001) | not_required |
| BCM-ATT-006 Quotation Management | bcm-att-006-quotation-management | 14 | cash-sales (secondary catalog-test-configuration) | none (standalone QuotationRequest; Sale AGG-010 deferred) | not_required |
| BCM-LAB-001 Diagnostic Order Management | bcm-lab-001-diagnostic-order-management | 14 | orders-samples | AGG-007 DiagnosticOrder (owner) | not_required |

## Design decisions worth noting

- **Single aggregate owner, four orchestrators.** BCM-LAB-001 owns the
  `DiagnosticOrder` aggregate (AGG-007) end to end. BCM-ATT-001, BCM-ATT-003
  and BCM-ATT-004 model their own process-level entities (`AppointmentSlot`,
  `ReceptionVisit`, `AdmissionRequest`) and delegate every order mutation to
  BCM-LAB-001 commands — the same orchestrator/owner pattern already
  validated for BCM-ATT-002 around BCM-PER-002 in MVP-MOD-003.
- **Immutable snapshots.** `DiagnosticOrder` captures `PatientSnapshot`,
  `DoctorSnapshot`, `BranchSnapshot`, `CatalogSnapshot` and
  `OrderPricingSnapshot` value objects at order time, satisfying the
  backlog requirement that orders never depend on live master-data
  mutation.
- **Quotation without an unbuilt Sale aggregate.** BCM-ATT-006 depends on
  catalog (BCM-SVC-001/002/003) and price lists (BCM-SVC-009) as required,
  and owns its own `QuotationRequest` process aggregate rather than taking a
  forward dependency on the `Sale` aggregate (AGG-010), which belongs to the
  not-yet-built MVP-MOD-005 Cashier and Billing Request module. Accepted
  quotations convert into a diagnostic order via BCM-LAB-001 today; a
  Sale-conversion path can be added once MVP-MOD-005 exists.

## Validations executed

1. Required artifact completeness — passed
2. YAML syntax validation — passed
3. Capability map traceability (BCM-001) — passed
4. Dependency map traceability (BCM-002) — passed
5. Domain foundation traceability (context map, aggregates) — passed
6. Business rule format compliance (RN-###) — passed
7. Generation plan separation (generated vs custom) — passed
8. MDPE manual authoring compliance (no CRUD/DTO/etc. authored) — passed
9. API surface classification — passed
10. Permissions and audit coverage — passed
11. UI and mobile surface classification — passed
12. Registered path existence — passed
13. Agent-agnostic scan — passed
14. Cross-context ownership compliance (orchestrators delegate, do not own) — passed
15. Immutable snapshot modeling compliance (patient/doctor/branch/catalog/price) — passed
16. HRP alignment (HRP-001-P03 segments covered by MVP-MOD-004) — passed
17. BRM alignment (BRM-001-R003/R004/R005/R006/R018) — passed
18. No forward dependency on unbuilt MVP-MOD-005 Sale aggregate — passed

## Non-blocking observations

- Quotation-to-Sale conversion is deferred until MVP-MOD-005 models the Sale
  aggregate (tracked as TD-DEF-001).
- Detailed appointment capacity planning against branch schedules (BCM-ORG-007,
  MVP2) is out of scope for MVP-MOD-004; only branch operational status and
  overlap detection are modeled (tracked as TD-DEF-002).

## Readiness decision

MVP-MOD-004-DEF is **closed**. The next backlog item,
MVP-MOD-004-BE-001 (Compile appointment, reception and order backend
outputs), is unblocked.
