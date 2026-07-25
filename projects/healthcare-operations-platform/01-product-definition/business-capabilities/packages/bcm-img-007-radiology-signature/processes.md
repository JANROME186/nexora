---
id: HOP-PROC-BCM-IMG-007
format: markdown_structured_payload
type: processes
name: Radiology Signature Processes
version: 1.0.0
status: modeled
---

# Radiology Signature Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-IMG-007
  type: processes
  name: Radiology Signature Processes
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
processes:
  - id: HRP-006-P07
    name: Radiology Signature Standard Process
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
