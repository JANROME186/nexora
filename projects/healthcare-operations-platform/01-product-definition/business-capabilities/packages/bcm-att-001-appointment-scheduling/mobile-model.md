---
id: HOP-MOB-BCM-ATT-001
format: markdown_structured_payload
type: mobile-model
name: Appointment Scheduling Mobile Model
version: 0.1.0
status: deferred
---

# Appointment Scheduling Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-ATT-001
  type: mobile-model
  name: Appointment Scheduling Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-ATT-001
mobile_scope:
  status: required
  flows:
  - id: MOB-FLOW-APT-001
    name: Patient Mobile Check-In
    description: Self check-in upon branch arrival.
    screens:
    - MobileCheckInScreen
    - QRScannerComponent
  offline_expectations: local_caching_of_upcoming_appointment_data
```
