---
id: HOP-TRACE-BCM-PLT-004
format: markdown_structured_payload
type: traceability
name: Integration Management Traceability
version: 0.1.0
status: modeled
---

# Integration Management Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-PLT-004
  type: traceability
  name: Integration Management Traceability
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
traces:
  capability_map:
    bcm_001: BCM-PLT-004
    domain: DOM-10 Platform
  dependency_map:
    bcm_002_profile: platform_extension
    required_capabilities:
    - BCM-PLT-001
    - BCM-PLT-006
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-IMG-004
    - BCM-AI-007
    - BCM-LAB-006
  domain_foundation:
    bounded_context: integration-interoperability
    aggregate_reference: IntegrationEndpoint (new platform aggregate; no prior aggregate
      existed for integration-interoperability in aggregate-catalog.md)
    context_relationships:
    - REL-CTX-011
    context_map_observation: context-map.md REL-CTX-011 already declares integration-interoperability
      as an anti-corruption-layer targeting all-core-contexts with published_language
      ExternalMessageEnvelope/NormalizedClinicalMessage/IntegrationAcknowledgement;
      this package reuses those exact published-language names rather than inventing
      new ones. No context-map.md edit was required.
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-007
  brm_alignment:
  - rule: BRM-001-R016
    alignment: Integration adapters cannot bypass validation; enforced by RN-001,
      RN-002.
  - rule: BRM-001-R018
    alignment: Audit records are append-only; every message lifecycle transition is
      audited (RN-005).
  hrp_alignment:
  - process: HRP-001-P08 Migration and Integration Dry Run
    capability_role: Receive, normalize and acknowledge external messages segment
      of the process (shared with BCM-PLT-005).
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-INT-004-01
  - rule: RN-002
    tests:
    - TST-INT-004-02
  - rule: RN-003
    tests:
    - TST-INT-004-03
  - rule: RN-004
    tests:
    - TST-INT-004-04
  - rule: RN-005
    tests:
    - TST-INT-004-05
  - rule: RN-006
    tests:
    - TST-INT-004-06
  processes_to_commands:
  - process: PRC-INT-004-01
    commands:
    - RegisterIntegrationEndpoint
  - process: PRC-INT-004-02
    commands:
    - ReceiveMessage
    - NormalizeMessage
  - process: PRC-INT-004-03
    commands:
    - AcknowledgeMessage
    - RetryMessage
  api_to_permissions:
  - operation: registerIntegrationEndpoint
    scope: integration.endpoint.manage
  - operation: listIntegrationEndpoints
    scope: integration.message.read
  - operation: receiveMessage
    scope: integration.endpoint.manage
  - operation: retryMessage
    scope: integration.endpoint.manage
  events_to_audit:
  - event: IntegrationEndpointRegistered
    audit_sink: BCM-PLT-007
  - event: MessageNormalizationFailed
    audit_sink: BCM-PLT-007
  - event: MessageDeadLettered
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-INT-004-01
    operations:
    - registerIntegrationEndpoint
    - listIntegrationEndpoints
    - retireIntegrationEndpoint
  - screen: SCR-INT-004-02
    operations:
    - getMessage
    - retryMessage
  consumed_by_capabilities:
  - capability: BCM-PLT-005
    relationship: Uses IntegrationAcknowledgement status for partner API delivery
      observability.
  - capability: BCM-LAB-006
    relationship: Receives normalized laboratory device result messages as a read-only
      future consumer.
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-DEF-validation.md
  compilation_evidence:
    backend_implementation: ../../../../../07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/integrationmanagement/
    schema: ../../../../../07-implementation/backend/src/main/resources/db/integration-interoperability/schema.sql
    qa_evidence: ../../../../08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-002-validation.md
    security_quality_evidence: ../../../../08-qa/security-quality/MVP-MOD-008-BE-002/security-quality-evidence.md
    notes: RegisterIntegrationEndpoint, listIntegrationEndpoints, retireIntegrationEndpoint
      compiled as generatable outputs. ReceiveMessage/NormalizeMessage/AcknowledgeMessage
      (CUS-INT-004-01/02/03) implemented via IntegrationAdapterPort + LocalDeterministicPassthroughIntegrationAdapter,
      with idempotent reprocessing keyed by (endpointId, externalMessageId). MVP-MOD-008-BE-002
      implemented CUS-INT-004-04 (bounded exponential-backoff retry that transitions
      an exhausted message to dead_lettered, RN-004) and CUS-INT-004-05 (a deterministic
      correlationId derived once at first receipt and propagated unchanged through
      IntegrationAdapterPort.acknowledgeMessage across every retry, RN-005). Real
      open-source protocol parser adoption (CUS-INT-004-06) remains an open, undebted
      gap per BE-001's original evaluation (the local deterministic adapter fully
      satisfies this capability's own scope without a real HL7v2/FHIR/ASTM/DICOM parser
      dependency).
  backlog_items:
    definition: MVP-MOD-008-DEF
    definition_status: closed
    compilation: MVP-MOD-008-BE-001
    compilation_status: closed
    custom_rules: MVP-MOD-008-BE-002
    custom_rules_status: closed
    ui: MVP-MOD-008-FE-001
    ui_status: pending
    validation: MVP-MOD-008-QA-001
    validation_status: pending
    closeout: MVP-MOD-008-CLOSEOUT
    closeout_status: pending
```
