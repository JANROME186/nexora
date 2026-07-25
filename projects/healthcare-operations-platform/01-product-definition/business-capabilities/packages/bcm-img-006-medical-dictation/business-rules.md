---
id: HOP-RULES-BCM-IMG-006
format: markdown_structured_payload
type: business-rules
name: Medical Dictation Business Rules
version: 1.0.0
status: modeled
---

# Medical Dictation Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-006
  type: business-rules
  name: Medical Dictation Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
rules:
  - id: BR-BCM-IMG-006-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for Medical Dictation.
    error_code: ERR_BCM_IMG_006_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-006-001: Operational Validation
All transactions submitted to `BCM-IMG-006` must satisfy operational state and tenant permission preconditions.
