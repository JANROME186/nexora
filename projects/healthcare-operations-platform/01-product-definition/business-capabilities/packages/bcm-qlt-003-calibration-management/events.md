---
id: HOP-EVT-BCM-QLT-003
format: markdown_structured_payload
type: events
name: Calibration Management Events
version: 0.1.0
status: modeled
---

# Calibration Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-QLT-003
  type: events
  name: Calibration Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
domain_events:
- name: CalibrationRecorded
  description: A calibration event was appended to an equipment item's calibrationRecord.
  payload:
  - calibrationEventId
  - inventoryItemId
  - result
  - nextDueDate
  audit: true
- name: CalibrationFailed
  description: A calibration event resulted in a fail outcome.
  payload:
  - calibrationEventId
  - inventoryItemId
  audit: true
integration_events:
  published:
  - name: CalibrationFailed
    description: Signals BCM-QLT-004 Equipment Management to transition availabilityStatus
      to out_of_service.
    consumers:
    - inventory-procurement
  consumed: []
published_language: []
```
