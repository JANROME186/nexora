---
id: HOP-TRACE-BCM-ATT-005
format: markdown_structured_payload
type: traceability
name: Cashier Operations Traceability
version: 0.4.0
status: module_closed
---

# Cashier Operations Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-ATT-005
  type: traceability
  name: Cashier Operations Traceability
  version: 0.4.0
  status: module_closed
  classification: editable_model
  capability: BCM-ATT-005
links:
  bcm_001: 01-product-definition/business-capabilities/bcm-001/business-capability-map.md#BCM-ATT-005
  bcm_002: 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md#BCM-ATT-005
  requirements: 04-requirements/capabilities/bcm-att-005-cashier-operations/requirements.md
  aggregates:
  - 02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md#AGG-010
  - 02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md#AGG-011
coverage:
  requirements:
  - FR-ATT-005-001
  - FR-ATT-005-002
  - FR-ATT-005-003
  - FR-ATT-005-004
  - FR-ATT-005-005
  rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
  - RN-006
  processes:
  - PROC-CASH-001
  - PROC-CASH-002
  - PROC-CASH-003
  - PROC-CASH-004
  events:
  - CashSessionOpened
  - SaleCreated
  - PaymentRegistered
  - CashSessionClosed
  - CashVarianceDetected
hrp_alignment:
  process: HRP-001-P04 Revenue Collection and Billing Request
  segment: payment_and_cashier_execution
brm_alignment:
  rules:
  - BRM-001-R005
  - BRM-001-R006
  - BRM-001-R018
downstream_capabilities:
- BCM-ATT-008
- BCM-RES-004
implementation_trace:
  backend_package: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/cashieroperations/
  schema: 07-implementation/backend/src/main/resources/db/cash-sales/schema.sql
  qa_evidence_be_001: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-001-validation.md
  security_quality_evidence_be_001: 08-qa/security-quality/MVP-MOD-005-BE-001/security-quality-evidence.md
  qa_evidence_be_002: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-002-validation.md
  security_quality_evidence_be_002: 08-qa/security-quality/MVP-MOD-005-BE-002/security-quality-evidence.md
  public_frontdesk_source_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/application/FrontDeskSaleSourcePort.java
  employee_portal_screens:
  - 07-implementation/employee-portal/src/components/screens/CashSessionsScreen.tsx
  - 07-implementation/employee-portal/src/components/screens/SalesScreen.tsx
  qa_evidence_fe_001: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-FE-001-validation.md
  security_quality_evidence_fe_001: 08-qa/security-quality/MVP-MOD-005-FE-001/security-quality-evidence.md
  qa_evidence_qa_001: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-QA-001-validation.md
  security_quality_evidence_qa_001: 08-qa/security-quality/MVP-MOD-005-QA-001/security-quality-evidence.md
  qa_evidence_closeout: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-CLOSEOUT.md
  security_quality_evidence_closeout: 08-qa/security-quality/MVP-MOD-005-CLOSEOUT/security-quality-evidence.md
  module_boundary_verification: PlatformFoundationModulithTest (Spring Modulith ApplicationModules
    verify) confirms CashSales cannot mutate clinical/patient/order/catalog aggregates
    directly.
  current_backlog_item_status: closed
  next_backlog_item: none (module closed; see MVP-MOD-006-DEF for the next roadmap
    module)
```
