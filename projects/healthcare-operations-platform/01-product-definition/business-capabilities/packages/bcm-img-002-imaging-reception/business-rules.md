---
id: HOP-RULES-BCM-IMG-002
format: markdown_structured_payload
type: business-rules
name: Imaging Reception Business Rules
version: 1.0.0
status: modeled
---

# Imaging Reception Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-002
  type: business-rules
  name: Imaging Reception Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
rules:
  - id: BR-BCM-IMG-002-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for Imaging Reception.
    error_code: ERR_BCM_IMG_002_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-002-001: Operational Validation
All transactions submitted to `BCM-IMG-002` must satisfy operational state and tenant permission preconditions.
