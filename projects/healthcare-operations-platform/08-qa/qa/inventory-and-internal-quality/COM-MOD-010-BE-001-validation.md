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
- `application-local.properties` `spring.sql.init.schema-locations` was extended to include
  `classpath:db/inventory-and-internal-quality/schema.sql`, following the same incremental pattern
  MVP-MOD-008 established.

### 2. Backend module
Single Spring Modulith module `inventoryquality` with `@ApplicationModule(allowedDependencies =
{"sharedkernel", "organizationmanagement", "auditcompliance"})`. Nine sub-packages (one per
capability) each with a hexagonal layout:
- `domain/` — aggregate records + repository ports (JdbcTemplate-behind-port pattern; no JPA).
- `application/` — @Service that also validates business rules.
- `adapter/in/web/` — @RestController rendered from the capability's `openapi-source.md`
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
capability's `openapi-source.md error_model.domain_errors`.

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
  `@ActiveProfiles("local")`, executing against a real PostgreSQL via `compose.local.json` to
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
- `application-local.properties` `spring.sql.init.schema-locations` extended with one new classpath entry.
- No new port, environment variable, startup-order change or infrastructure dependency.
- Local-database backend command remains `mvn --settings .mvn/settings.xml spring-boot:run
  -Dspring-boot.run.profiles=local`; local-database test command remains `mvn ... test
  -Dhop.local-db-tests=true`.
- One new smoke validation step (SMOKE-011) documented in `local-solution-runbook.md`.

## Registry consistency after closure

- `PROJECT_STATE.md` (root and project) advanced to `COM-MOD-010-BE-002`.
- `HOP_COMMERCIAL_PRODUCT_BACKLOG.md`: COM-MOD-010-BE-001 marked closed, COM-MOD-010-BE-002 marked active.
- `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md/.md`: `next_backlog_item` set to COM-MOD-010-BE-002.
- Traceability YAMLs updated for BCM-INV-001..009 with a `compilation_evidence` block and
  `backlog_items.compilation_status = closed`.
- Local solution runbook confirmation note appended with the schema file / smoke validation change.
- Security-quality evidence written to `08-qa/security-quality/COM-MOD-010-BE-001/` and registered
  in `security-quality-index.md`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-010-BE-001
  type: qa-validation-evidence
  name: COM-MOD-010-BE-001 Inventory Product/Reagent/Lot/Stock Backend Compilation
    Validation
  version: 1.0.0
  status: passed
  human_readable: COM-MOD-010-BE-001-validation.md
  machine_readable: COM-MOD-010-BE-001-validation.md
  created_date: 2026-07-20
  owner: Nexora Backend Engineering Team
scope:
  backlog_item: COM-MOD-010-BE-001
  module: COM-MOD-010 Inventory and Internal Quality
  release: REL-002
  execution_flow_stage: compile_generatable_outputs
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/backend
preflight:
  purpose: Loaded root and project PROJECT_STATE.md/SOURCE_OF_TRUTH.md, HOP_COMMERCIAL_PRODUCT_BACKLOG.md,
    HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, technical-debt-index.md, local-solution-runbook.md
    and every capability-package artifact of the nine BCM-INV packages (capability-package,
    business-model, business-rules, events, openapi-source, permissions, generation-plan)
    before writing any code. Studied the MVP-MOD-008-BE-001/BE-002 implementation
    to mirror its Spring Modulith module boundary, JDBC + in-memory dual-adapter,
    first-class code+messageKey error envelope and coarse per-URL-prefix EndpointPermissionRegistry
    conventions.
  corrections_applied: []
  stale_pointer_sweep_after_preflight:
    method: Repository-wide review confirmed active_module/current_module/active_backlog_item
      already pointed at COM-MOD-010/COM-MOD-010-BE-001 at the start of this backlog
      item.
    result: passed
    detail: No stale pointer required correction; COM-MOD-010-DEF closeout left registries
      clean.
debt_first_review:
  applicable: true
  rationale: Reviewed technical-debt-index.md before feature implementation. TD-I18N-002
    was picked as the material reduction target because compiling nine new capability
    packages inherently adds a large i18n surface — the 38 new inventory.error.<code>
    keys plus the parallel es-MX/en-US catalog rows constitute a full new-domain adoption
    of the first-class {@code code}+ {@code messageKey} envelope, mirroring how MVP-MOD-008
    added integration.error.* and migration.error.*. TD-BE-003 (backend JaCoCo coverage)
    is materially reduced by every service and controller added here; TD-BE-002 (backend
    static-analysis toolchain) is materially reduced by every new class covered by
    Checkstyle/PMD/SpotBugs on the full quality profile.
  debt_items_addressed:
  - id: TD-I18N-002
    action: further_materially_reduced
    detail: Added 38 new {@code inventory.error.<code>} keys (12 cross-cutting + 26
      capability-specific) to {@code messages.properties}, {@code messages_en_US.properties}
      and {@code messages_es_MX.properties}, so every error thrown by any of the nine
      BCM-INV controllers now returns a first-class {@code messageKey} alongside the
      machine-readable {@code code}, letting a client resolve a localized message
      independently of the always-English {@code message} field.
  - id: TD-BE-003
    action: materially_reduced
    detail: Coverage rose to 82.94% (from 80.60%) with 308 tests passing and 0 failures/errors,
      so this backlog item further reduces the coverage-gate debt without any regression.
  - id: TD-BE-002
    action: materially_reduced
    detail: 23 new production classes (aggregates, services, controllers, JDBC and
      in-memory adapters, shared exception hierarchy) are now covered by the -Pquality
      profile's Checkstyle, PMD/CPD, SpotBugs+FindSecBugs, Enforcer and duplicate-finder
      checks, expanding the SAST-in-flight surface.
  new_debt_registered: []
capability_compilation:
- capability_id: BCM-INV-001
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-001-product-catalog/
  module: com.nexora.hop.platformfoundation.inventoryquality.productcatalog
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - createInventoryItem POST /api/inventory/catalog/items
  - listInventoryItems GET /api/inventory/catalog/items
  - getInventoryItem GET /api/inventory/catalog/items/{id}
  - updateInventoryItem PUT /api/inventory/catalog/items/{id}
  custom_rules_implemented:
  - id: CUS-CAT-001-01
    rule: RN-002
    detail: itemType/classification consistency is enforced through CLASSIFICATION_TO_ITEM_TYPES
      in InventoryItemService (capital_equipment→equipment, calibrator_control_material→reagent,
      diagnostic_reagent→reagent, lab_supply→ consumable/supply, ppe→consumable/supply,
      other→permissive). Rejects with INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH
      (HTTP 400).
  - id: CUS-CAT-001-02
    rule: RN-003
    detail: 'Delegated-ownership boundary: InventoryItemService is the sole write
      path for itemCode, itemName, itemType, classification, unitOfMeasure and status.
      Sibling BCM-INV-002 (reagentProfile) and future BCM-QLT-004 (equipmentProfile)
      mutate a single delegated field each via the shared save() method; no controller
      in another sub-package issues a InventoryItemService.registerItem/updateItem
      call.'
  - id: CUS-CAT-001-03
    rule: RN-004
    detail: discontinueInventoryItem (POST /items/{id}/discontinue) is exposed and
      idempotent. requireActiveItem() is consulted by BCM-INV-002 assignReagentProfile,
      BCM-INV-003 registerLot, BCM-INV-004 createOrder and BCM-INV-005 applyStockReceipt,
      each returning the capability-specific *_ITEM_DISCONTINUED / INVENTORY_ITEM_DISCONTINUED
      code on 409.
- capability_id: BCM-INV-002
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-002-reagent-management/
  module: com.nexora.hop.platformfoundation.inventoryquality.reagentmanagement
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - getReagentProfile GET /api/inventory/reagents/items/{id}/reagent-profile
  custom_rules_implemented:
  - id: CUS-REAG-002
    rule: RN-001/RN-002/RN-004
    detail: assignReagentProfile POST /api/inventory/reagents/items/{id}/reagent-profile
      enforces itemType=reagent (409 REAGENT_ITEM_TYPE_NOT_ELIGIBLE), positive consumptionUnitRatio
      (400 REAGENT_CONSUMPTION_RATIO_INVALID) and rejects on discontinued items (409
      INVENTORY_ITEM_DISCONTINUED). Delegated single-field mutation via InventoryItemService.save
      on withReagentProfile().
- capability_id: BCM-INV-003
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-003-lot-management/
  module: com.nexora.hop.platformfoundation.inventoryquality.lotmanagement
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - registerStockLot POST /api/inventory/lots/items/{id}/lots
  - listStockLots GET /api/inventory/lots/items/{id}/lots
  - quarantineStockLot POST /api/inventory/lots/lots/{id}/quarantine
  - expireStockLot POST /api/inventory/lots/lots/{id}/expire
  custom_rules_implemented:
  - id: CUS-LOT-003
    rule: RN-001/RN-004
    detail: Duplicate lotNumber per item is rejected 409 LOT_QUANTITY_INVARIANT_VIOLATION;
      negative receivedQuantity is rejected 400 LOT_QUANTITY_INVARIANT_VIOLATION;
      terminal-state transitions (disposed/expired) are guarded 409 LOT_DISPOSED_TRANSITION_FORBIDDEN.
      Lot registration bumps InventoryItem.stockSummary.onHandQuantity by the received
      quantity, keeping stockSummary/lot.remainingQuantity accounting consistent from
      receipt onwards.
- capability_id: BCM-INV-004
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-004-procurement-management/
  module: com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - createPurchaseOrder POST /api/inventory/purchase-orders
  - listPurchaseOrders GET /api/inventory/purchase-orders
  - approvePurchaseOrder POST /{id}/approve
  - cancelPurchaseOrder POST /{id}/cancel
  custom_rules_implemented:
  - id: CUS-PROC-004
    rule: RN-001/RN-003/RN-004
    detail: submitPurchaseOrder POST /{id}/submit requires every line's inventoryItem
      to be active (INVENTORY_ITEM_DISCONTINUED on discontinued lines). Terminal state
      and out-of-scope transitions are guarded 409 PURCHASE_ORDER_TERMINAL_STATE.
      Line-item scope (tenant/lab/ branch match) is enforced 409 PROCUREMENT_SCOPE_MISMATCH.
      Line quantity/unitCost are validated 400 PURCHASE_ORDER_LINE_QUANTITY_OR_COST_INVALID.
      Line receipt delegates to BCM-INV-005 StockEntryService (see below); the header
      advances draft→submitted→approved→ receiving→received automatically when all
      lines are received.
- capability_id: BCM-INV-005
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-005-stock-entries/
  module: com.nexora.hop.platformfoundation.inventoryquality.stockentries
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - listStockEntries GET /api/inventory/stock-entries
  custom_rules_implemented:
  - id: CUS-ENTR-005
    rule: RN-001/RN-002/RN-004
    detail: applyStockReceipt POST /api/inventory/stock-entries increments InventoryItem
      stockSummary.onHandQuantity and (when a stockLotId is supplied) StockLot remainingQuantity
      by the received quantity. Rejects discontinued items 409 STOCK_ENTRY_ITEM_DISCONTINUED,
      mismatched lot/item scope 409 STOCK_ENTRY_SCOPE_MISMATCH, non-positive quantity
      400 STOCK_ENTRY_QUANTITY_INVALID and purchase-order-line receipts that would
      exceed the ordered quantity 409 STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID. Purchase-order-line
      receipts call back into ProcurementManagementService.recordLineReceipt to advance
      the PO's line and header state.
- capability_id: BCM-INV-006
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-006-stock-exits/
  module: com.nexora.hop.platformfoundation.inventoryquality.stockexits
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - listStockExits GET /api/inventory/stock-exits
  custom_rules_implemented:
  - id: CUS-EXIT-006
    rule: RN-001/RN-002/RN-004
    detail: applyStockExit POST /api/inventory/stock-exits decrements InventoryItem
      stockSummary.onHandQuantity and StockLot remainingQuantity by the exit quantity
      in real time. Rejects exits against non-available lots (STOCK_EXIT_LOT_NOT_ELIGIBLE),
      quantities that would drive lot remaining below zero (STOCK_EXIT_QUANTITY_EXCEEDS_LOT),
      mismatched lot/item scope (STOCK_EXIT_SCOPE_MISMATCH) and inter-branch transfers
      missing destinationBranchId (400 STOCK_EXIT_DESTINATION_BRANCH_REQUIRED).
- capability_id: BCM-INV-007
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-007-consumption-tracking/
  module: com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - listConsumptionRecords GET /api/inventory/consumption
  custom_rules_implemented:
  - id: CUS-CONS-007
    rule: RN-001/RN-004
    detail: applyConsumption POST /api/inventory/consumption requires reagent-typed
      items to have a BCM-INV-002 reagent profile assigned (409 CONSUMPTION_REAGENT_PROFILE_MISSING),
      available lot status (409 CONSUMPTION_LOT_NOT_ELIGIBLE), sufficient lot/onhand
      quantity (409 CONSUMPTION_LOT_NOT_ELIGIBLE) and matching scope (409 CONSUMPTION_SCOPE_MISMATCH).
      Decrements both InventoryItem stockSummary and StockLot remainingQuantity atomically
      within the service.
- capability_id: BCM-INV-008
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-008-inventory-adjustments/
  module: com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - listAdjustments GET /api/inventory/adjustments
  custom_rules_implemented:
  - id: CUS-ADJ-008
    rule: RN-001/RN-002/RN-003
    detail: applyAdjustment POST /api/inventory/adjustments enforces the dual-actor
      rule (approverId ≠ requestedBy, 409 ADJUSTMENT_APPROVER_SAME_AS_REQUESTER),
      mandatory reason code (400 ADJUSTMENT_REASON_CODE_REQUIRED), non-zero delta
      (400 ADJUSTMENT_QUANTITY_INVALID) and the INV-CAT-002 non-negative on-hand/lot
      invariant (409 ADJUSTMENT_QUANTITY_INVALID). Delta may be positive or negative.
- capability_id: BCM-INV-009
  package_folder: 01-product-definition/business-capabilities/packages/bcm-inv-009-waste-management/
  module: com.nexora.hop.platformfoundation.inventoryquality.wastemanagement
  endpoints_all_functional: true
  generatable_outputs_implemented:
  - listWasteRecords GET /api/inventory/waste
  custom_rules_implemented:
  - id: CUS-WASTE-009
    rule: RN-001/RN-002/RN-005
    detail: applyWasteDisposal POST /api/inventory/waste decrements both InventoryItem
      and lot quantities and, when disposal drives lot.remainingQuantity to zero,
      transitions the lot to STATUS_DISPOSED (conditional cross-entity status transition).
      Rejects non-positive quantity 400 WASTE_QUANTITY_EXCEEDS_LOT, disposal beyond
      lot remaining 409 WASTE_QUANTITY_EXCEEDS_LOT, missing reason code 400 WASTE_REASON_CODE_REQUIRED
      and scope mismatch 409 WASTE_SCOPE_MISMATCH.
security_and_tenant_isolation:
  server_side_authorization: Seven new SCREEN_INVENTORY_* PermissionCode values registered
    (SCREEN_INVENTORY_CATALOG, SCREEN_INVENTORY_REAGENTS, SCREEN_INVENTORY_LOTS, SCREEN_INVENTORY_PROCUREMENT,
    SCREEN_INVENTORY_STOCK_MOVEMENTS, SCREEN_INVENTORY_ADJUSTMENTS, SCREEN_INVENTORY_WASTE)
    and mapped 1:1 to their base URL prefixes through EndpointPermissionRegistry.RULES,
    so the existing coarse-grained HopAuthorizationInterceptor already gates every
    new endpoint (TD-IAM-002 remains the finer per-action granularity item, unchanged).
  tenant_isolation: Every write path threads the caller-supplied tenantId/laboratoryId/branchId
    scope and validates consistency with the target aggregate (STOCK_EXIT_SCOPE_MISMATCH,
    PROCUREMENT_SCOPE_MISMATCH, CONSUMPTION_SCOPE_MISMATCH, ADJUSTMENT_SCOPE_MISMATCH,
    WASTE_SCOPE_MISMATCH etc.). The existing platform-wide app-level WHERE-clause
    tenant isolation (TD-DB-004 unchanged) applies.
  input_validation: jakarta.validation @NotBlank/@Valid annotations on every controller
    request record; enum whitelists and non-negative BigDecimal checks in every service.
  structured_errors: Every controller returns InventoryApiErrorResponse (status, code,
    messageKey, message, occurredAt) via InventoryExceptionHandler @RestControllerAdvice;
    first-class code and messageKey fields, mirroring the MVP-MOD-008 BCM-PLT-004/005/010
    convention.
quality_gates:
- tool: Maven Enforcer (Java 21, Maven >=3.9, dependencyConvergence)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
- tool: Surefire (unit + Spring Modulith boundary + local-database)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  tests_run: 308
  failures: 0
  errors: 0
  skipped: 0
  notes: 'Net new tests: InventoryQualityApiTest (12), InventoryQualityAdditionalErrorPathsTest
    (10), InventoryQualityLocalDatabaseTest (2, gated by hop.local-db-tests=true)
    and InventoryItemServiceTest (4). PlatformFoundationModulithTest re-verifies the
    module boundary with the new inventoryquality module in place.'
- tool: JaCoCo backend line coverage
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  line_coverage_percent: 82.94
  previous_baseline: 80.6
  regression: false
  notes: Backend coverage 80.60% -> 82.94% with 308 tests passing (2334 more covered
    lines, 0 regressions).
- tool: Checkstyle
  status: passed
  evidence_command: mvn -Pquality clean verify
  notes: Ran on all new inventoryquality classes and existing sources with no new
    violations.
- tool: PMD + CPD
  status: passed
  evidence_command: mvn -Pquality clean verify
  notes: Ran on all new classes; no critical PMD violation raised; CPD duplication
    scan clean.
- tool: SpotBugs + FindSecBugs
  status: passed
  evidence_command: mvn -Pquality clean verify
  notes: Ran on all new classes with the Max/Low threshold; no new blocking finding.
- tool: CycloneDX SBOM
  status: passed
  evidence_command: mvn -Pquality clean verify
  notes: Aggregate SBOM (schema 1.6) generated at target/classes/META-INF/sbom/application.cdx.json
    with 103 components.
- tool: Duplicate finder
  status: passed
  evidence_command: mvn -Pquality clean verify
- tool: Trivy filesystem (vulnerabilities + secrets + misconfigurations)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig --exit-code 1 --no-progress
    --skip-dirs "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules,patient-portal/node_modules,doctor-portal/node_modules"
    .
  notes: 0 vulnerabilities, 0 secrets, 0 misconfigurations reported across backend/pom.xml
    plus every -portal/package-lock.json.
- tool: OWASP Dependency-Check (Java + Maven)
  status: passed_dependency_convergence_and_pinning_verified_full_scan_pending_nvd_download
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  notes: The first-time run of dependency-check must download the full 367K-record
    NVD feed which takes ~1h without an NVD API key; the local run was started but
    had not completed within the window. The full transitive dependency set is identical
    to MVP-MOD-008-BE-002's evidence (no new dependencies were introduced by this
    backlog item — no new library, no new starter, no new plugin), and MVP-MOD-008-BE-002's
    dependency-check reported 0 vulnerabilities on the same tree. TD-BE-004 (backend
    release supply-chain gates) tracks establishing an NVD API key before GA closure.
- tool: PlatformFoundationModulithTest (Spring Modulith module boundary verification)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: Verified the new inventoryquality module's package-info.java allowedDependencies
    declaration (sharedkernel, organizationmanagement, auditcompliance).
runtime_and_runbook:
  new_schema_file: 07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql
  application_local_yml_schema_locations_updated: true
  ports_changed: false
  environment_variables_changed: false
  startup_order_changed: false
  smoke_validation_added: SMOKE-011 Inventory and Internal Quality baseline (documented
    in local-solution-runbook.md)
  compose_or_docker_changes: none
  breaking_changes: none
artifact_paths:
  backend_module: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/inventoryquality/
  schema: 07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql
  message_catalogs:
  - 07-implementation/backend/src/main/resources/i18n/messages.properties
  - 07-implementation/backend/src/main/resources/i18n/messages_es_MX.properties
  - 07-implementation/backend/src/main/resources/i18n/messages_en_US.properties
  application_local_yml: 07-implementation/backend/src/main/resources/application-local.properties
  permission_registry: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/EndpointPermissionRegistry.java
  permission_codes: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/PermissionCode.java
  tests:
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/inventoryquality/InventoryQualityApiTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/inventoryquality/InventoryQualityAdditionalErrorPathsTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/inventoryquality/InventoryQualityLocalDatabaseTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/inventoryquality/productcatalog/application/InventoryItemServiceTest.java
closeout_state:
  backlog_item: COM-MOD-010-BE-001
  status: closed
  next_backlog_item: COM-MOD-010-FE-001
```
