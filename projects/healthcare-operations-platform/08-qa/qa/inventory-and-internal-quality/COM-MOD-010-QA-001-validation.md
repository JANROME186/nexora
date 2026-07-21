# COM-MOD-010-QA-001 Validation

Status: **passed**.

Integrated traceability, stock and quality evidence for COM-MOD-010 Inventory and Internal
Quality, covering all 13 capability packages (BCM-INV-001..009, BCM-QLT-001/003/004/005) across
capability models, backend implementation, employee-portal implementation, tests and QA/security
evidence.

## Traceability validation

For each of the 13 capability packages, `openapi-source.yaml` operations were cross-checked
against the backend controllers, `permissions.yaml` against `EndpointPermissionRegistry.java` and
`RolePermissionCatalog.java`, and `ui-model.yaml` against the employee-portal screens and i18n
locale files. All 13 packages trace end to end with no gap.

**Stale pointers found and corrected:** the 9 `BCM-INV-001..009` `traceability.yaml` files had a
stale `backlog_items.custom_rules` pointer at `COM-MOD-010-BE-002` / `pending`, even though
`COM-MOD-010-BE-002` only implemented the 4 `BCM-QLT` packages. The real custom rules for
`BCM-INV-001..009` were implemented and closed inside `COM-MOD-010-BE-001` itself. Corrected all 9
files to `custom_rules: COM-MOD-010-BE-001` / `closed`, and set `validation_status: closed` for all
13 packages. `capability-package-index.yaml`'s COM-MOD-010 roadmap-group entry was also stale
(`backlog_item: COM-MOD-010-BE-001`, `package_status: modeled`); corrected to
`COM-MOD-010-QA-001` / `validated` for the group and all 13 capabilities.

## Coverage correction

A clean backend rebuild reproducibly measured JaCoCo line coverage at **81.90%** (0 source changes
since `COM-MOD-010-BE-002`), below the recorded 82.94% floor. Root cause: `COM-MOD-010-BE-002`
added 4 new JDBC repository adapters (quality-control runs, calibration, equipment availability,
maintenance) without a PostgreSQL-backed integration test, unlike `BCM-INV-001..009`'s existing
`InventoryQualityLocalDatabaseTest`. Added `InventoryQualityControlsLocalDatabaseTest.java` (3
tests) to close this real, in-scope gap. Corrected, reproducible backend line coverage is now
**83.73%** (315 tests, 0 failures/errors/skipped), above the 82.94% floor.

## Integrated workflow validation

Verified end to end against a real local PostgreSQL database: create inventory item, register
stock lot, stock entry (direct and purchase-order-line receipt), stock exit/consumption/
adjustment/waste (full +100/-10/-10/+5/-5 = 100 on-hand chain), equipment profile/availability/
calibration, maintenance record/complete, internal quality-control run/override, IAM permission
gating and dynamic menu filtering, and es-MX/en-US i18n key parity.

## Quality gates

- Backend `mvn -Pquality "-Dhop.local-db-tests=true" clean verify checkstyle:checkstyle pmd:pmd
  pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check`: passed, 315 tests,
  0 failures/errors/skipped, line coverage 83.73% (floor 82.94%). 0 Checkstyle/PMD/SpotBugs
  findings in the `inventoryquality` module (31 pre-existing Checkstyle findings and 1 pre-existing
  SpotBugs finding are all in unrelated modules, unchanged).
- `mvn -Pquality org.owasp:dependency-check-maven:check`: passed, 65 dependencies, 0
  vulnerabilities, using the local advisory database as-is (not refreshed), dated 2026-07-20
  13:56:20 -06:00.
- Employee portal `npm run quality`: passed, 124 tests/48 files, 0 failures, line coverage 88.24%
  (floor 87.87%), build/duplication/format/license all passed.
- `npm audit --audit-level=low`: passed, 0 vulnerabilities.
- Trivy filesystem scan (`vuln,secret,misconfig`, all severities): passed, 0 findings across
  `backend/pom.xml`, `employee-portal`, `doctor-portal` and `patient-portal` package locks.
- Secrets scan: passed (via Trivy `secret` scanner), 0 findings.
- Agent-agnostic scan: passed, 0 matches.
- DAST: no new externally-reachable surface introduced beyond the existing
  `HopAuthorizationInterceptor` boundary already covered by the HOP-QA-ALIGN-004 ZAP baseline;
  consistent with the COM-MOD-009-QA-001 precedent, marked `passed_in_prior_zap_scans` rather than
  re-run.
- YAML parse: passed, 1105 files, 0 errors.
- `git diff --check`: passed, no whitespace errors.

## Debt disposition

- `TD-BE-003` materially reduced: backend coverage corrected and raised to 83.73%.
- No new technical debt registered.

Ready for next backlog item: **COM-MOD-010-CLOSEOUT**.
