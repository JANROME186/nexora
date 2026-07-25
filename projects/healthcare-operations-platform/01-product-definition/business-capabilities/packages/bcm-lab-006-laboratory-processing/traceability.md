---
id: HOP-TRACE-BCM-LAB-006
format: markdown_structured_payload
type: traceability
name: Laboratory Processing Traceability
version: 0.1.0
status: modeled
---

# Laboratory Processing Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-LAB-006
  type: traceability
  name: Laboratory Processing Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
traces:
  capability_map:
    bcm_001: BCM-LAB-006
    domain: DOM-05 Clinical Operations
  dependency_map:
    bcm_002_profile: clinical_operations
    required_capabilities:
    - BCM-LAB-005
    - BCM-SVC-004
    - BCM-SVC-006
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-008
  domain_foundation:
    bounded_context: laboratory-results
    aggregate_reference: AGG-009 LaboratoryResult
    context_relationships:
    - REL-CTX-004
    - REL-CTX-011
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-ID-003
    - VO-ID-004
    - VO-ID-006
    - VO-ID-007
    - VO-ID-008
    - VO-ID-009
    - VO-007
  brm_alignment:
  - rule: BRM-001-R010
    alignment: Results cannot be captured against a rejected or unreceived sample
      (RN-001).
  - rule: BRM-001-R016
    alignment: Device messages must be normalized before reaching this capability's
      domain commands (RN-004).
  - rule: BRM-001-R017
    alignment: AI may read but never validate, release or amend LaboratoryResult (RN-006).
  - rule: BRM-001-R018
    alignment: Every capture, incident and submission event is audited (RN-007, RN-008).
  hrp_alignment:
  - process: HRP-001-P05 Sample Collection and Processing
    capability_role: Capture result value, record processing incident and submit-for-validation
      segment of the sample lifecycle process.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-LPR-006-01
  - rule: RN-002
    tests:
    - TST-LPR-006-02
  - rule: RN-003
    tests:
    - TST-LPR-006-03
  - rule: RN-004
    tests:
    - TST-LPR-006-04
  - rule: RN-005
    tests:
    - TST-LPR-006-05
  - rule: RN-006
    tests:
    - TST-LPR-006-06
  - rule: RN-007
    tests:
    - TST-LPR-006-07
  - rule: RN-008
    tests:
    - TST-LPR-006-08
  processes_to_commands:
  - process: PRC-LPR-006-01
    commands:
    - CaptureResultValue
  - process: PRC-LPR-006-02
    commands:
    - RecordProcessingIncident
  - process: PRC-LPR-006-03
    commands:
    - SubmitResultForValidation
  api_to_permissions:
  - operation: captureResultValue
    scope: result.capture
  - operation: recordProcessingIncident
    scope: result.capture
  - operation: submitResultForValidation
    scope: result.capture
  - operation: listProcessingWorklist
    scope: result.capture
  - operation: getLaboratoryResult
    scope: result.read
  events_to_audit:
  - event: ResultCaptured
    audit_sink: BCM-PLT-007
  - event: ProcessingIncidentRecorded
    audit_sink: BCM-PLT-007
  - event: ResultSubmittedForValidation
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-LPR-006-01
    operations:
    - listProcessingWorklist
  - screen: SCR-LPR-006-02
    operations:
    - captureResultValue
    - recordProcessingIncident
    - submitResultForValidation
  - screen: SCR-LPR-006-03
    operations:
    - getLaboratoryResult
  consumed_by_capabilities:
  - capability: BCM-LAB-008
    relationship: Technical validation reads the submitted LaboratoryResult and invokes
      PerformTechnicalValidation/FlagCriticalResult.
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
