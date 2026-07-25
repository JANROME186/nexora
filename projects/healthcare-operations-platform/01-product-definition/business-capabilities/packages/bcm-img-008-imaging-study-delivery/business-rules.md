---
id: HOP-RULES-BCM-IMG-008
format: markdown_structured_payload
type: business-rules
name: Imaging Study Delivery Business Rules
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-008
  type: business-rules
  name: Imaging Study Delivery Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
rules:
  - id: BR-BCM-IMG-008-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for Imaging Study Delivery.
    error_code: ERR_BCM_IMG_008_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-008-001: Operational Validation
All transactions submitted to `BCM-IMG-008` must satisfy operational state and tenant permission preconditions.
