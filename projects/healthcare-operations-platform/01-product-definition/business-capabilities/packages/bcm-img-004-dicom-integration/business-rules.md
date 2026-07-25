---
id: HOP-RULES-BCM-IMG-004
format: markdown_structured_payload
type: business-rules
name: DICOM Integration Business Rules
version: 1.0.0
status: modeled
---

# DICOM Integration Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-004
  type: business-rules
  name: DICOM Integration Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
rules:
  - id: BR-BCM-IMG-004-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for DICOM Integration.
    error_code: ERR_BCM_IMG_004_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-004-001: Operational Validation
All transactions submitted to `BCM-IMG-004` must satisfy operational state and tenant permission preconditions.
