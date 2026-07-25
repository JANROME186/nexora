---
id: HOP-PROC-BCM-IMG-002
format: markdown_structured_payload
type: processes
name: Imaging Reception Processes
version: 1.0.0
status: modeled
---

# Imaging Reception Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-IMG-002
  type: processes
  name: Imaging Reception Processes
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
processes:
  - id: HRP-006-P02
    name: Imaging Reception Standard Process
    steps:
      - step_number: 1
        name: Intake / Trigger
        description: Initiates process workflow.
      - step_number: 2
        name: Execution
        description: Processes domain rules.
      - step_number: 3
        name: Handoff
        description: Emits domain events and updates records.
```
