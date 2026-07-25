---
id: HOP-TRACE-BCM-LAB-003
format: markdown_structured_payload
type: traceability
name: Sample Labeling Traceability
version: 0.1.0
status: modeled
---

# Sample Labeling Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-LAB-003
  type: traceability
  name: Sample Labeling Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-003
traces:
  capability_map:
    bcm_001: BCM-LAB-003
    domain: DOM-05 Clinical Operations
  dependency_map:
    bcm_002_profile: clinical_operations
    required_capabilities:
    - BCM-LAB-002
    - BCM-SVC-007
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-005
  domain_foundation:
    bounded_context: orders-samples
    aggregate_reference: AGG-008 Sample (owned by BCM-LAB-002)
    context_relationships:
    - REL-CTX-003
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-003
    - VO-ID-008
    - VO-007
  brm_alignment:
  - rule: BRM-001-R009
    alignment: A confirmed label is a required identification field before reception
      or processing (RN-001, RN-002).
  - rule: BRM-001-R018
    alignment: Label print, confirmation and reprint events are audited (RN-006).
  hrp_alignment:
  - process: HRP-001-P05 Sample Collection and Processing
    capability_role: Print or confirm sample label segment of the sample lifecycle
      process.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-LBL-003-01
  - rule: RN-002
    tests:
    - TST-LBL-003-02
  - rule: RN-003
    tests:
    - TST-LBL-003-03
  - rule: RN-004
    tests:
    - TST-LBL-003-04
  - rule: RN-005
    tests:
    - TST-LBL-003-05
  - rule: RN-006
    tests:
    - TST-LBL-003-06
  processes_to_commands:
  - process: PRC-LBL-003-01
    commands:
    - PrintSpecimenLabel
  - process: PRC-LBL-003-02
    commands:
    - ConfirmSpecimenLabel
  - process: PRC-LBL-003-03
    commands:
    - RequestLabelReprint
  api_to_permissions:
  - operation: printSpecimenLabel
    scope: sample.label
  - operation: confirmSpecimenLabel
    scope: sample.label
  - operation: requestLabelReprint
    scope: sample.label
  - operation: getLabelPrintJob
    scope: sample.read
  events_to_audit:
  - event: SpecimenLabelPrinted
    audit_sink: BCM-PLT-007
  - event: SpecimenLabelAssigned
    audit_sink: BCM-PLT-007
  - event: SpecimenLabelReprinted
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-LBL-003-01
    operations:
    - printSpecimenLabel
    - confirmSpecimenLabel
    - requestLabelReprint
  consumed_by_capabilities:
  - capability: BCM-LAB-005
    relationship: Sample reception requires a confirmed labelInfo before receiving
      or rejecting.
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
