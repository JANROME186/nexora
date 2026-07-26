---
id: HOP-PROC-BCM-AI-007
format: markdown_structured_payload
type: process-model
name: Model Provider Integration Processes
version: 1.0.0
status: modeled
---

# Model Provider Integration Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-AI-007
  type: process-model
  status: modeled
capability_id: BCM-AI-007
processes:
  - id: PROC-AI-013
    name: Model Provider Integration process 1
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
  - id: PROC-AI-014
    name: Model Provider Integration process 2
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
```
