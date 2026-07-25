---
id: HOP-EVT-BCM-LAB-001
format: markdown_structured_payload
type: events
name: Diagnostic Order Management Events
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-001
  type: events
  name: Diagnostic Order Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
domain_events:
- name: DiagnosticOrderCreated
  description: A diagnostic order was created with immutable patient, doctor and branch
    snapshots.
  payload:
  - orderId
  - tenantId
  - branchId
  - actorId
  - intakeChannel
  - sourceReferenceId
  - patientSnapshotVersion
  - doctorSnapshotVersion
  - orderLineCount
  audit: true
- name: OrderPriced
  description: A pricing snapshot was captured for the order.
  payload:
  - orderId
  - priceListId
  - priceListVersion
  - totalAmount
  - actorId
  audit: true
- name: OrderAccepted
  description: The order was accepted for downstream clinical processing.
  payload:
  - orderId
  - actorId
  - branchId
  - acceptedAt
  audit: true
- name: OrderCancelled
  description: The order was cancelled.
  payload:
  - orderId
  - reasonCode
  - overrideJustification
  - actorId
  audit: true
- name: OrderCompleted
  description: All order lines reached a terminal clinical state.
  payload:
  - orderId
  - completedAt
  - actorId
  audit: true
integration_events:
  published:
  - name: DiagnosticOrderCreated
    description: Signals reception, admission and cashier modules of a new order.
    consumers:
    - reception
    - cash-sales
    - laboratory-results
  - name: OrderAccepted
    description: Signals sample collection that an order is ready for specimen handling.
    consumers:
    - orders-samples
    - laboratory-results
  - name: OrderCancelled
    description: Signals cashier and laboratory of order cancellation.
    consumers:
    - cash-sales
    - laboratory-results
  consumed:
  - name: PatientRegistrationCommitted
    source: BCM-ATT-002
  - name: DoctorCredentialVerified
    source: BCM-PER-003
  - name: TestDefinitionPublished
    source: BCM-SVC-002
  - name: PriceListPublished
    source: BCM-SVC-009
  - name: QuotationAccepted
    source: BCM-ATT-006
published_language:
- DiagnosticOrderCreated
- OrderPriced
- OrderAccepted
- OrderCancelled
- OrderCompleted
```
