---
id: HOP-TRACE-BCM-RES-006
format: markdown_structured_payload
type: traceability
name: Critical Results Traceability
version: 0.1.0
status: modeled
---

# Critical Results Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-RES-006
  type: traceability
  name: Critical Results Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
traces:
  capability_map:
    bcm_001: BCM-RES-006
    domain: DOM-07 Results
  dependency_map:
    bcm_002_profile: results_delivery
    required_capabilities:
    - BCM-LAB-008
    - BCM-RES-001
    - BCM-RES-007
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities: []
  domain_foundation:
    bounded_context: laboratory-results
    aggregate_reference: CriticalResultEscalation (new entity owned by this capability);
      AGG-009 LaboratoryResult read-only
    context_relationships:
    - REL-CTX-004
    - REL-CTX-012
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-ID-004
    - VO-ID-009
    - VO-007
  brm_alignment:
  - rule: BRM-001-R013
    alignment: This capability is the traceable escalation record itself, created
      unconditionally for every critical flag (RN-001).
  - rule: BRM-001-R018
    alignment: Every escalation lifecycle transition is audited (RN-005, RN-006).
  hrp_alignment:
  - process: HRP-001-P06 Result Validation and Release
    capability_role: Traceable escalation-record segment triggered by the critical-result
      flag produced during that process.
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-CRR-006-01
  - rule: RN-002
    tests:
    - TST-CRR-006-02
  - rule: RN-003
    tests:
    - TST-CRR-006-03
  - rule: RN-004
    tests:
    - TST-CRR-006-04
  - rule: RN-005
    tests:
    - TST-CRR-006-05
  - rule: RN-006
    tests:
    - TST-CRR-006-06
  processes_to_commands:
  - process: PRC-CRR-006-01
    commands:
    - CreateCriticalResultEscalation
  - process: PRC-CRR-006-02
    commands:
    - AcknowledgeCriticalResult
  - process: PRC-CRR-006-03
    commands:
    - EscalateCriticalResult
  api_to_permissions:
  - operation: listOpenEscalations
    scope: escalation.read
  - operation: acknowledgeCriticalResult
    scope: escalation.manage
  events_to_audit:
  - event: CriticalResultEscalationCreated
    audit_sink: BCM-PLT-007
  - event: CriticalResultAcknowledged
    audit_sink: BCM-PLT-007
  - event: CriticalResultEscalated
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-CRR-006-01
    operations:
    - listOpenEscalations
  - screen: SCR-CRR-006-02
    operations:
    - acknowledgeCriticalResult
  consumed_by_capabilities:
  - capability: BCM-RES-007
    relationship: Result notifications composes and re-composes notification requests
      from CriticalResultEscalationCreated/CriticalResultEscalated.
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/results-and-digital-delivery/MVP-MOD-007-DEF-validation.md
  backlog_items:
    definition: MVP-MOD-007-DEF
    definition_status: closed
    compilation: MVP-MOD-007-BE-001
    compilation_status: closed
    custom_rules: MVP-MOD-007-BE-002
    custom_rules_status: closed
    ui: MVP-MOD-007-FE-001
    ui_status: closed
    validation: MVP-MOD-007-QA-001
    validation_status: closed
    closeout: MVP-MOD-007-CLOSEOUT
    closeout_status: closed
```
