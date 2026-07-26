---
id: HOP-PROC-BCM-AI-003
format: markdown_structured_payload
type: process-model
name: Result and Case Summaries Processes
version: 1.0.0
status: modeled
---

# Result and Case Summaries Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-AI-003
  type: process-model
  status: modeled
capability_id: BCM-AI-003
processes:
  - id: PROC-AI-005
    name: Result and Case Summaries process 1
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
  - id: PROC-AI-006
    name: Result and Case Summaries process 2
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
```
