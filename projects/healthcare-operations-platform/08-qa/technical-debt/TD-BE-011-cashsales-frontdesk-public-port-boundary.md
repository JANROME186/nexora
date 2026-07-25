---
id: TD-BE-011
format: markdown_structured_payload
type: technical-debt-item
name: CashSales depends on open FrontDeskCareDelivery internals instead of stable
  public ports
version: 1.1.0
status: closed
---

# Cashsales Depends On Open Frontdeskcaredelivery Internals Instead Of Stable Public Ports

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-011
  type: technical-debt-item
  name: CashSales depends on open FrontDeskCareDelivery internals instead of stable
    public ports
  version: 1.1.0
  status: closed
  created_date: 2026-07-16
source:
  discovered_during_backlog_item: MVP-MOD-005-BE-001
  module: MVP-MOD-005 Cashier and Billing Request
  evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-001-validation.md
classification:
  category: modular_architecture_boundary
  affected_area: cashsales_frontdesk_module_boundary
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/package-info.java
  risk_level: medium
  blocking: false
  reason_non_blocking: 'MVP-MOD-005-BE-001 requires creating Sale records from accepted
    diagnostic orders and quotations. The existing front-desk services expose the
    required source snapshots but not through a dedicated public port or named interface,
    so the module was temporarily opened to preserve Spring Modulith verification
    while keeping the capability functional and tested.

    '
current_state:
  issue: 'Closed by MVP-MOD-005-BE-002. CashSales now reads FrontDeskCareDelivery
    source snapshots through the stable sale-source-port named interface instead of
    depending on internal front-desk application services.

    '
  compensating_control:
  - Spring Modulith verification passes without Type.OPEN on FrontDeskCareDelivery.
  - CashSales does not mutate front-desk, patient or catalog aggregates.
  - The source handoff is covered by CashSalesApiTest, CashSalesLocalDatabaseTest
    and full backend quality gates.
target_state:
  preferred_open_source_tooling:
  - Spring Modulith named interfaces
  - ArchUnit module-boundary checks
  expected_integration_points:
  - frontdeskcaredelivery public source snapshot port for diagnostic orders
  - frontdeskcaredelivery public source snapshot port for quotations
  - cashsales application services depending only on public ports
remediation:
  strategy: completed_by_MVP_MOD_005_BE_002
  recommended_trigger:
  - MVP-MOD-005-BE-002
  - MVP-MOD-005-QA-001
  - future front-desk/cash-sales refactor touching source snapshot handoff
  acceptance_criteria:
  - FrontDeskCareDelivery exposes stable public ports or named interfaces for sale-source
    snapshots. Completed.
  - CashSales no longer depends on internal front-desk application services. Completed.
  - frontdeskcaredelivery/package-info.java no longer needs Type.OPEN for cash-sales
    access. Completed.
  - Spring Modulith and ArchUnit checks pass with the stricter module boundary. Completed.
  closure_evidence:
    backlog_item: MVP-MOD-005-BE-002
    validation_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-002-validation.md
    security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-BE-002/security-quality-evidence.md
    implementation:
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/application/FrontDeskSaleSourcePort.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/package-info.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/package-info.java
```
