---
id: HOP-TRACE-BCM-QLT-001
format: markdown_structured_payload
type: traceability
name: Internal Quality Controls Traceability
version: 0.1.0
status: modeled
---

# Internal Quality Controls Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-QLT-001
  type: traceability
  name: Internal Quality Controls Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-001
traces:
  capability_map:
    bcm_001: BCM-QLT-001
    domain: DOM-09 Quality
  dependency_map:
    bcm_002_profile: inventory_quality
    required_capabilities:
    - BCM-INV-001
    - BCM-INV-003
    - BCM-INV-007
    - BCM-SVC-002
    - BCM-SVC-007
    - BCM-ORG-003
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-008
  domain_foundation:
    bounded_context: inventory-procurement
    aggregate_reference: QualityControlRun (new entity owned by this capability; no
      prior aggregate of this name existed in aggregate-catalog.md). Does not duplicate
      AGG-013 InventoryItem or AGG-009 LaboratoryResult; both are referenced by id
      only.
    context_relationships:
    - REL-CTX-007
    cross_context_read_only_reference: AGG-009 LaboratoryResult (laboratory-results
      bounded context; read-only, never mutated)
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-ID-003
    - VO-ID-004
    - VO-007
    downstream_capability_note: BCM-LAB-008 Technical Validation (MVP-MOD-006, already
      closed) is this capability's declared downstream_dependencies entry in capability-dependency-map.md.
      This package publishes QualityControlRunRecorded for BCM-LAB-008 to consume;
      enforcing any release gate on out_of_control status remains BCM-LAB-008's own
      responsibility and is out of scope here.
  brm_alignment:
  - rule: BRM-001-R018
    alignment: Audit records are append-only; every QC run and override is audited
      (RN-005).
  hrp_alignment:
  - process: not_yet_defined_in_HRP-001
    capability_role: Self-contained processes.md; tracked as a non-blocking documentation
      gap.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-IQC-001-01
  - rule: RN-002
    tests:
    - TST-IQC-001-02
  - rule: RN-003
    tests:
    - TST-IQC-001-03
  - rule: RN-004
    tests:
    - TST-IQC-001-04
  - rule: RN-005
    tests:
    - TST-IQC-001-05
  processes_to_commands:
  - process: PRC-IQC-001-01
    commands:
    - RecordQualityControlRun
  - process: PRC-IQC-001-02
    commands:
    - OverrideAcceptanceDecision
  api_to_permissions:
  - operation: recordQualityControlRun
    scope: quality.internalcontrol.manage
  - operation: overrideAcceptanceDecision
    scope: quality.internalcontrol.manage
  - operation: listQualityControlRuns
    scope: quality.internalcontrol.read
  events_to_audit:
  - event: QualityControlRunRecorded
    audit_sink: BCM-PLT-007
  - event: QualityControlOverrideRecorded
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-IQC-001-01
    operations:
    - recordQualityControlRun
    - overrideAcceptanceDecision
    - listQualityControlRuns
    - getQualityControlRun
  consumed_by_capabilities:
  - capability: BCM-LAB-008
    relationship: Reads QualityControlRunRecorded/out_of_control status as a technical-validation
      signal (BCM-LAB-008's own enforcement, not implemented here).
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
