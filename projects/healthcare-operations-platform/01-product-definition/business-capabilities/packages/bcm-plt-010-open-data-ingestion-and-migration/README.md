# BCM-PLT-010 Open Data Ingestion and Migration Capability Package

Human-readable companion for the Open Data Ingestion and Migration capability
package. The YAML models in this folder are the authoritative source of
truth.

## Capability

- ID: BCM-PLT-010
- Domain: DOM-10 Platform
- Bounded context: `data-migration-portability` (secondary: `integration-interoperability`, `audit-compliance`)
- Primary aggregate: `MigrationJob` (AGG-016, owner)
- Process reference: HRP-001-P08 Migration and Integration Dry Run
- Roadmap group: MVP-MOD-008 Integration and Migration Readiness
- Priority: Critical

## Purpose

Provider-agnostic legacy-data migration boundary implementing the Nexora Open
Data Ingestion Standard (NXF-ODI-STD-001) and the HOP Open Data Ingestion
Contract (HOP-ODI-001). Ingests customer data delivered by incumbent systems
in simple, documented, provider-deliverable formats (CSV, XLSX, JSON, NDJSON,
ZIP bundles), validates and reconciles it in a dry run before any domain
mutation, and executes accepted imports exclusively through existing domain
commands — never a direct write to a business aggregate.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | MigrationJob aggregate and its owned entities |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Receive, map/dry-run, approve/execute, retry |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model: import/dry-run/commit/reconciliation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee-portal admin screens (jobs, dry-run, reconciliation) |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, contract, requirements, tests, QA |

## Key rules modeled

- Only manifest-declared, accepted-format bundles may be ingested (RN-001).
- Dry-run validation must pass before any domain mutation (RN-002).
- Execution happens only through existing domain commands, never a direct write (RN-003).
- Migration jobs are retryable from their last checkpoint (RN-004).
- Every job step is audited and reconciled (RN-005).

## Aggregate naming note

`aggregate-catalog.yaml` AGG-016 names `MigrationJob`'s owned sub-entities
`SourceDataset` and `ValidationReport`. This package uses `ImportBatch` and
`ImportValidationReport` — the exact names
`capability-dependency-map.yaml`'s `related_aggregates` list uses for
BCM-PLT-010. Both name pairs refer to the same conceptual entities; see
`traceability.yaml` for the explicit correspondence. No aggregate-catalog.yaml
or context-map.yaml edit was required — both already declare
`data-migration-portability` and its published language
(`UniversalImportRecord`/`CanonicalDataRecord`/`MigrationValidationReport`, per
REL-CTX-010).

## Technical debt alignment

`generation-plan.yaml`'s custom implementation points name concrete
open-source-first parsing libraries (Apache Commons CSV, Apache POI, Jackson)
as the evaluation basis for future backend compilation, consistent with
Nexora's open-source-first technology selection policy.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers manifest/checksum verification, field mapping,
multi-category dry-run validation, domain-command-only execution, checkpoint
retry and reconciliation aggregation.
