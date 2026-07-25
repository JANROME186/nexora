---
id: HOP-TST-BCM-QLT-002
format: markdown_structured_payload
type: test-model
name: External Quality Controls Test Model
version: 0.1.0
status: modeled
---

# External Quality Controls Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TST-BCM-QLT-002
  type: test-model
  name: External Quality Controls Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
test_cases:
- id: TST-EQC-001
  name: Register EQA Survey Entry
  type: unit
  target_rule: RN-EQC-001
  description: Verify that sample measurements cannot be entered without registering
    a valid survey cycle.
- id: TST-EQC-002
  name: Compute Z-Score and Rating
  type: unit
  target_rule: RN-EQC-002
  description: Verify z-score formula (measuredValue - peerGroupMean) / peerGroupSd
    and proper rating assignment.
- id: TST-EQC-003
  name: Trigger CAPA on Unacceptable Rating
  type: integration
  target_rule: RN-EQC-004
  description: Verify that registering an evaluation with |z| > 3.0 emits ExternalQualityOutOfRangeDetected
    and triggers BCM-QLT-006 CAPA.
- id: TST-EQC-004
  name: EQA Contract OpenAPI Validation
  type: contract
  description: Verify OpenAPI 3.0 request/response schemas for external quality endpoints.
```
