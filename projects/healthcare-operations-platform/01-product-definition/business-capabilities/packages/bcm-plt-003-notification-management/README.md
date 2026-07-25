# BCM-PLT-003 Notification Management Capability Package

Human-readable companion for the Notification Management capability
package. The YAML models in this folder are the authoritative source of
truth.

## Capability

- ID: BCM-PLT-003
- Domain: DOM-10 Platform
- Bounded context: `notifications`
- Primary aggregate: `NotificationRequest` (new platform aggregate owned by this capability)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Provider-agnostic notification dispatch platform capability. Routes
`NotificationRequest` submissions to a configured channel (email, SMS,
push, in-app) through a `NotificationProviderPort` — mirroring the
`FiscalAdapterPort` pattern from MVP-MOD-005 and the `DocumentStoragePort`
pattern from BCM-PLT-008 — and records delivery outcome. Never decides what
to send or to whom; that business decision belongs entirely to the
requesting capability (BCM-RES-007), consistent with the context map's
`notifications does not own business decisions` rule (REL-CTX-012).

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | NotificationRequest aggregate, delivery-attempt/preference value objects, NotificationProviderPort |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Submit, dispatch and finalize a notification request |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | No UI — system-to-system internal service |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, tests, QA |

## Key rules modeled

- Dispatch respects recipient channel preferences, with a critical-priority override policy (RN-001).
- All dispatch goes through `NotificationProviderPort` — no direct provider access (RN-002, provider-agnostic).
- This capability never decides notification content or trigger conditions (RN-003).
- Failed dispatch follows a bounded retry policy before finalizing as failed (RN-004).
- This capability never mutates a business aggregate (RN-005).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the preference check, the provider-port/adapter
boundary, the content/decision boundary and retry-policy sequencing.
