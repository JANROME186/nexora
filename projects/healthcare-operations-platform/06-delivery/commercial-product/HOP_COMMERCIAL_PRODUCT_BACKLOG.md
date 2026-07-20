# HOP Commercial Product Backlog

Artifact ID: `HOP-COM-BACKLOG-001`
Version: `1.0.0`
Status: `approved`
Date: `2026-07-08`

This document defines the complete backlog required to take Healthcare Operations Platform from the completed MVP foundation into a commercial product. The machine-readable source for agents is `HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`.

## MDPE Rule

HOP now follows Nexora Model Driven Product Engineering.

The primary development unit is the `Business Capability Package`.

Modules remain only as roadmap groupings. They do not replace capability packages as the source of truth.

The official flow is:

```text
Model -> Compile -> Implement Rules -> Validate -> Release
```

Do not manually write repetitive artifacts that must come from the model:

- CRUD scaffolding
- DTOs
- Controllers
- Repositories
- Swagger documentation
- SDKs
- Repetitive documentation
- Duplicate models
- Repetitive test cases

## Starting Point

HOP has closed `MVP-MOD-005 Cashier and Billing Request` in full (`MVP-MOD-005-DEF` through
`MVP-MOD-005-CLOSEOUT`), `MVP-MOD-006 Laboratory Workflow` in full (`MVP-MOD-006-DEF` through
`MVP-MOD-006-CLOSEOUT`) and `HOP-QUALITY-ALIGNMENT` (`HOP-QA-ALIGN-001` through
`HOP-QA-ALIGN-CLOSEOUT`) is closed. `HOP-ENTERPRISE-FOUNDATION-ALIGNMENT` (`HOP-ENT-FOUND-001`) is
closed. `MVP-MOD-007 Results and Digital Delivery` is closed in full (`MVP-MOD-007-DEF` through
`MVP-MOD-007-CLOSEOUT`). `MVP-MOD-008 Integration and Migration Readiness` is closed in full (`MVP-MOD-008-DEF` through
`MVP-MOD-008-CLOSEOUT`). Functional development has moved to `COM-MOD-009 Patient and Doctor Portals`, starting
with `COM-MOD-009-DEF`.

The product must now move from MVP foundation to commercial readiness through an ordered backlog. Each module must be implemented, validated and closed before dependent modules begin, unless the dependency is explicitly mocked in that module definition.

## Commercial Objective

HOP is commercializable when it can support a diagnostic healthcare organization from configuration to result delivery, with secure digital channels, revenue operations, auditability, integrations, migration readiness, supportability and customer enablement.

The product is considered ready for commercial general availability only when:

- Core operational workflows run from tenant setup through result delivery.
- Web employee portal, patient and doctor digital channels, mobile foundation and backend APIs are aligned.
- Revenue, billing request, payments and reconciliation boundaries are usable by a real operation.
- Security, audit, observability, supportability and deployment operations are production-ready.
- Integration and migration boundaries allow customers to onboard without coupling HOP to legacy platforms.
- Customer data can be ingested from simple open formats that any incumbent system provider can deliver without proprietary dependencies.
- Optional product capabilities can be offered, purchased, entitled, installed and consumed through a marketplace-ready extension model.
- Compliance, quality, documentation and customer enablement are sufficient for a paid pilot and general availability.

## Release Plan

### REL-000 Foundation Completed

Status: `completed`

Includes:

- `MVP-MOD-001 Platform Foundation`

Outcome: platform foundation implemented and ready for functional validation.

### REL-001 Operational Core

Status: `planned`

Includes:

- `MVP-MOD-002 Diagnostic Catalog`
- `MVP-MOD-003 People and Clinical Master Data`
- `MVP-MOD-004 Front Desk and Care Delivery`
- `MVP-MOD-005 Cashier and Billing Request`
- `MVP-MOD-006 Laboratory Workflow`
- `MVP-MOD-007 Results and Digital Delivery`
- `MVP-MOD-008 Integration and Migration Readiness`

Outcome: a diagnostic laboratory can configure catalog, register people, create orders, collect samples, validate results and deliver reports.

### REL-002 Commercial Beta

Status: `planned`

Includes:

- `COM-MOD-009 Patient and Doctor Portals`
- `COM-MOD-010 Inventory and Internal Quality`
- `COM-MOD-011 Public Website and Digital Growth`
- `COM-MOD-012 Platform Hardening and SaaS Operations`

Outcome: HOP can run a controlled paid pilot with customer-facing channels, inventory/quality baseline and SaaS operations.

### REL-003 Commercial General Availability

Status: `planned`

Includes:

- `COM-MOD-013 Advanced Quality and Compliance`
- `COM-MOD-016 Commercial Launch and Customer Enablement`
- `COM-MOD-017 Product Marketplace and Extension Packaging`

Outcome: HOP is ready to be sold, onboarded, extended, supported and governed as a commercial product.

### REL-004 Expansion Packages

Status: `planned`

Includes:

- `COM-MOD-014 Imaging Operations`
- `COM-MOD-015 AI Overlay`

Outcome: imaging operations and AI-assisted overlays can be commercialized as optional product packages.

## Capability Group Sequence

| Order | Roadmap Group | Release | Purpose |
| --- | --- | --- | --- |
| 1 | `MVP-MOD-002 Diagnostic Catalog` | REL-001 | Configure services, tests, panels, analytes, samples, preparation, reference ranges and prices. |
| 2 | `MVP-MOD-003 People and Clinical Master Data` | REL-001 | Manage patients, doctors and person records. |
| 3 | `HOP-QUALITY-ALIGNMENT Enterprise Quality Alignment` | REL-001 | Bring HOP up to the updated enterprise quality framework before continuing functionality. |
| 4 | `MVP-MOD-004 Front Desk and Care Delivery` | REL-001 | Manage appointments, reception, admission, quotations and diagnostic order intake. |
| 5 | `MVP-MOD-005 Cashier and Billing Request` | REL-001 | Manage cash sessions, payments, sales and billing request boundaries. |
| 6 | `MVP-MOD-006 Laboratory Workflow` | REL-001 | Manage sample collection, labeling, reception, processing and validation. |
| 7 | `MVP-MOD-007 Results and Digital Delivery` | REL-001 | Generate reports and deliver released results. |
| 8 | `HOP-ENTERPRISE-FOUNDATION-ALIGNMENT Enterprise Product Foundation Alignment` | REL-001 | Align localization, IAM, session, database, UX/UI, documentation, persistence, contract generation, debt and coverage before customer-facing portals. |
| 9 | `MVP-MOD-008 Integration and Migration Readiness` | REL-001 | Provide adapter contracts, import validation, migration dry runs and API governance. |
| 10 | `COM-MOD-009 Patient and Doctor Portals` | REL-002 | Provide commercial patient and doctor digital channels. |
| 11 | `COM-MOD-010 Inventory and Internal Quality` | REL-002 | Add inventory, reagent, equipment, maintenance and internal quality controls. |
| 12 | `COM-MOD-011 Public Website and Digital Growth` | REL-002 | Provide public service discovery and conversion flows. |
| 13 | `COM-MOD-012 Platform Hardening and SaaS Operations` | REL-002 | Harden deployment, observability, support, backup, restore and tenant operations. |
| 14 | `COM-MOD-013 Advanced Quality and Compliance` | REL-003 | Add external quality, CAPA, audit management and compliance workflows. |
| 15 | `COM-MOD-014 Imaging Operations` | REL-004 | Add imaging workflows and DICOM/PACS adapter boundaries. |
| 16 | `COM-MOD-015 AI Overlay` | REL-004 | Add assistant, OCR, summary, semantic search and retrieval capabilities with clinical guardrails. |
| 17 | `COM-MOD-016 Commercial Launch and Customer Enablement` | REL-003 | Prepare onboarding, support, training, release governance and launch assets. |
| 18 | `COM-MOD-017 Product Marketplace and Extension Packaging` | REL-003 | Publish, sell, entitle, install, activate, upgrade and retire optional product packages. |

## Execution Contract

Before implementation, every capability in a roadmap group must have a Capability Package under:

`01-product-definition/business-capabilities/packages/`

Each Capability Package must include:

- `capability-package.yaml`
- `business-model.yaml`
- `business-rules.yaml`
- `processes.yaml`
- `events.yaml`
- `openapi-source.yaml`
- `permissions.yaml`
- `ui-model.yaml`
- `mobile-model.yaml`
- `test-model.yaml`
- `observability-model.yaml`
- `generation-plan.yaml`
- `traceability.yaml`
- `README.md`

During implementation, generated outputs must be produced from those models. Manual work is limited to custom business rules, external adapters, security-sensitive policies, performance-sensitive queries and ambiguous migration mappings.

Every code-changing backlog item must also apply the Nexora open-source-first security quality
standard. Evidence belongs under `08-qa/security-quality/<backlog-item-id>/` and must cover
applicable tests, best-practice and coding-standard checks, duplicate-code checks, complexity
checks, SAST/static analysis, OWASP or equivalent secure-code checks, dependency vulnerabilities
across all severities, secrets scan, coverage, message externalization/i18n and DAST when a
runnable surface exists.

Commercial completion and GA readiness additionally require:

- No open technical debt in `08-qa/technical-debt/`.
- At least 80% line coverage for every applicable delivered stack.
- No coverage regression below the previous measured iteration baseline during intermediate work.
- A 3 to 5 percentage point line-coverage improvement target for relevant iterations when a changed stack remains below 80%, or explicit justification plus immediate coverage debt.
- Enterprise product foundations satisfied before customer-facing portal/app expansion: base locales `es-MX` and `en-US`, language switching, IAM permissions, dynamic menus/actions, login/session context, database deliverables, UX/UI design baseline, code documentation, persistence architecture and OpenAPI/contract-first generation review.
- Every backlog closure has a verifiable closure audit: YAML parse, stale-pointer sweep,
  evidence-state sweep, `git diff --check`, command-output metrics matching evidence,
  synchronized registries, commit hash and clean `git status --short`.
- Backlog items with dirty git status, missing commit hash, stale pointers or limited/unexecuted
  mandatory gates are incomplete and must not be treated as closed.

## Commercial GA Gates

HOP reaches commercial general availability only after these gates pass:

- `GA-001 Operational workflow gate`: REL-001 modules complete and order-to-result flow passes.
- `GA-002 Digital channel gate`: patient, doctor, employee and mobile surfaces pass access and privacy validation.
- `GA-003 Security and compliance gate`: role, permission, audit, retention and privacy controls are validated.
- `GA-004 Operations gate`: deployment, monitoring, backup, restore and incident procedures are validated.
- `GA-005 Commercial enablement gate`: onboarding, training, support, release governance and sales demo materials are complete.
- `GA-006 Marketplace gate`: product marketplace standard, package lifecycle, entitlement, installation and observability evidence are complete.

## Next Action

`COM-MOD-009-PORTAL-002` is closed. Continue with `COM-MOD-009-APP-001` from:

`06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`

See `08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-002-validation.yaml` for the compilation validation evidence and `08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-001-validation.yaml`/`COM-MOD-009-BE-001-validation.yaml`/`COM-MOD-009-DEF-validation.yaml` for the preceding evidence.
