---
id: HOP-OBS-BCM-ATT-005
format: markdown_structured_payload
type: observability-model
name: Cashier Operations Observability Model
version: 0.1.0
status: modeled
---

# Cashier Operations Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-ATT-005
  type: observability-model
  name: Cashier Operations Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-005
  depends_on_capability: BCM-PLT-006
logs:
- event: cash_session_opened
  level: info
  fields:
  - sessionId
  - branchId
  - actorId
- event: sale_created
  level: info
  fields:
  - saleId
  - sourceType
  - sourceReferenceId
  - totalAmount
- event: payment_registered
  level: info
  fields:
  - saleId
  - paymentId
  - amount
  - method
- event: cash_variance_detected
  level: warn
  fields:
  - sessionId
  - branchId
  - varianceAmount
metrics:
- name: cashier_sales_created_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: cashier_payments_registered_total
  type: counter
  labels:
  - tenantId
  - branchId
  - method
- name: cashier_outstanding_amount
  type: gauge
  labels:
  - tenantId
  - branchId
- name: cash_variance_amount
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: CreateSale
  child_spans:
  - ValidateSourceState
  - CaptureSnapshots
  - PersistSale
- span: RegisterPayment
  child_spans:
  - ValidateCashSession
  - AllocatePayment
  - PublishPaymentRegistered
alerts:
- name: HighCashVariance
  condition: cash_variance_amount exceeds threshold
  severity: warning
- name: PaymentRegistrationFailureSpike
  condition: payment registration failures exceed threshold
  severity: warning
```
