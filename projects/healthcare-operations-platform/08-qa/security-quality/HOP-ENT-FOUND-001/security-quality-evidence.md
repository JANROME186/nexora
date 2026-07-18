# HOP-ENT-FOUND-001 Security and Quality Evidence

Machine-readable source: `security-quality-evidence.yaml`.

## Open source first

Only open source dependencies were introduced: Spring Security for backend request-time
authorization and local mobile quality dev tooling. `react-i18next`/FormatJS and
JPA/Hibernate/OpenAPI Generator/MapStruct/Lombok were each evaluated and deliberately deferred with
stated reasons — see the localization and persistence review documents under `03-architecture/`.

## Debt-first action

Closed `TD-BE-009`, `TD-IAM-001` and `TD-APP-002`; materially reduced `TD-I18N-002` and
`TD-IAM-002`; updated `TD-BE-003`'s baseline; kept residual gaps explicit rather than undocumented.

## Results

| Stack | Tests | Coverage | Result |
|---|---|---|---|
| Backend | 191, 0 failures/errors | 76.99% → **77.92%** | BUILD SUCCESS |
| Employee portal | 87, 0 failures | 83.98% → **84.44%** | all 7 gates passed |
| Mobile app | 17, 0 failures | **97.15%** | all configured gates passed |

Trivy: 0 vulnerabilities, no secret findings reported, no misconfiguration targets detected.
`npm audit`: 0 vulnerabilities. 878 repository
YAML files parse. Agent-agnostic scan: 1 confirmed false positive (`cursor: pointer`).

## Secure coding

The one genuine hardcoded-actor finding (a `"system"` literal standing in for a real
user-initiated action) was **fixed in code**. Corrective closure also added Spring Security,
request-time authentication/authorization through `HopAuthorizationInterceptor`, API/action
permission mapping and 401/403 tests. `TD-IAM-001` is closed for the local-development baseline;
production OIDC/IdP hardening remains productization work.

## DAST

No new DAST scan was required for this corrective closure because no new HTTP endpoint was added.
Existing OWASP ZAP baseline from `HOP-QA-ALIGN-004` remains the current DAST evidence; request-time
authorization behavior is covered by focused backend tests.

## Decision

**Passed.** No blocking findings, no accepted risks required.
