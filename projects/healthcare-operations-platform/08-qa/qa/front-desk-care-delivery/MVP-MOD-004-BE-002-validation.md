# MVP-MOD-004-BE-002 Validation Evidence

Human-readable companion for `MVP-MOD-004-BE-002-validation.yaml`.

## Scope

- Backlog item: MVP-MOD-004-BE-002 (Implement quote calculation and order lifecycle custom rules)
- Module: MVP-MOD-004 Front Desk and Care Delivery (Release REL-001)
- Execution flow stage: custom_rules
- Implementation root: `07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/`
- Baseline: `MVP-MOD-004-BE-001-validation.yaml`

## Result summary

Every custom rule explicitly deferred by MVP-MOD-004-BE-001's
`custom_rule_baseline_implemented_now.hooks_by_capability` is now implemented and tested.

| Capability | Rules refined | What changed |
| --- | --- | --- |
| BCM-LAB-001 | RN-001, RN-003, RN-007 | Referring-doctor eligibility gating, per-line multi-price-list resolution, tiered cancellation override |
| BCM-ATT-001 | RN-002, RN-006 | Tenant-configurable branch daily capacity, tenant-configurable no-show grace period, preparation-instruction surfacing |
| BCM-ATT-003 | RN-005, RN-007 | Priority + wait-time queue ordering, richer reception-to-admission audit trail |
| BCM-ATT-004 | RN-003 | Tenant-configurable admission acknowledgement policy |
| BCM-ATT-006 | RN-002, RN-003 | Per-line multi-price-list resolution, tenant-configurable discount policy |

## Design notes worth recording

- **New shared policy store.** `FrontDeskPolicyStore` (in `frontdeskcaredelivery.shared`) is a new
  `@Component` consolidating every tenant-configurable knob this backlog item introduces: no-show
  grace days, branch daily appointment capacity, required admission acknowledgements, and standard
  / override discount percentage caps. It mirrors the existing
  `peopleclinicalmasterdata.personmanagement.application.TenantPeoplePolicyStore` precedent
  established in MVP-MOD-003.
- **Multi-price-list resolution is per-line, not per-order.** Both `DiagnosticOrderManagementService
  .price()` and `QuotationManagementService.issue()` previously resolved one price list from the
  first line and reused it for every line. They now resolve independently per line, since a
  multi-service order/quotation may span catalog items only priced in different price lists. The
  order/quotation's single pricing-snapshot fields still record the *primary* (first-line) price
  list for backward-compatible auditing; the audit event additionally records how many distinct
  price lists were actually used.
- **Discovered domain rule while testing multi-price-list scenarios.** Two price lists in the same
  laboratory/currency/agreement scope with overlapping effective windows cannot both be published
  (`PriceListManagementService.publish()` RN-005). A genuine multi-price-list test therefore needs
  a second price list with a distinct `agreementRefId`; `getEffectivePriceSnapshot` still resolves
  either one when called with a `null` agreementRefId (no scope filter), which is how order/quotation
  pricing calls it. This is not a new rule — it already existed in bcm-svc-009 — but it was not
  previously exercised by any Front Desk and Care Delivery test.
- **Doctor eligibility is a real precondition now.** `create()` on `DiagnosticOrderManagementService`
  now calls `DoctorDirectory.isEligibleAsReferringDoctor(doctorId)` instead of existence-only
  lookup. A freshly registered doctor has no verified credential by default, so the API test's
  `registerDoctor()` helper now attaches and verifies a `medical_license` credential for tests that
  need an eligible referring doctor, and can skip that step for the new negative test.
- **Cancellation tiering uses order status as an honest proxy, not a fiction.** RN-007 requires an
  override reason when downstream sample/processing work already exists. The Sample aggregate does
  not exist until MVP-MOD-006, so `cancel()` uses order status (accepted/in_progress) as the
  closest enforceable signal and requires a written `overrideJustification` (≥15 characters) in
  that case. This over-approximation (an accepted order with no real sample yet still requires the
  override) is deliberate and documented as **TD-BE-010**, not silently accepted as "done."

## Validations executed

1. Backend test suite passes without a local database — passed (77 tests, 0 failures, 0 errors, 7 skipped)
2. Backend test suite passes against real Postgres (`-Dhop.local-db-tests=true`) — passed (77 tests, 0 failures, 0 errors, 0 skipped)
3. New and refined custom-rule test coverage — passed (10 new tests, all pre-existing tests still pass)
4. Spring Modulith module boundaries remain valid — passed (0 violations)
5. OpenAPI/contract coverage — passed (including the new preparation-instructions operation)
6. YAML repository files remain parseable — passed (557 files)
7. Agent-agnostic scan — passed (0 matches)
8. Stale pointer scan — passed
9. Filesystem vulnerability, secret and misconfiguration scan (Trivy) — passed (0 findings)
10. Security quality gate — passed (see security-quality-evidence)
11. `git diff --check` — passed (0 whitespace errors)

## Technical debt disposition

- **New:** TD-BE-010 (RN-007 full sample/processing-state check deferred to MVP-MOD-006).
- **Updated, not resolved:** TD-DEF-002 (a flat tenant-configurable daily branch capacity check now
  exists; real schedule-based capacity from BCM-ORG-007 remains the open target state).
- **Unchanged, still open:** TD-DEF-001 (quotation-to-Sale conversion, deferred to MVP-MOD-005) and
  TD-BE-009 (branch snapshot version placeholder) — neither was in this backlog item's scope.

## Readiness decision

MVP-MOD-004-BE-002 is **closed**. The next backlog item, MVP-MOD-004-FE-001 (Compile front desk
worklist and order creation UI outputs), is unblocked.
