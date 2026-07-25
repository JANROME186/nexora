---
id: HOP-TRACE-BCM-SVC-004
format: markdown_structured_payload
type: traceability
name: Analyte Catalog Traceability
version: 0.1.0
status: modeled
---

# Analyte Catalog Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-SVC-004
  type: traceability
  name: Analyte Catalog Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
traces:
  capability_map:
    bcm_001: BCM-SVC-004
    domain: DOM-03 Diagnostic Services
  dependency_map:
    bcm_002_profile: catalog
    required_capabilities:
    - BCM-ORG-001
    - BCM-ORG-002
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-006
    - BCM-RES-001
    - BCM-RES-006
    - BCM-SVC-006
  domain_foundation:
    bounded_context: catalog-test-configuration
    secondary_bounded_context: laboratory-results
    aggregate: AGG-006 TestDefinition
    secondary_aggregate: AGG-009 LaboratoryResult
    context_relationships:
    - REL-CTX-003
    - REL-CTX-004
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-007
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-SVC-004-01
  - rule: RN-002
    tests:
    - TST-SVC-004-02
  - rule: RN-003
    tests:
    - TST-SVC-004-03
  - rule: RN-004
    tests:
    - TST-SVC-004-04
  - rule: RN-005
    tests:
    - TST-SVC-004-05
  - rule: RN-006
    tests:
    - TST-SVC-004-06
  - rule: RN-007
    tests:
    - TST-SVC-004-07
  processes_to_commands:
  - process: PRC-SVC-004-01
    commands:
    - CreateAnalyte
  - process: PRC-SVC-004-02
    commands:
    - PublishAnalyte
  - process: PRC-SVC-004-03
    commands:
    - UpdateAnalyte
  api_to_permissions:
  - operation: createAnalyte
    scope: catalog.analyte.write
  - operation: publishAnalyte
    scope: catalog.analyte.publish
  events_to_audit:
  - event: AnalyteDefinitionPublished
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-SVC-004-02
    operations:
    - createAnalyte
    - updateAnalyte
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/catalog-test-configuration/MVP-MOD-002-DEF-validation.md
  backlog_items:
    definition: MVP-MOD-002-DEF
    compilation: MVP-MOD-002-BE-001
    custom_rules: MVP-MOD-002-BE-002
    ui: MVP-MOD-002-FE-001
```
