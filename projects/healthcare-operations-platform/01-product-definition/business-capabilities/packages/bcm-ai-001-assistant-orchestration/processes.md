---
id: HOP-PROC-BCM-AI-001
format: markdown_structured_payload
type: process-model
name: Assistant Orchestration Processes
version: 1.0.0
status: modeled
---

# Assistant Orchestration Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-AI-001
  type: process-model
  status: modeled
capability_id: BCM-AI-001
processes:
  - id: PROC-AI-001
    name: Assistant Orchestration process 1
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
  - id: PROC-AI-002
    name: Assistant Orchestration process 2
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
```
