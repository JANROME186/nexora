# BCM-PLT-005 API Management Capability Package

Human-readable companion for the API Management capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PLT-005
- Domain: DOM-10 Platform
- Bounded context: `integration-interoperability`
- Primary aggregate: `ApiSurfaceRegistration` (new platform aggregate owned by this capability)
- Process reference: HRP-001-P08 Migration and Integration Dry Run
- Roadmap group: MVP-MOD-008 Integration and Migration Readiness
- Priority: High

## Purpose

Governance boundary for every API operation HOP exposes: classification as
public, internal or partner; partner API key issuance, scoping and
revocation; rate-limit policy per consumer; and deprecation/versioning
governance for breaking changes. This capability never implements the
business behavior of a classified operation — that stays with its owning
capability's own `openapi-source.yaml`.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | ApiSurfaceRegistration aggregate, PartnerApiKey, RateLimitPolicy |
| `business-rules.yaml` | Numbered rules RN-001..RN-007 |
| `processes.yaml` | Classify, issue/revoke partner key, schedule deprecation |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee-portal admin screens (classification, partner keys) |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, tests, QA |

## Key rules modeled

- Every operation must be classified before external exposure (RN-001).
- Partner calls require a valid, scope-matched, tenant-matched key (RN-002).
- Breaking changes require a deprecation window and migration note (RN-003).
- Rate limits are enforced per consumer/classification tier (RN-004).
- Every governance action is audited (RN-005).

## Architecture note

`context-map.yaml` REL-CTX-011 already declares `integration-interoperability`
as an anti-corruption-layer bounded context. BCM-PLT-005 governs the
outbound/API-consumer side of that same bounded context while BCM-PLT-004
governs the inbound message side; no new bounded context or context-map.yaml
entry was required.

## Technical debt alignment

- **TD-STACK-003** (no OpenAPI-Generator client/server generation): this
  capability's `generation-plan.yaml` designates itself as the concrete pilot
  target for a generated TypeScript client, since it governs the
  partner-consumer surface that most needs a generated, versioned SDK.
- **TD-I18N-002** (structured error codes): `openapi-source.yaml` models a
  first-class `code` field on every error from inception, matching the debt
  item's own acceptance criterion for a structured-error-code API consumer.
- **TD-BE-015** (rate-limit enforcement scoped to partner-API-key-bearing
  requests only): materially reduced by COM-MOD-011-DEF. RN-007 and the new
  `RateLimitPolicy.consumerIdentificationMethod` field model how anonymous
  public-website callers (BCM-SVC-001/002/003/005, BCM-ATT-001/006) are
  identified for rate-limiting by IP address or session token. The runtime
  counter/window mechanism for non-keyed traffic remains a
  COM-MOD-011-BE-001 implementation task.

## COM-MOD-011 reuse

This capability has no public-facing screen or endpoint of its own. It governs the
`classification=public` `ApiSurfaceRegistration` entries and public-tier `RateLimitPolicy` that
BCM-SVC-001/002/003/005 and BCM-ATT-001/006 register for their new public_surface operations
under COM-MOD-011. No new capability package or aggregate was created; see `traceability.yaml`'s
`cross_module_reuse` entry, which also records COM-MOD-011-DEF's correction of stale
MVP-MOD-008/COM-MOD-009 status pointers found in this package during modeling.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers classification/publish-gating, partner-key
authorization, deprecation governance, rate-limit enforcement and audit
wiring.
