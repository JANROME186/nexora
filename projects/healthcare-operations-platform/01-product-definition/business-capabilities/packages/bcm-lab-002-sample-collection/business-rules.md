---
id: HOP-BR-BCM-LAB-002
format: markdown_structured_payload
type: business-rules
name: Sample Collection Business Rules
version: 0.1.0
status: modeled
---

# Sample Collection Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-LAB-002
  type: business-rules
  name: Sample Collection Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A sample must be associated with a valid, accepted DiagnosticOrder line
    before it can be collected; collecting against a draft, cancelled or completed
    order is rejected.
  applies_to: Sample
  enforcement_point: command:CollectSample
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability status check against BCM-LAB-001's
    DiagnosticOrder and order-line state.
  brm_alignment: BRM-001-R009
  test_refs:
  - TST-COL-002-01
- id: RN-002
  statement: A sample must carry a resolvable PatientIdentitySnapshot and SampleRequirementSnapshot
    at collection time; the aggregate never resolves these fields by live lookup.
  applies_to: Sample
  enforcement_point: command:CollectSample
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Snapshot capture must copy versioned fields from the order's own
    snapshot and from BCM-SVC-007's published SampleRequirement at a single consistent
    point in time.
  brm_alignment: BRM-001-R009
  test_refs:
  - TST-COL-002-02
- id: RN-003
  statement: A sample must carry a traceable identification (barcode or manual specimen
    identifier) before it can be marked received or processed; an unidentified sample
    can be collected but cannot advance past collection.
  applies_to: Sample
  enforcement_point: command:ReceiveSampleAtLaboratory, command:MarkSampleInProcess
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability confirmation that BCM-LAB-003 has assigned
    labelInfo before the receiving or processing capability can transition status.
  test_refs:
  - TST-COL-002-03
- id: RN-004
  statement: Every Sample state transition must append a ChainOfCustodyEvent recording
    actor, timestamp and branch; transitions without a custody event are rejected
    atomically.
  applies_to: Sample
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires transactional guarantee that the custody event append and
    the state transition succeed or fail together.
  brm_alignment: BRM-001-R018
  test_refs:
  - TST-COL-002-04
- id: RN-005
  statement: A sample rejected at collection or at reception must record a structured
    SampleRejectionReason with a reason code; free-text-only rejection is not accepted,
    and a rejected sample must never advance to in_process.
  applies_to: Sample
  enforcement_point: command:RejectSampleAtCollection
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Reason-code validation and terminal-state enforcement require domain
    logic beyond a generated field check.
  brm_alignment: BRM-001-R010
  test_refs:
  - TST-COL-002-05
- id: RN-006
  statement: Only BCM-LAB-002, BCM-LAB-003 and BCM-LAB-005 may mutate Sample state;
    each is restricted to its own named field set (collectionData and rejection-at-collection
    for this capability; labelInfo for BCM-LAB-003; receptionRecord, rejection-at-reception
    and disposal for BCM-LAB-005).
  applies_to: Sample
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement mirrors the aggregate ownership
    rule already applied to DiagnosticOrder (BCM-LAB-001, RN-004) and to AGG-008's
    forbidden_mutators outside orders-samples.
  test_refs:
  - TST-COL-002-06
- id: RN-007
  statement: Sample commands must execute within the actor's tenant, laboratory and
    branch scope.
  applies_to: Sample
  enforcement_point: authorization:sample.collect, authorization:sample.manage
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-COL-002-07
- id: RN-008
  statement: Sample domain events must include actor identity, branch, order reference
    and collection method.
  applies_to: Sample
  enforcement_point: event:SampleCollected
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-COL-002-08
- id: RN-009
  statement: Clinical evidence on a Sample (collection data, rejection reason, chain
    of custody, snapshots) must never be deleted; corrections after collection require
    an explicit new chain-of-custody event, never in-place field mutation of historical
    data.
  applies_to: Sample
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires an append-only persistence strategy for collection evidence
    rather than destructive update.
  test_refs:
  - TST-COL-002-09
enforcement_summary:
  generatable_rules:
  - RN-007
  - RN-008
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
  - RN-006
  - RN-009
```
