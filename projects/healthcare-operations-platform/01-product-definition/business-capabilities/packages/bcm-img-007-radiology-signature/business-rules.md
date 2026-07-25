---
id: HOP-RULES-BCM-IMG-007
format: markdown_structured_payload
type: business-rules
name: Radiology Signature Business Rules
version: 1.0.0
status: modeled
---

# Radiology Signature Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-007
  type: business-rules
  name: Radiology Signature Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
rules:
  - id: BR-BCM-IMG-007-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for Radiology Signature.
    error_code: ERR_BCM_IMG_007_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-007-001: Operational Validation
All transactions submitted to `BCM-IMG-007` must satisfy operational state and tenant permission preconditions.
