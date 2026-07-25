---
id: HOP-OBS-BCM-ATT-006
format: markdown_structured_payload
type: observability-model
name: Quotation Management Observability Model
version: 0.1.0
status: modeled
---

# Quotation Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-ATT-006
  type: observability-model
  name: Quotation Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-006
  depends_on_capability: BCM-PLT-006
logs:
- event: quotation_drafted
  level: info
  fields:
  - quotationId
  - branchId
  - actorId
- event: quotation_issued
  level: info
  fields:
  - quotationId
  - actorId
  - totalAmount
  - validUntil
- event: quotation_accepted
  level: info
  fields:
  - quotationId
  - actorId
  - totalAmount
- event: quotation_converted
  level: info
  fields:
  - quotationId
  - convertedOrderId
  - actorId
- event: quotation_closed
  level: info
  fields:
  - quotationId
  - actorId
  - reasonCode
- event: quotation_discount_policy_rejected
  level: warn
  fields:
  - quotationId
  - actorId
  - requestedDiscount
metrics:
- name: quotation_issued_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: quotation_accepted_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: quotation_conversion_rate
  type: histogram
  labels:
  - tenantId
  - branchId
- name: quotation_expired_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: quotation_average_discount_percentage
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: IssueQuotation
  child_spans:
  - ResolvePriceList
  - CapturePricingSnapshot
  - ApplyDiscountPolicy
- span: ConvertQuotation
  child_spans:
  - InvokeCreateDiagnosticOrder
  - PublishQuotationConverted
audit_events:
- QuotationDrafted
- QuotationIssued
- QuotationAccepted
- QuotationConverted
- QuotationClosed
alerts:
- name: HighQuotationExpirationRate
  condition: quotation_expired_total rate exceeds threshold
  severity: warning
- name: HighDiscountPolicyRejectionRate
  condition: quotation_discount_policy_rejected rate exceeds threshold
  severity: warning
```
