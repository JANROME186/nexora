---
id: HOP-PROC-BCM-SVC-009
format: markdown_structured_payload
type: processes
name: Price List Management Processes
version: 0.1.0
status: modeled
---

# Price List Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-009
  type: processes
  name: Price List Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-009
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: finance-manager
  name: Finance Manager
  source: ACM-001
processes:
- id: PRC-SVC-009-01
  name: Define price list
  actor: catalog-administrator
  trigger: A new price list must be configured.
  commands:
  - CreatePriceList
  - AddPriceEntry
  preconditions:
  - Catalog items exist.
  - Actor holds catalog.price.write.
  steps:
  - Capture code, name, currency and effective date.
  - Add price entries for services, tests and panels.
  - Persist as draft version 1.
  outcome: PriceListCreated
  rules:
  - RN-001
  - RN-002
  - RN-007
- id: PRC-SVC-009-02
  name: Publish price list
  actor: finance-manager
  trigger: A draft price list is approved.
  commands:
  - PublishPriceList
  preconditions:
  - Referenced items are published.
  - No effective-date overlap exists.
  steps:
  - Validate item publication and effective-date overlap.
  - Freeze effective-dated price snapshot.
  - Mark price list published.
  outcome: PriceListPublished
  rules:
  - RN-003
  - RN-004
  - RN-005
- id: PRC-SVC-009-03
  name: Version price list
  actor: finance-manager
  trigger: Prices change.
  commands:
  - UpdatePriceList
  steps:
  - Create new effective-dated draft version.
  - Preserve prior version for historical sales pricing.
  outcome: PriceListRevised
  rules:
  - RN-004
  - RN-006
commands:
- name: CreatePriceList
  generatable: true
- name: AddPriceEntry
  generatable: true
- name: UpdatePriceEntry
  generatable: true
- name: UpdatePriceList
  generatable: false
  custom_reason: Effective-dated versioning and snapshot freeze.
- name: PublishPriceList
  generatable: false
  custom_reason: Cross-aggregate item validation and overlap detection.
- name: DeprecatePriceList
  generatable: true
```
