# MVP-MOD-008-BE-002 Security & Quality Evidence

Status: **passed** · Date: 2026-07-19 · Machine-readable source: [security-quality-evidence.yaml](security-quality-evidence.yaml)

## Summary

All open-source, dependency, secret, static-analysis and coverage gates passed for the integration
retry/dead-letter, API deprecation/rate-limit and migration checkpoint custom rules. Backend line
coverage improved from the 80.08% floor to **80.49%**. TD-BE-013 (XLSX row-level parsing) was closed
as the required debt-first action before feature work began.

## New dependency: Apache POI

`org.apache.poi:poi-ooxml:5.4.1` was added, explicitly named by both
`bcm-plt-010-open-data-ingestion-and-migration/generation-plan.yaml` and TD-BE-013's own
`target_state.preferred_open_source_tooling`. 0 vulnerabilities (OWASP Dependency-Check). Adding it
introduced a `commons-io` version conflict across three transitive paths (commons-csv, poi, poi's own
commons-compress); resolved by pinning `commons-io:2.20.0` in `dependencyManagement`, the same
technique this `pom.xml` already uses for its `jackson-databind` CVE pin.

## Quality gate results

| Gate | Result |
|---|---|
| Maven Enforcer | passed |
| Surefire (unit + Modulith + local-DB) | passed — 265 tests, 0 failures/errors/skipped |
| JaCoCo | 80.49% line coverage (floor was 80.08%) |
| CycloneDX | SBOM generated |
| OWASP Dependency-Check | 0 vulnerabilities |
| Trivy (`vuln,secret,misconfig`) | 0 findings (repo root, `.m2` cache excluded) |
| PMD | 354 findings — repo-wide, non-blocking, TD-BE-002 |
| CPD | 1 — unchanged from baseline |
| SpotBugs / Find Security Bugs | 23 findings — repo-wide, non-blocking, TD-BE-002; no new CORRECTNESS/SECURITY finding in new code |
| Duplicate Finder | passed |
| Spotless | pre-existing repo-wide debt, unchanged (not this backlog item's regression) |

## Secure-code review highlights

- **Bounded retry / dead-letter**: a hard 5-attempt ceiling with exponential backoff prevents retry
  storms against `IntegrationAdapterPort`; exhausted messages are deterministically dead-lettered.
- **Rate-limit enforcement**: unknown/revoked partner keys are rejected before further processing;
  an in-memory fixed-window counter throttles per-key traffic with bounded memory (one counter per
  active key, replaced each window).
- **Domain-command boundary preserved**: `MigrationDomainCommandPort` remains the only interaction
  point for import execution; `INV-MIG-003` (never write directly to a business aggregate) holds by
  construction, re-verified by `PlatformFoundationModulithTest`.
- **Structured errors**: every new error code is a named constant matching each capability's
  `openapi-source.yaml` error model, now carrying a `messageKey` alongside `code`.

## Debt decision

- **Closed**: TD-BE-013 (XLSX row-level parsing).
- **Opened**: TD-BE-014 (migration domain-command port has no real cross-module wiring yet),
  TD-BE-015 (rate-limit enforcement scoped to partner-API-key-bearing requests only).
- **Reduced**: TD-I18N-002 (added `messageKey` alongside `code` with full es-MX/en-US catalog
  coverage for BCM-PLT-004/005/010 error codes).

Ready for next backlog item: **MVP-MOD-008-FE-001**.
