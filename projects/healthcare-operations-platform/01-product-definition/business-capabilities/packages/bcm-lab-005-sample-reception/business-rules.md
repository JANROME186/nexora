---
id: HOP-BR-BCM-LAB-005
format: markdown_structured_payload
type: business-rules
name: Sample Reception Business Rules
version: 0.1.0
status: modeled
---

# Sample Reception Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-LAB-005
  type: business-rules
  name: Sample Reception Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-005
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A sample cannot be received without a confirmed specimen label; an unlabeled
    sample presented at reception must be rejected, not received.
  applies_to: Sample
  enforcement_point: command:ReceiveSampleAtLaboratory
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability check that Sample.labelInfo
    is non-null, delegated from BCM-LAB-003.
  brm_alignment: BRM-001-R009
  test_refs:
  - TST-RCP-005-01
- id: RN-002
  statement: A sample with hemolysis, an unintact container, insufficient volume or
    a transport window violation must be rejected at reception with a matching structured
    reason code; it cannot be received.
  applies_to: Sample
  enforcement_point: command:ReceiveSampleAtLaboratory, command:RejectSampleAtReception
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: ReceptionConditionCheck evaluation against multiple physical criteria
    is not a simple field validation.
  brm_alignment: BRM-001-R010
  test_refs:
  - TST-RCP-005-02
- id: RN-003
  statement: A rejected sample is terminal for laboratory processing; it can only
    transition to disposed, never to received or in_process.
  applies_to: Sample
  enforcement_point: command:DisposeSample
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Terminal-state guard requires checking prior rejection status before
    allowing any further transition.
  brm_alignment: BRM-001-R010
  test_refs:
  - TST-RCP-005-03
- id: RN-004
  statement: Disposal must never delete prior collection, labeling, reception or rejection
    evidence; it only appends a DisposalRecord and a final chain-of-custody event.
  applies_to: Sample
  enforcement_point: command:DisposeSample
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires an append-only persistence strategy consistent with the
    aggregate's evidence-preservation invariant.
  test_refs:
  - TST-RCP-005-04
- id: RN-005
  statement: This capability may mutate only Sample.receptionRecord, rejection-at-reception
    fields and disposal fields; it must not write collectionData, labelInfo or any
    other Sample field.
  applies_to: Sample
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement mirrors the aggregate ownership
    rule declared by BCM-LAB-002 (RN-006).
  test_refs:
  - TST-RCP-005-05
- id: RN-006
  statement: Reception and disposal commands must execute within the actor's tenant,
    laboratory and branch scope.
  applies_to: Sample
  enforcement_point: authorization:sample.receive, authorization:sample.dispose
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-RCP-005-06
- id: RN-007
  statement: Reception and rejection domain events must include actor identity, branch,
    sample reference and condition-check outcome.
  applies_to: Sample
  enforcement_point: event:SampleReceived, event:SampleRejected
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-RCP-005-07
enforcement_summary:
  generatable_rules:
  - RN-006
  - RN-007
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
```
