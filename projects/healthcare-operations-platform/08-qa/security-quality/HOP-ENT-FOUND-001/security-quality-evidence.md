# HOP-ENT-FOUND-001 Security and Quality Evidence

Machine-readable source: `security-quality-evidence.yaml`.

## Open source first

No new dependencies introduced. `react-i18next`/FormatJS and JPA/Hibernate/OpenAPI
Generator/MapStruct/Lombok were each evaluated and deliberately deferred with stated reasons —
see the localization and persistence review documents under `03-architecture/`.

## Debt-first action

Closed `TD-BE-009`; materially reduced `TD-I18N-002`; updated `TD-BE-003`'s baseline; registered
12 new, non-blocking debt items rather than leaving discovered gaps undocumented.

## Results

| Stack | Tests | Coverage | Result |
|---|---|---|---|
| Backend | 182, 0 failures/errors | 76.99% → **77.32%** | BUILD SUCCESS |
| Employee portal | 86, 0 failures | 83.98% → **84.42%** | all 7 gates passed |
| Mobile app | 15, 0 failures | not configured (TD-APP-002) | all configured gates passed |

Trivy: 0 vulnerabilities/secrets/misconfigurations. `npm audit`: 0 vulnerabilities. 874 repository
YAML files parse. Agent-agnostic scan: 1 confirmed false positive (`cursor: pointer`).

## Secure coding

The one genuine hardcoded-actor finding (a `"system"` literal standing in for a real
user-initiated action) was **fixed in code**. The backend's complete lack of authentication was
found and **honestly disclosed** as `TD-IAM-001` (high risk, explicit P0 precondition for
`COM-MOD-009`) rather than hidden or downplayed.

## DAST

Not applicable — no new HTTP endpoint or runnable attack surface was added (one new required
request field on an existing endpoint). Existing OWASP ZAP baseline from `HOP-QA-ALIGN-004`
remains the current DAST evidence.

## Decision

**Passed.** No blocking findings, no accepted risks required.
