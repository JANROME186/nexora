---
id: HOP-TRACE-BCM-QLT-003
format: markdown_structured_payload
type: traceability
name: Calibration Management Traceability
version: 0.1.0
status: modeled
---

# Calibration Management Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-QLT-003
  type: traceability
  name: Calibration Management Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
traces:
  capability_map:
    bcm_001: BCM-QLT-003
    domain: DOM-09 Quality
  dependency_map:
    bcm_002_profile: inventory_quality
    required_capabilities:
    - BCM-INV-001
    - BCM-QLT-004
    - BCM-SVC-002
    - BCM-SVC-007
    - BCM-ORG-003
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-006
    - BCM-QLT-004
  domain_foundation:
    bounded_context: inventory-procurement
    aggregate_reference: AGG-013 InventoryItem (owned by BCM-INV-001; calibrationRecord
      delegated to this capability)
    context_relationships:
    - REL-CTX-007
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-003
    - VO-ID-004
    - VO-007
  brm_alignment: []
  hrp_alignment:
  - process: not_yet_defined_in_HRP-001
    capability_role: Self-contained processes.md; tracked as a non-blocking documentation
      gap.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-CAL-003-01
  - rule: RN-002
    tests:
    - TST-CAL-003-02
  - rule: RN-003
    tests:
    - TST-CAL-003-03
  - rule: RN-004
    tests:
    - TST-CAL-003-04
  - rule: RN-005
    tests:
    - TST-CAL-003-05
  processes_to_commands:
  - process: PRC-CAL-003-01
    commands:
    - RecordCalibration
  api_to_permissions:
  - operation: recordCalibration
    scope: quality.calibration.manage
  - operation: listCalibrations
    scope: quality.calibration.read
  events_to_audit:
  - event: CalibrationRecorded
    audit_sink: BCM-PLT-007
  - event: CalibrationFailed
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-CAL-003-01
    operations:
    - recordCalibration
    - listCalibrations
  consumed_by_capabilities:
  - capability: BCM-QLT-004
    relationship: Consumes CalibrationFailed to transition equipmentProfile.availabilityStatus.
  - capability: BCM-LAB-006
    relationship: Reads calibration currency for laboratory processing readiness.
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/inventory-and-internal-quality/COM-MOD-010-DEF-validation.md
  backend_qa_evidence: ../../../../08-qa/qa/inventory-and-internal-quality/COM-MOD-010-BE-002-validation.md
  backend_security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-010-BE-002/security-quality-evidence.md
  backlog_items:
    definition: COM-MOD-010-DEF
    definition_status: closed
    compilation: COM-MOD-010-BE-002
    compilation_status: closed
    custom_rules: COM-MOD-010-BE-002
    custom_rules_status: closed
    ui: COM-MOD-010-FE-001
    ui_status: closed
    validation: COM-MOD-010-QA-001
    validation_status: closed
    closeout: COM-MOD-010-CLOSEOUT
    closeout_status: closed
```
