---
id: HOP-EVT-BCM-ATT-008
format: markdown_structured_payload
type: events
name: Billing Request Management Events
version: 0.1.0
status: modeled
---

# Billing Request Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ATT-008
  type: events
  name: Billing Request Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-008
published_events:
- id: EVT-BILL-001
  name: InvoiceRequested
  payload:
  - invoiceRequestId
  - saleId
  - patientId
  - branchId
  consumers:
  - audit-compliance
  - reporting
- id: EVT-BILL-002
  name: InvoiceSubmitted
  payload:
  - invoiceRequestId
  - adapterCorrelationId
  consumers:
  - audit-compliance
- id: EVT-BILL-003
  name: InvoiceIssued
  payload:
  - invoiceRequestId
  - saleId
  - externalInvoiceId
  consumers:
  - cash-sales
  - patient-portal
  - reporting
- id: EVT-BILL-004
  name: InvoiceFailed
  payload:
  - invoiceRequestId
  - providerCode
  - statusCode
  consumers:
  - audit-compliance
  - reporting
- id: EVT-BILL-005
  name: InvoiceCancelled
  payload:
  - invoiceRequestId
  - externalInvoiceId
  - cancelledBy
  consumers:
  - cash-sales
  - audit-compliance
  - reporting
consumed_events:
- event: SaleCreated
  source_capability: BCM-ATT-005
- event: PaymentRegistered
  source_capability: BCM-ATT-005
```
