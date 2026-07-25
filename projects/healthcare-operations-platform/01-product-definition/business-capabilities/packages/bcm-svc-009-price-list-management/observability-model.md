---
id: HOP-OBS-BCM-SVC-009
format: markdown_structured_payload
type: observability-model
name: Price List Management Observability Model
version: 0.1.0
status: modeled
---

# Price List Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-009
  type: observability-model
  name: Price List Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-009
  depends_on_capability: BCM-PLT-006
logs:
- event: price_list_created
  level: info
  fields:
  - priceListId
  - tenantId
  - currency
  - actorId
- event: price_list_published
  level: info
  fields:
  - priceListId
  - version
  - effectiveFrom
  - actorId
- event: price_list_publish_rejected
  level: warn
  fields:
  - priceListId
  - reasonCode
metrics:
- name: catalog_price_lists_total
  type: gauge
  labels:
  - tenantId
  - status
  - currency
- name: catalog_price_list_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_effective_price_resolution_latency_ms
  type: histogram
traces:
- span: CreatePriceList
- span: PublishPriceList
  child_spans:
  - ValidateItemPublication
  - ValidateEffectiveOverlap
  - FreezePriceSnapshot
- span: ResolveEffectivePrice
audit_events:
- PriceListCreated
- PriceListPublished
- PriceListRevised
- PriceListDeprecated
alerts:
- name: HighPriceListPublishFailureRate
  condition: catalog_price_list_publish_failures_total rate exceeds threshold
  severity: warning
- name: EffectivePriceResolutionMiss
  condition: effective price resolution returns no active version for a sale
  severity: critical
```
