---
id: HOP-TRACE-BCM-SVC-007
format: markdown_structured_payload
type: traceability
name: Sample Catalog Traceability
version: 0.1.0
status: modeled
---

# Sample Catalog Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-SVC-007
  type: traceability
  name: Sample Catalog Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
traces:
  capability_map:
    bcm_001: BCM-SVC-007
    domain: DOM-03 Diagnostic Services
  dependency_map:
    bcm_002_profile: catalog
    required_capabilities:
    - BCM-ORG-001
    - BCM-ORG-002
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-LAB-002
    - BCM-LAB-003
    - BCM-LAB-005
    - BCM-SVC-002
  domain_foundation:
    bounded_context: catalog-test-configuration
    secondary_bounded_context: orders-samples
    aggregate: AGG-006 TestDefinition
    secondary_aggregate: AGG-008 Sample
    context_relationships:
    - REL-CTX-003
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-ID-008
    - VO-007
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-SVC-007-01
  - rule: RN-002
    tests:
    - TST-SVC-007-02
  - rule: RN-003
    tests:
    - TST-SVC-007-03
  - rule: RN-004
    tests:
    - TST-SVC-007-04
  - rule: RN-005
    tests:
    - TST-SVC-007-05
  - rule: RN-006
    tests:
    - TST-SVC-007-06
  processes_to_commands:
  - process: PRC-SVC-007-01
    commands:
    - CreateSampleType
  - process: PRC-SVC-007-02
    commands:
    - CreateSampleRequirement
  - process: PRC-SVC-007-03
    commands:
    - PublishSampleRequirement
  api_to_permissions:
  - operation: createSampleRequirement
    scope: catalog.sample.write
  - operation: publishSampleRequirement
    scope: catalog.sample.publish
  events_to_audit:
  - event: SampleRequirementPublished
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-SVC-007-03
    operations:
    - createSampleRequirement
    - updateSampleRequirement
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/catalog-test-configuration/MVP-MOD-002-DEF-validation.md
  backlog_items:
    definition: MVP-MOD-002-DEF
    compilation: MVP-MOD-002-BE-001
    custom_rules: MVP-MOD-002-BE-002
    ui: MVP-MOD-002-FE-001
```
