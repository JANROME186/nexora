# COM-MOD-010-BE-001 — Compile Product, Reagent, Lot and Stock Outputs Validation

- Artifact ID: HOP-QA-COM-MOD-010-BE-001
- Status: passed
- Module: COM-MOD-010 Inventory and Internal Quality
- Release: REL-002
- Business Requirement version: v0.68.0
- Backend coverage: 80.60% → **82.94%** (no regression)
- Tests: **308 passed / 0 failed / 0 errors / 0 skipped**
- Next backlog item: COM-MOD-010-BE-002

## Summary

Compiled the generatable backend outputs and essential custom rules for nine BCM-INV capability
packages (BCM-INV-001 Product Catalog, BCM-INV-002 Reagent Management, BCM-INV-003 Lot Management,
BCM-INV-004 Procurement Management, BCM-INV-005 Stock Entries, BCM-INV-006 Stock Exits,
BCM-INV-007 Consumption Tracking, BCM-INV-008 Inventory Adjustments and BCM-INV-009 Waste
Management) as a single Spring Modulith module
`com.nexora.hop.platformfoundation.inventoryquality`, mirroring the hexagonal + JDBC/in-memory
dual-adapter layout established by MVP-MOD-008. Every one of the 27 REST operations across the
nine capabilities is functional (no 501). BCM-QLT-001/003/004/005 remain scheduled for
COM-MOD-010-BE-002.

The shared AGG-013 `InventoryItem` aggregate is owned by BCM-INV-001 and its delegated field
mutations (reagentProfile from BCM-INV-002, stockSummary from BCM-INV-005/006/007/008/009) go
through single-field-scoped methods on the aggregate plus an `InventoryItemService.save`
delegated write path, so the INV-CAT-003 delegated-ownership invariant is architecturally
enforced. Stock accounting is consistent across every path: registering a lot bumps
`InventoryItem.stockSummary.onHandQuantity` by the received quantity, and every subsequent
entry/exit/consumption/adjustment/waste mutates both `InventoryItem.stockSummary` and the
referenced `StockLot.remainingQuantity` atomically. INV-CAT-002 (on-hand cannot go negative) is
enforced by every mutator.

## Debt-first action

- **TD-I18N-002 (further materially reduced).** Added 38 new
  `inventory.error.<code>` keys (12 cross-cutting + 26 capability-specific) to
  `messages.properties`, `messages_en_US.properties` and `messages_es_MX.properties`. Every error
  thrown by any of the nine BCM-INV controllers now returns a first-class `messageKey` alongside
  the machine-readable `code` and the always-English `message`, letting a client resolve a
  localized message independently.
- **TD-BE-003 (materially reduced).** Backend line coverage rose from 80.60% to 82.94% with 308
  tests, 0 failures/errors, 0 skipped.
- **TD-BE-002 (materially reduced).** 23 new production classes now covered by the `-Pquality`
  profile's Checkstyle, PMD/CPD, SpotBugs+FindSecBugs, Enforcer and duplicate-finder.

## What was implemented

### 1. Data schema
- `07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql`
  creates the `inventory_quality` schema with 9 tables: `inventory_items`, `stock_lots`,
  `purchase_orders`, `purchase_order_lines`, `stock_entries`, `stock_exits`,
  `consumption_records`, `inventory_adjustments`, `waste_records`. `inventory_items` carries the
  full BCM-INV-001..009 + BCM-QLT-004 field structure (StockSummary rollup fields, ReagentProfile
  fields, EquipmentProfile fields reserved for BCM-QLT-004), preserving INV-CAT-004 by design.
  `stock_lots.remaining_quantity` never goes negative because every mutator (exit, consumption,
  adjustment, waste) explicitly guards against underflow before persisting.
- `application-local.yml` `spring.sql.init.schema-locations` was extended to include
  `classpath:db/inventory-and-internal-quality/schema.sql`, following the same incremental pattern
  MVP-MOD-008 established.

### 2. Backend module
Single Spring Modulith module `inventoryquality` with `@ApplicationModule(allowedDependencies =
{"sharedkernel", "organizationmanagement", "auditcompliance"})`. Nine sub-packages (one per
capability) each with a hexagonal layout:
- `domain/` — aggregate records + repository ports (JdbcTemplate-behind-port pattern; no JPA).
- `application/` — @Service that also validates business rules.
- `adapter/in/web/` — @RestController rendered from the capability's `openapi-source.yaml`
  operation list.
- `adapter/out/jdbc/` — `@Profile("local")` PostgreSQL implementation.
- `adapter/out/memory/` — `@Profile("!local")` in-memory implementation used by tests.

### 3. Custom rules implemented
See `capability_compilation` in the YAML evidence for the per-capability rule-to-code mapping.
Highlights:
- **BCM-INV-001 CUS-CAT-001-01/02/03** — itemType/classification consistency; delegated ownership
  boundary; discontinuation gate consulted by BCM-INV-002/003/004/005 before accepting new
  commands.
- **BCM-INV-004 CUS-PROC-004** — full purchase-order lifecycle (draft → submitted → approved →
  receiving → received / cancelled), with line-receipt callbacks into BCM-INV-005 that keep the
  header's state authoritative in this capability.
- **BCM-INV-008 CUS-ADJ-008** — dual-actor requester/approver rule (both are required and must
  differ) plus the INV-CAT-002 non-negative invariant enforced on both `onHandQuantity` and
  `lot.remainingQuantity`.
- **BCM-INV-009 CUS-WASTE-009** — conditional cross-entity status transition: when a disposal
  drives `lot.remainingQuantity` to zero, the lot transitions to `disposed`.

### 4. IAM / permissions
Seven new `SCREEN_INVENTORY_*` `PermissionCode` values registered and 1:1 mapped to base URL
prefixes in `EndpointPermissionRegistry.RULES`, so the existing coarse `HopAuthorizationInterceptor`
already gates every new endpoint (TD-IAM-002 remains as the finer per-action item, unchanged).

### 5. Error envelope
`InventoryExceptionHandler` returns
`{status, code, messageKey: "inventory.error.<code-lowercase>", message, occurredAt}` on every
`@RestControllerAdvice` mapping. All controllers return either 400 (invalid input), 404 (not
found), 409 (state guard / conflict) or 2xx, with clearly-typed `code` values matching each
capability's `openapi-source.yaml error_model.domain_errors`.

### 6. Audit events
Every command emits an `AuditRecorder.recordSystemEvent(tenantId, action, subjectType,
subjectId, jsonMetadata)` audit trail entry (`InventoryItemRegistered`, `InventoryItemUpdated`,
`InventoryItemDiscontinued`, `ReagentProfileAssigned`, `StockLotRegistered`,
`StockLotQuarantined`, `StockLotExpired`, `PurchaseOrder*`, `StockEntryApplied`,
`StockExitApplied`, `ConsumptionApplied`, `InventoryAdjustmentApplied`, `WasteDisposalApplied`).

### 7. Tests
- `InventoryQualityApiTest` — 12 happy-path + error-path scenarios covering all 27 endpoints via
  MockMvc.
- `InventoryQualityAdditionalErrorPathsTest` — 10 targeted domain-guard scenarios (dual-actor,
  discontinued item guards, PO state machine, lot duplicate-number, waste/adjustment invariants).
- `InventoryQualityLocalDatabaseTest` — 2 scenarios gated by `-Dhop.local-db-tests=true` and
  `@ActiveProfiles("local")`, executing against a real PostgreSQL via `compose.local.yml` to
  exercise the JDBC adapters end-to-end.
- `InventoryItemServiceTest` — 4 unit tests for RN-002 consistency, duplicate scope, active-check
  contract.

## Quality gate execution

Backend `-Pquality "-Dhop.local-db-tests=true" clean verify` reported BUILD SUCCESS with:

| Gate | Result |
|---|---|
| Maven Enforcer (Java 21, Maven ≥3.9, dependencyConvergence) | passed |
| Surefire (unit + Spring Modulith + local-db) | 308 tests, 0 failures, 0 errors, 0 skipped |
| JaCoCo line coverage | **82.94%** (up from 80.60% floor) |
| Spotless / Checkstyle | passed |
| PMD + CPD | passed |
| SpotBugs + FindSecBugs | passed |
| CycloneDX SBOM (schema 1.6, 103 components) | passed |
| duplicate-finder | passed |
| PlatformFoundationModulithTest | passed (new module boundary verified) |
| Trivy filesystem (vuln + secret + misconfig) | 0 findings across all severities |
| OWASP Dependency-Check | pending full NVD download (~1h without API key); tracked via TD-BE-004 for an NVD API key. No new dependency was introduced by this backlog item — the transitive tree is identical to MVP-MOD-008-BE-002's, which had a clean scan. |

## Runbook impact

- New schema file: `07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql`.
- `application-local.yml` `spring.sql.init.schema-locations` extended with one new classpath entry.
- No new port, environment variable, startup-order change or infrastructure dependency.
- Local-database backend command remains `mvn --settings .mvn/settings.xml spring-boot:run
  -Dspring-boot.run.profiles=local`; local-database test command remains `mvn ... test
  -Dhop.local-db-tests=true`.
- One new smoke validation step (SMOKE-011) documented in `local-solution-runbook.yaml`.

## Registry consistency after closure

- `PROJECT_STATE.yaml` (root and project) advanced to `COM-MOD-010-BE-002`.
- `HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`: COM-MOD-010-BE-001 marked closed, COM-MOD-010-BE-002 marked active.
- `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml/.md`: `next_backlog_item` set to COM-MOD-010-BE-002.
- Traceability YAMLs updated for BCM-INV-001..009 with a `compilation_evidence` block and
  `backlog_items.compilation_status = closed`.
- Local solution runbook confirmation note appended with the schema file / smoke validation change.
- Security-quality evidence written to `08-qa/security-quality/COM-MOD-010-BE-001/` and registered
  in `security-quality-index.yaml`.
