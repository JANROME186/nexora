---
id: HOP-TEST-BCM-IMG-004
format: markdown_structured_payload
type: test-model
name: DICOM Integration Test Model
version: 1.0.0
status: modeled
---

# DICOM Integration Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-IMG-004
  type: test-model
  name: DICOM Integration Test Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
test_suite:
  unit_tests:
    - test_case: test_bcm_img_004_validation
  integration_tests:
    - test_case: test_bcm_img_004_api_boundary
```
