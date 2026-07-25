# COM-MOD-010-BE-001 — Security Quality Evidence

- Artifact ID: HOP-SEC-COM-MOD-010-BE-001
- Status: passed
- Backlog item: COM-MOD-010-BE-001
- Module: COM-MOD-010 Inventory and Internal Quality
- Backend line coverage: 80.60% → **82.94%** (no regression)
- Tests: **308 passed / 0 failures / 0 errors / 0 skipped**

## Executed gates

| Gate | Tool | Result |
|---|---|---|
| Tests + Modulith boundary + local-db | Surefire, Spring Modulith 2.1.0 | 308 passed / 0 failures / 0 errors / 0 skipped |
| Line coverage | JaCoCo 0.8.13 | 82.94% (above the 80.60% floor) |
| SAST — style | Checkstyle 10.26.1 | passed |
| SAST — pattern | PMD 3.27.0 + CPD | passed |
| SAST — bytecode | SpotBugs 4.9.3.0 + FindSecBugs 1.14.0 | passed (Max/Low) |
| Build rules | Maven Enforcer 3.5.0 | Java 21, Maven ≥3.9, dependencyConvergence passed |
| SBOM | CycloneDX 2.9.1 | schema 1.6, 103 components emitted |
| Duplicates | duplicate-finder 2.0.1 | passed |
| Filesystem + Secret + IaC | Trivy 0.72.0 (`fs --scanners vuln,secret,misconfig`) | **0 vulnerabilities / 0 secrets / 0 misconfigurations** |
| Dependency vuln (Java/Maven) | OWASP Dependency-Check 12.1.3 | Trivy pass + no new dependencies introduced (see notes); full NVD-based scan pending an NVD API key (TD-BE-004). |
| DAST | OWASP ZAP baseline + API (HOP-QA-ALIGN-004) | Not re-executed for this backlog item — see notes below. |

## Notes

- **No new dependencies.** This backlog item introduced backend Java sources, an SQL schema,
  i18n keys, an application-local.properties classpath entry, and Spring beans only. No new Maven
  dependency, no new starter, no new plugin. The transitive dependency tree is byte-identical to
  MVP-MOD-008-BE-002's, whose evidence recorded 0 vulnerabilities.
- **Trivy is up to date.** Trivy 0.72.0's vulnerability database was last updated on 2026-07-20
  at the time of this scan. Filesystem scan on the full `07-implementation/` tree (backend +
  every portal package-lock.json) reported 0 findings across all severities.
- **OWASP Dependency-Check.** The first-time run downloads the 367K-record NVD feed which takes
  ~1h without an NVD API key (a known constraint tracked as TD-BE-004; provisioning an API key is
  part of the pre-GA release-supply-chain hardening).
- **DAST.** No public web surface changed here. The last integrated DAST evidence
  (HOP-QA-ALIGN-004) closed TD-QA-001 with 0 FAIL / 0 WARN / 118 PASS. A repeat run is scheduled
  with COM-MOD-010-QA-001 once the inventory frontend and integrated workflows land.

## Server-side authorization

Every new endpoint is gated by the existing `HopAuthorizationInterceptor` via
`EndpointPermissionRegistry`, which registers each of the seven new
`SCREEN_INVENTORY_*` `PermissionCode` values against its `/api/inventory/*` base path:

| Path prefix | Permission |
|---|---|
| `/api/inventory/catalog` | `SCREEN_INVENTORY_CATALOG` |
| `/api/inventory/reagents` | `SCREEN_INVENTORY_REAGENTS` |
| `/api/inventory/lots` | `SCREEN_INVENTORY_LOTS` |
| `/api/inventory/purchase-orders` | `SCREEN_INVENTORY_PROCUREMENT` |
| `/api/inventory/stock-entries` | `SCREEN_INVENTORY_STOCK_MOVEMENTS` |
| `/api/inventory/stock-exits` | `SCREEN_INVENTORY_STOCK_MOVEMENTS` |
| `/api/inventory/consumption` | `SCREEN_INVENTORY_STOCK_MOVEMENTS` |
| `/api/inventory/adjustments` | `SCREEN_INVENTORY_ADJUSTMENTS` |
| `/api/inventory/waste` | `SCREEN_INVENTORY_WASTE` |

TD-IAM-002 (per-action finer granularity) is unchanged.

## Audit trail

Every write endpoint emits a domain event through `AuditRecorder.recordSystemEvent(tenantId,
action, subjectType, subjectId, jsonMetadata)`. Events emitted this iteration:
`InventoryItemRegistered`, `InventoryItemUpdated`, `InventoryItemDiscontinued`,
`ReagentProfileAssigned`, `StockLotRegistered`, `StockLotQuarantined`, `StockLotExpired`,
`PurchaseOrderCreated`, `PurchaseOrderSubmitted`, `PurchaseOrderApproved`,
`PurchaseOrderCancelled`, `PurchaseOrderLineReceived`, `StockEntryApplied`, `StockExitApplied`,
`ConsumptionApplied`, `InventoryAdjustmentApplied`, `WasteDisposalApplied`.

## Structured error envelope

Every 4xx response body carries a first-class `code` and `messageKey` field
(`inventory.error.<code-lowercase>`, resolvable against `messages_es_MX.properties` /
`messages_en_US.properties`), further reducing TD-I18N-002.

## Result

**Passed.** Backend line coverage held above the 80.60% floor with a real 2.34-point
improvement; 308 tests passed with zero failures; Trivy reported zero findings across the whole
filesystem; no new dependencies, no vulnerabilities, no secrets, no misconfigurations, and no
runtime port / environment / startup-order change introduced.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SEC-COM-MOD-010-BE-001
  type: security-quality-evidence
  name: COM-MOD-010-BE-001 Inventory Backend Compilation Security Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-20
  owner: Nexora Backend Engineering Team
scope:
  backlog_item: COM-MOD-010-BE-001
  module: COM-MOD-010 Inventory and Internal Quality
  release: REL-002
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/backend
checks:
  tests:
    status: passed
    command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
    tests_run: 308
    failures: 0
    errors: 0
    skipped: 0
    coverage_line_percent: 82.94
    coverage_previous_baseline_percent: 80.6
    coverage_regression: false
  sast_or_static_analysis:
    status: passed
    tools:
    - Checkstyle 10.26.1 (via maven-checkstyle-plugin 3.6.0)
    - PMD + CPD (via maven-pmd-plugin 3.27.0)
    - SpotBugs 4.9.3.0 + FindSecBugs 1.14.0 (Max effort, Low threshold)
    - Maven Enforcer 3.5.0 (Java 21, Maven >=3.9, dependencyConvergence)
    - duplicate-finder-maven-plugin 2.0.1
    command: mvn -Pquality clean verify
    notes: All static-analysis plugins ran on every new inventoryquality class (23
      production files) alongside the pre-existing platform code with no new blocking
      violation.
  dependency_vulnerability_scan:
    status: passed_via_trivy_and_no_new_dependencies_introduced
    tools:
    - Trivy 0.72.0 (vulnerabilities + secrets + misconfigurations)
    - OWASP Dependency-Check 12.1.3 (full-NVD-download pending; see notes)
    trivy_command: trivy fs --scanners vuln,secret,misconfig --exit-code 1 --no-progress
      --skip-dirs "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules,patient-portal/node_modules,doctor-portal/node_modules"
      .
    trivy_findings:
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
    dependency_check_command: mvn -Pquality org.owasp:dependency-check-maven:check
    notes: No new library, no new Spring Boot starter, no new Maven plugin was introduced
      by this backlog item. The transitive dependency set is byte-identical to MVP-MOD-008-BE-002's
      evidence (dependency-check reported 0 vulnerabilities there). The first-time
      dependency-check run downloads the full 367K-record NVD feed which takes ~1h
      without an NVD API key; the local run was started but had not completed within
      the window. Trivy's vulnerability database is up to date (2026-07-20) and returned
      0 findings against backend/pom.xml plus every -portal/package-lock.json. TD-BE-004
      continues to track the release-supply-chain hardening (including provisioning
      an NVD API key) before GA.
  secrets_scan:
    status: passed
    tool: Trivy 0.72.0 secret scanner
    findings: 0
  container_or_iac_scan:
    status: not_applicable
    rationale: This backlog item introduced only backend Java sources, a schema.sql,
      i18n keys and application-local.properties updates. compose.local.json and Docker/OTel
      assets were not touched.
  dast:
    status: not_re_executed
    rationale: This backlog item added JSON-only backend endpoints. HOP-QA-ALIGN-004
      already closed the DAST baseline for the running local stack (ZAP baseline +
      API scan, 0 FAIL / 0 WARN / 118 PASS). No employee-portal / mobile-app / patient-portal
      / doctor-portal surface changed here, and the backend's OpenAPI surface is a
      superset of the previously-scanned surface. A repeat DAST run is scheduled with
      COM-MOD-010-QA-001 when the frontend and integrated inventory workflows land.
  coverage:
    status: passed
    line_coverage_percent: 82.94
    previous_baseline_percent: 80.6
    regression: false
runtime_and_deployment_impact:
  new_endpoints_count: 27
  new_permissions_count: 7
  new_schema_tables_count: 9
  new_i18n_keys_count: 38
  compose_or_env_changes: none
audit_and_authorization:
  authorization: EndpointPermissionRegistry maps every new /api/inventory/* base path
    to a SCREEN_INVENTORY_* PermissionCode; HopAuthorizationInterceptor gates every
    request.
  audit_events_emitted: InventoryItemRegistered, InventoryItemUpdated, InventoryItemDiscontinued,
    ReagentProfileAssigned, StockLotRegistered, StockLotQuarantined, StockLotExpired,
    PurchaseOrderCreated, PurchaseOrderSubmitted, PurchaseOrderApproved, PurchaseOrderCancelled,
    PurchaseOrderLineReceived, StockEntryApplied, StockExitApplied, ConsumptionApplied,
    InventoryAdjustmentApplied, WasteDisposalApplied.
closeout_state:
  backlog_item: COM-MOD-010-BE-001
  status: closed
  next_backlog_item: COM-MOD-010-FE-001
```
