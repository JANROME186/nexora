---
id: HOP-BR-BCM-ATT-004
format: markdown_structured_payload
type: business-rules
name: Admission Management Business Rules
version: 0.1.0
status: modeled
---

# Admission Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-ATT-004
  type: business-rules
  name: Admission Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-004
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: An admission request can only be started from a ReceptionVisit with identityConfirmed
    true.
  applies_to: AdmissionRequest
  enforcement_point: command:StartAdmissionRequest
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability state check against BCM-ATT-003 ReceptionVisit.
  test_refs:
  - TST-ADM-004-01
- id: RN-002
  statement: An admission request cannot reach ready_for_order status unless catalogSelection
    is non-empty and every item is a published test or panel.
  applies_to: AdmissionRequest
  enforcement_point: command:MarkAdmissionReady
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability validation against BCM-SVC-001/002 publication
    state.
  test_refs:
  - TST-ADM-004-02
- id: RN-003
  statement: An admission request cannot commit to an order without consentConfirmed
    and, when the tenant requires it, sampleRequirementsAcknowledged.
  applies_to: AdmissionRequest
  enforcement_point: command:CommitAdmissionRequest
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Tenant policy defines which acknowledgements are mandatory before
    commit.
  test_refs:
  - TST-ADM-004-03
- id: RN-004
  statement: Committing an admission request must invoke BCM-LAB-001 CreateDiagnosticOrder,
    PriceDiagnosticOrder and AcceptDiagnosticOrder rather than persisting order state
    directly.
  applies_to: AdmissionRequest
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability delegation boundary mirroring the BCM-ATT-002 /
    BCM-PER-002 pattern.
  test_refs:
  - TST-ADM-004-04
- id: RN-005
  statement: Admission commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: AdmissionRequest
  enforcement_point: authorization:admission.manage
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADM-004-05
- id: RN-006
  statement: An admission request that fails completeness validation must be rejected
    with a recorded reason, not silently discarded.
  applies_to: AdmissionRequest
  enforcement_point: command:RejectAdmissionRequest
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADM-004-06
- id: RN-007
  statement: Admission audit events must include actor identity, branch and the resulting
    order id when committed.
  applies_to: AdmissionRequest
  enforcement_point: event:AdmissionRequestCommitted
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADM-004-07
enforcement_summary:
  generatable_rules:
  - RN-005
  - RN-006
  - RN-007
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
```
