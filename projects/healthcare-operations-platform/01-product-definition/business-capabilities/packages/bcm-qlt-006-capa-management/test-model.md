---
id: HOP-TST-BCM-QLT-006
format: markdown_structured_payload
type: test-model
name: CAPA Management Test Model
version: 0.1.0
status: modeled
---

# Capa Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TST-BCM-QLT-006
  type: test-model
  name: CAPA Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-006
test_cases:
- id: TST-CAP-001
  name: Require RCA Before Action Plan Submission
  type: unit
  target_rule: RN-CAP-001
  description: Verify that status transition to action_plan_pending is rejected if
    root cause summary is missing.
- id: TST-CAP-002
  name: Prevent Self-Approval of Action Plan
  type: unit
  target_rule: RN-CAP-002
  description: Verify that assigned investigator cannot approve their own CAPA action
    plan.
- id: TST-CAP-003
  name: Enforce Minimum Grace Period for Effectiveness
  type: unit
  target_rule: RN-CAP-003
  description: Verify that effectiveness verification fails if attempted prior to
    14 days post action item completion.
- id: TST-CAP-004
  name: CAPA Lifecycle State Machine Flow
  type: integration
  description: Verify complete state transitions from draft -> investigating -> action_plan_approved
    -> in_execution -> verification_pending -> closed.
```
