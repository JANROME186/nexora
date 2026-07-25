# BPR-001 Patient Intake

Patient Intake describes the process from initial patient identification to creation of an order-ready patient profile.

## High-Level Flow

1. Identify patient.
2. Search for existing record.
3. Register or update patient profile.
4. Capture consent and guardian data when required.
5. Validate mandatory information.
6. Make patient available for quotation, order creation and billing.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: BPR-001
type: businessProcess
name: Patient Intake
status: draft
version: 0.15.0
owner: Business Architecture
capability: CAP-001
steps:
- id: STEP-001
  name: Identify patient
- id: STEP-002
  name: Search existing patient
- id: STEP-003
  name: Register or update patient
- id: STEP-004
  name: Capture consent or guardian data when required
- id: STEP-005
  name: Validate patient profile
- id: STEP-006
  name: Make patient available for orders
relations:
- type: realizes
  target: CAP-001
- type: emits
  target: EVT-001
- type: constrainedBy
  target: BR-001
- type: produces
  target: US-001
```
