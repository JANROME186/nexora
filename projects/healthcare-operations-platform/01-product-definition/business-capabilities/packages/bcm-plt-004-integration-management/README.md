# BCM-PLT-004 Integration Management Capability Package

Human-readable companion for the Integration Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PLT-004
- Domain: DOM-10 Platform
- Bounded context: `integration-interoperability`
- Primary aggregate: `IntegrationEndpoint` (new platform aggregate owned by this capability)
- Process reference: HRP-001-P08 Migration and Integration Dry Run
- Roadmap group: MVP-MOD-008 Integration and Migration Readiness
- Priority: Critical

## Purpose

Anti-corruption boundary for external system messages (HL7, ASTM, FHIR, DICOM
and other provider protocols) entering HOP through registered integration
endpoints. Provides an `IntegrationAdapterPort` — a provider-agnostic message
boundary mirroring the `FiscalAdapterPort`/`DocumentStoragePort`/
`NotificationProviderPort` pattern from MVP-MOD-005/MVP-MOD-007 — with a local,
self-hostable default adapter. Normalizes external payloads into canonical
`NormalizedClinicalMessage` records before any domain module reads them.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | IntegrationEndpoint aggregate, message envelope value objects, IntegrationAdapterPort |
| `business-rules.md` | Numbered rules RN-001..RN-006 |
| `processes.md` | Register endpoint, receive/normalize, acknowledge/retry |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee-portal admin screens (endpoints, message log) |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, tests, QA |

## Key rules modeled

- No domain module parses a raw external payload directly (RN-001).
- Normalization failures use canonical error codes, never raw provider text (RN-002).
- Message processing is idempotent, keyed by `externalMessageId` (RN-003).
- Failed messages retry through a bounded, auditable policy (RN-004).
- Every message lifecycle transition is audited with a correlation id (RN-005).

## Architecture note

`context-map.md` REL-CTX-011 already declares `integration-interoperability`
as an anti-corruption layer with published-language types
`ExternalMessageEnvelope`/`NormalizedClinicalMessage`/`IntegrationAcknowledgement`.
This package reuses those exact names; no context-map.md edit was needed.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom implementation
covers the adapter port/protocol-parsing boundary, canonical error mapping,
idempotency, bounded retry and correlation-id audit propagation.
