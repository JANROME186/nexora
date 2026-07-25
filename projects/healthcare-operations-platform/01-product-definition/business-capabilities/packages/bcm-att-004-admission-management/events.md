---
id: HOP-EVT-BCM-ATT-004
format: markdown_structured_payload
type: events
name: Admission Management Events
version: 0.1.0
status: modeled
---

# Admission Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ATT-004
  type: events
  name: Admission Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-004
domain_events:
- name: AdmissionRequestStarted
  description: An admission request was created from a confirmed reception visit.
  payload:
  - admissionId
  - tenantId
  - branchId
  - visitId
  - actorId
  audit: true
- name: AdmissionMarkedReady
  description: The admission request completed catalog selection and clinical notes.
  payload:
  - admissionId
  - actorId
  audit: true
- name: AdmissionRequestCommitted
  description: The admission request committed a diagnostic order through BCM-LAB-001.
  payload:
  - admissionId
  - createdOrderId
  - actorId
  - branchId
  audit: true
- name: AdmissionRequestRejected
  description: The admission request was rejected before order commit.
  payload:
  - admissionId
  - rejectionReason
  - actorId
  audit: true
integration_events:
  published:
  - name: AdmissionRequestCommitted
    description: Signals cashier and clinical modules that a new order has been committed
      via admission.
    consumers:
    - cash-sales
    - orders-samples
  consumed:
  - name: ReceptionVisitReadyForAdmission
    source: BCM-ATT-003
  - name: TestDefinitionPublished
    source: BCM-SVC-002
  - name: PriceListPublished
    source: BCM-SVC-009
  - name: QuotationAccepted
    source: BCM-ATT-006
published_language:
- AdmissionRequestCommitted
```
