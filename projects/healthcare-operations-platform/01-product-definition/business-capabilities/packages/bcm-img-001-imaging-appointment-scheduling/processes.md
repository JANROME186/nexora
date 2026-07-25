---
id: HOP-PROC-BCM-IMG-001
format: markdown_structured_payload
type: processes
name: Imaging Appointment Scheduling Processes
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-IMG-001
  type: processes
  name: Imaging Appointment Scheduling Processes
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
processes:
  - id: HRP-006-P01
    name: Imaging Appointment Scheduling Standard Process
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
