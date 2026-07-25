---
id: HOP-TRACE-BCM-LAB-005
format: markdown_structured_payload
type: traceability
name: Sample Reception Traceability
version: 0.1.0
status: modeled
---

# Sample Reception Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-LAB-005
  type: traceability
  name: Sample Reception Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-005
traces:
  capability_map:
    bcm_001: BCM-LAB-005
    domain: DOM-05 Clinical Operations
  dependency_map:
    bcm_002_profile: clinical_operations
    required_capabilities:
    - BCM-LAB-002
    - BCM-LAB-003
    - BCM-ORG-003
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-006
  domain_foundation:
    bounded_context: orders-samples
    aggregate_reference: AGG-008 Sample (owned by BCM-LAB-002)
    context_relationships:
    - REL-CTX-003
    - REL-CTX-004
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-003
    - VO-ID-004
    - VO-ID-008
    - VO-007
  brm_alignment:
  - rule: BRM-001-R009
    alignment: Reception verifies the sample carries a confirmed label and traceable
      collection data before acceptance (RN-001).
  - rule: BRM-001-R010
    alignment: A sample rejected at reception (hemolysis, wrong container, insufficient
      volume) blocks dependent result release and cannot be processed (RN-002, RN-003).
  - rule: BRM-001-R018
    alignment: Reception, rejection and disposal events are audited (RN-006, RN-007).
  hrp_alignment:
  - process: HRP-001-P05 Sample Collection and Processing
    capability_role: Receive sample and reject-at-reception segment of the sample
      lifecycle process.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-RCP-005-01
  - rule: RN-002
    tests:
    - TST-RCP-005-02
  - rule: RN-003
    tests:
    - TST-RCP-005-03
  - rule: RN-004
    tests:
    - TST-RCP-005-04
  - rule: RN-005
    tests:
    - TST-RCP-005-05
  - rule: RN-006
    tests:
    - TST-RCP-005-06
  - rule: RN-007
    tests:
    - TST-RCP-005-07
  processes_to_commands:
  - process: PRC-RCP-005-01
    commands:
    - ReceiveSampleAtLaboratory
  - process: PRC-RCP-005-02
    commands:
    - RejectSampleAtReception
  - process: PRC-RCP-005-03
    commands:
    - DisposeSample
  api_to_permissions:
  - operation: receiveSampleAtLaboratory
    scope: sample.receive
  - operation: rejectSampleAtReception
    scope: sample.receive
  - operation: disposeSample
    scope: sample.dispose
  - operation: listReceptionWorklist
    scope: sample.receive
  events_to_audit:
  - event: SampleReceived
    audit_sink: BCM-PLT-007
  - event: SampleRejected
    audit_sink: BCM-PLT-007
  - event: SampleDisposed
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-RCP-005-01
    operations:
    - listReceptionWorklist
  - screen: SCR-RCP-005-02
    operations:
    - receiveSampleAtLaboratory
    - rejectSampleAtReception
  - screen: SCR-RCP-005-03
    operations:
    - disposeSample
  consumed_by_capabilities:
  - capability: BCM-LAB-006
    relationship: Laboratory processing reads received samples to begin result capture.
  technical_debt_alignment:
  - debt: TD-BE-010
    alignment: Provides the real Sample.status values (received, rejected) that MVP-MOD-006-BE-002
      will use to replace the order-status proxy check in BCM-LAB-001's cancellation
      override.
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/laboratory-workflow/MVP-MOD-006-DEF-validation.md
  backlog_items:
    definition: MVP-MOD-006-DEF
    definition_status: modeled
    compilation: MVP-MOD-006-BE-001
    compilation_status: closed
    custom_rules: MVP-MOD-006-BE-002
    custom_rules_status: closed
    ui: MVP-MOD-006-FE-001
    ui_status: closed
    validation: MVP-MOD-006-QA-001
    validation_status: closed
    closeout: MVP-MOD-006-CLOSEOUT
    closeout_status: closed
```
