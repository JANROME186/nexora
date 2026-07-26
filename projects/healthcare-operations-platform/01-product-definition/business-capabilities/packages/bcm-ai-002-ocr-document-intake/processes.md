---
id: HOP-PROC-BCM-AI-002
format: markdown_structured_payload
type: process-model
name: OCR Document Intake Processes
version: 1.0.0
status: modeled
---

# OCR Document Intake Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-AI-002
  type: process-model
  status: modeled
capability_id: BCM-AI-002
processes:
  - id: PROC-AI-003
    name: OCR Document Intake process 1
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
  - id: PROC-AI-004
    name: OCR Document Intake process 2
    steps:
    - capture tenant, actor, purpose and allowed source context
    - enforce AI safety policy and provider routing policy
    - prepare draft output with citations and confidence metadata
    - route to human review when policy or confidence requires it
    - persist audit evidence
```
