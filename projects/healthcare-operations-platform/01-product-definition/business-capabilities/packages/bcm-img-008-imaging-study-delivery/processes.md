---
id: HOP-PROC-BCM-IMG-008
format: markdown_structured_payload
type: processes
name: Imaging Study Delivery Processes
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-IMG-008
  type: processes
  name: Imaging Study Delivery Processes
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
processes:
  - id: HRP-006-P08
    name: Imaging Study Delivery Standard Process
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
