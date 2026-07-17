# BCM-PLT-008 Document Management Capability Package

Human-readable companion for the Document Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PLT-008
- Domain: DOM-10 Platform
- Bounded context: `document-management`
- Primary aggregate: `StoredDocument` (new platform aggregate owned by this capability)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Generic, domain-agnostic document storage and versioning service used by
multiple bounded contexts (results, billing, imaging, patient records)
without owning any of their business aggregates. Provides a
`DocumentStoragePort` — a provider-agnostic storage boundary mirroring the
`FiscalAdapterPort` pattern from MVP-MOD-005 — with a local
filesystem/deterministic adapter as the default, self-hostable
implementation.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | StoredDocument aggregate, storage reference/retention value objects, DocumentStoragePort |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Store, retrieve and schedule disposal of a document |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | No UI — system-to-system internal service |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, tests, QA |

## Key rules modeled

- Every document has an identifier, version, content type and hash (RN-001).
- Retrieval re-verifies content hash; a mismatch blocks the read (RN-002).
- All storage access goes through `DocumentStoragePort` — no direct provider access (RN-003, provider-agnostic).
- Legal hold takes precedence over retention-based disposal (RN-004).
- This capability never inspects business content or mutates a business aggregate (RN-005).

## Architecture note

`context-map.yaml` does not yet declare a formal relationship entry for the
`document-management` bounded context. This is recorded as a non-blocking
traceability observation (see `traceability.yaml`) rather than edited
directly, since architecture-map changes require an ADR per
`SOURCE_OF_TRUTH.yaml`'s governance rule.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers hash computation, integrity re-verification, the
storage-port/adapter boundary, legal-hold precedence and the domain-agnostic
architecture boundary.
