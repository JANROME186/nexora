# MVP-MOD-008-DEF Validation — Integration and Migration Readiness Capability Package Models

**Status:** passed
**Backlog item:** MVP-MOD-008-DEF
**Module:** MVP-MOD-008 Integration and Migration Readiness
**Code implemented:** No — this is a definition-only backlog item.

## Scope

Modeled the full Business Capability Package set for the three capabilities in
MVP-MOD-008's scope:

| Capability | Bounded context | Primary aggregate |
| --- | --- | --- |
| BCM-PLT-004 Integration Management | `integration-interoperability` | `IntegrationEndpoint` (new) |
| BCM-PLT-005 API Management | `integration-interoperability` | `ApiSurfaceRegistration` (new) |
| BCM-PLT-010 Open Data Ingestion and Migration | `data-migration-portability` | `MigrationJob` (AGG-016, owner) |

Each package contains the full 14-artifact required set (capability-package,
business-model, business-rules, processes, events, openapi-source,
permissions, ui-model, mobile-model, test-model, observability-model,
generation-plan, traceability, README) — 42 artifacts total.

## Key modeling decisions

- **BCM-PLT-004** models an `IntegrationAdapterPort` mirroring the
  `FiscalAdapterPort`/`DocumentStoragePort`/`NotificationProviderPort` pattern,
  normalizing external HL7/ASTM/FHIR/DICOM messages into canonical
  `NormalizedClinicalMessage` records before any domain module reads them,
  reusing `context-map.yaml` REL-CTX-011's published-language types exactly.
- **BCM-PLT-005** governs API classification (public/internal/partner),
  partner API key issuance/revocation and rate-limit policy, without
  implementing any classified operation's own business logic.
- **BCM-PLT-010** implements the pre-existing Open Data Ingestion Standard
  (NXF-ODI-STD-001) and HOP Open Data Ingestion Contract (HOP-ODI-001)
  directly — no new ingestion format or manifest schema was invented. It
  correctly maps onto `aggregate-catalog.yaml`'s AGG-016 `MigrationJob`, using
  `ImportBatch`/`ImportValidationReport` names from
  `capability-dependency-map.yaml`'s `related_aggregates` list for the same
  conceptual sub-entities `aggregate-catalog.yaml` calls `SourceDataset`/
  `ValidationReport`.

## Validations

19 validations executed (VAL-001 through VAL-019): required-artifact
completeness, YAML syntax, BCM-001/BCM-002 traceability, no duplicate
aggregate ownership, business-rule format compliance, generation-plan
separation, MDPE manual-authoring compliance, API surface classification,
permissions/audit coverage, UI/mobile surface classification, registered path
existence, agent-agnostic scan, provider-agnostic adapter pattern compliance,
MVP-MOD-008 acceptance-summary coverage, HRP-001-P08 alignment, BRM-001-R016/
R018 alignment, Open Data Ingestion Standard/contract compliance, and
architecture-map governance boundary compliance. **All passed; zero blocking
gaps.**

Two non-blocking observations are tracked: protocol/format-parsing library
selection is deferred to MVP-MOD-008-BE-001 as a stack decision, and
ACM-001's actor catalog does not yet define dedicated Implementation
Specialist/Data Migration Lead actor ids (existing internal-staff actors were
reused and the substitution documented).

## Debt-first review

Unlike prior definition-only backlog items, two open technical-debt items
were genuinely addressable at the modeling stage and were **materially
reduced** (not closed — no code was written):

- **TD-STACK-003** (no OpenAPI-Generator client/server generation): BCM-PLT-005's
  generation-plan.yaml now designates itself as the concrete pilot target for
  a generated TypeScript client, scheduled for MVP-MOD-008-FE-001.
- **TD-I18N-002** (structured error codes / full i18n adoption): its own
  recommended trigger — a structured-error-code API consumer — is hit for the
  first time by these three externally-facing capabilities, which model a
  first-class `code` field and reserve message-key namespaces from inception.

Both reductions are honestly scoped as modeling-stage decisions in each debt
item's own evidence text, not implementation claims.

## Readiness

- MVP-MOD-008-DEF: **closed**
- Next backlog item: **MVP-MOD-008-BE-001** (Compile integration adapter
  contracts and API governance outputs)
- HOP commercially complete / GA-ready: **No** — unchanged
- Coverage baselines unchanged and not regressed: backend 78.51%, employee
  portal 85.50%, mobile 98.87%, patient portal 41.93%, doctor portal 40.62%.
