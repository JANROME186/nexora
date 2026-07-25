---
id: HOP-RULES-BCM-IMG-001
format: markdown_structured_payload
type: business-rules
name: Imaging Appointment Scheduling Business Rules
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-IMG-001
  type: business-rules
  name: Imaging Appointment Scheduling Business Rules
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
rules:
  - id: BR-BCM-IMG-001-001
    name: Operational Validation
    description: Enforces authorization and valid state transitions for Imaging Appointment Scheduling.
    error_code: ERR_BCM_IMG_001_VALIDATION_FAILED
```

## Rule Specification
### BR-BCM-IMG-001-001: Operational Validation
All transactions submitted to `BCM-IMG-001` must satisfy operational state and tenant permission preconditions.
