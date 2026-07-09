# BCM-002 — Capability Dependency Map

## Purpose

BCM-002 defines the implementation dependency map for the Healthcare Operations Platform capabilities listed in BCM-001.

This artifact is not a runtime architecture diagram. It is a sequencing and traceability guide for agents and engineering teams. It explains which capabilities must exist before another capability can be implemented safely, which modules are optional, and which product surfaces are affected.

## Scope

BCM-002 covers all 91 capabilities from BCM-001.

Each capability is mapped to:

- Required dependency profile.
- MVP phase.
- Product area.
- Affected bounded contexts.
- Related aggregates.
- Downstream dependencies.
- AI opportunities.
- Integration and migration relevance through its dependency profile.

The machine-readable source is:

`01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`

## Dependency Profiles

| Profile | Purpose | Typical Phase |
| --- | --- | --- |
| foundation | Tenant, laboratory, branch, identity, audit and observability primitives. | MVP1 |
| master_data | People, patients, doctors, employees, companies, suppliers and contacts. | MVP1-MVP2 |
| catalog | Diagnostic services, tests, analytes, samples, prices and preparation rules. | MVP1 |
| care_delivery | Appointment, registration, reception, admission and quotation workflows. | MVP1 |
| clinical_operations | Orders, samples, processing, validation and result release. | MVP1 |
| results_delivery | Reports, delivery, notifications, result history and critical result handling. | MVP1 |
| revenue_cycle | Cashier, payments, billing requests and country-pack fiscal workflows. | MVP1-MVP2 |
| inventory_quality | Inventory, reagents, equipment, calibration, QC and audit workflows. | MVP2-MVP3 |
| imaging | Imaging scheduling, DICOM/PACS, dictation, radiology signature and delivery. | MVP2-MVP3 |
| platform_extension | Integration, API management, workflow, notifications and documents. | MVP1-MVP2 |
| ai_overlay | Provider-agnostic AI capabilities and assistants. | MVP2-MVP3 |

## MVP1 Implementation Spine

The first executable product slice must be a usable laboratory operations flow:

1. Configure tenant, laboratory, branch, users, roles, audit and observability.
2. Configure diagnostic catalog, tests, analytes, reference ranges, samples and prices.
3. Register patients and doctors.
4. Create appointments or walk-in reception.
5. Register admission, order and payment.
6. Collect, label and receive samples.
7. Capture/process results.
8. Perform technical and medical validation.
9. Release results.
10. Generate PDF and deliver results through patient and doctor channels.

This spine is the minimum practical foundation for agents to begin implementation without waiting for imaging, advanced quality, inventory automation or AI assistants.

## Agent Usage Rules

Agents implementing a capability must:

1. Load BCM-001, BCM-002, the context map, shared kernel and aggregate catalog.
2. Confirm the capability exists in BCM-001.
3. Confirm all required capabilities are implemented or explicitly stubbed.
4. Use the owning bounded context and aggregate from BCM-002 and AGG-CATALOG-001.
5. Create or update OpenAPI contracts before implementation.
6. Emit domain events only through published language.
7. Update traceability records and project state after the change.

Agents must not:

- Introduce provider-bound AI, cloud or deployment dependencies into capability definitions.
- Mutate another bounded context aggregate directly.
- Treat generated diagrams as source of truth.
- Promote an optional capability into MVP1 without an ADR.

## Product Areas

| Product Area | Primary Responsibility |
| --- | --- |
| public_website | Public discovery and basic entry points. |
| employee_portal | Core operational work surface. |
| doctor_portal | Referring physician access to orders and released results. |
| patient_portal | Patient self-service, documents and result access. |
| mobile_apps | Patient, doctor and selected staff mobile workflows. |
| platform_core | Cross-cutting services used by every module. |

## Open Items

- Country-specific tax connectors must remain country-pack adapters under billing capabilities.
- Imaging requires a separate readiness track because DICOM/PACS adds infrastructure and integration constraints.
- AI use cases remain overlays until privacy, audit, cost and human-in-the-loop controls are approved for the target workflow.
