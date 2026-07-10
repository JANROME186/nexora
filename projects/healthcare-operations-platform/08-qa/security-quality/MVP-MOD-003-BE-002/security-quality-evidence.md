# MVP-MOD-003-BE-002 — Security Quality Evidence

Machine-readable evidence: [security-quality-evidence.yaml](security-quality-evidence.yaml)

## Summary

`MVP-MOD-003-BE-002` implements the duplicate-detection and portal-identity custom rules deferred
by `MVP-MOD-003-BE-001` across BCM-PER-001, BCM-PER-002, BCM-PER-003 and BCM-ATT-002. No new
dependency was introduced; identifier hashing uses only JDK-bundled `java.security`/`java.util`
classes. The stack stays on Spring Boot 3.5.14, Spring Modulith 1.4.5 and PostgreSQL JDBC 42.7.12.

## Security review specific to this backlog

- One-way SHA-256 hashing replaces a weak `hashCode()`-based identifier digest for duplicate
  matching.
- Consent revocation is append-only; the original evidence row is never mutated.
- Patient merge never deletes data (soft-merge with a bounded merge-chain lookup).
- Duplicate detection and tenant policy overrides are strictly tenant-scoped.
- The new tenant policy store has no REST surface in this backlog, so it cannot be altered by an
  external caller (flagged as a modeling gap in `FWF-HOP-002`, not a vulnerability).

## Gates executed

| Gate | Result |
|---|---|
| Backend automated tests (`mvn test`) | Passed: 58 tests, 0 failures, 0 errors, 6 skipped |
| Backend database-backed tests | Passed: 58 tests, 0 failures, 0 errors, 0 skipped |
| Spring Modulith module boundary check | Passed |
| OpenAPI contract coverage | Passed |
| Custom-rule functional coverage | Passed |
| Static analysis (compile) | Passed through Maven test execution |
| Trivy fs vuln + secret + misconfig scan | Passed: 0 HIGH/CRITICAL findings |
| Agent-agnostic scan | Passed |
| DAST | Deferred — TD-QA-001 |
| Container or IaC scan | Passed through Trivy filesystem scan |

## Confirmation note

The original delivery reported shell/build unavailability. This follow-up validation executed the
required Maven, YAML and Trivy gates, corrected the issues found, and resolved exception `EX-001`.

## Dependencies

No dependency changes.

## Technical debt

Reuses `TD-QA-001`, `TD-BE-002`, `TD-BE-003`, `TD-BE-004`, `TD-STACK-001`. Newly registered:
`TD-BE-005` (doctor referring-eligibility as a computed query instead of a status-field change) and
`TD-BE-006` (patient registration commit orchestration is not transactionally atomic).

## Result

Security quality gate: **passed**. Ready to continue with `MVP-MOD-003-FE-001`.
