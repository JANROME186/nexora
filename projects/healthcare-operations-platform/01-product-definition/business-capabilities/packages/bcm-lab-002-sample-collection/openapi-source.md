---
id: HOP-API-SRC-BCM-LAB-002
format: markdown_structured_payload
type: openapi-source
name: Sample Collection API Source Model
version: 0.1.0
status: modeled
---

# Sample Collection Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-LAB-002
  type: openapi-source
  name: Sample Collection API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/clinical-operations/samples
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - sample.collect
    - sample.manage
resources:
- name: Sample
  operations:
  - id: listCollectionWorklist
    method: GET
    path: /collection-worklist
    scopes:
    - sample.collect
    generatable: true
  - id: getSample
    method: GET
    path: /{sampleId}
    scopes:
    - sample.read
    generatable: true
  - id: collectSample
    method: POST
    path: /
    scopes:
    - sample.collect
    generatable: false
    custom_reason: Multi-source snapshot capture and chain-of-custody initiation.
  - id: rejectSampleAtCollection
    method: POST
    path: /{sampleId}/reject-at-collection
    scopes:
    - sample.collect
    generatable: false
    custom_reason: Structured reason-code validation and terminal-state guard.
schemas_source:
- Sample
- PatientIdentitySnapshot
- SampleRequirementSnapshot
- SampleCollectionData
- SampleRejectionReason
- ChainOfCustodyEvent
error_model:
  standard: rfc7807
  domain_errors:
  - code: SAMPLE_ORDER_LINE_NOT_ACCEPTED
    maps_to_rule: RN-001
  - code: SAMPLE_SNAPSHOT_INCOMPLETE
    maps_to_rule: RN-002
  - code: SAMPLE_NOT_IDENTIFIABLE
    maps_to_rule: RN-003
  - code: SAMPLE_CUSTODY_EVENT_MISSING
    maps_to_rule: RN-004
  - code: SAMPLE_REJECTION_REASON_REQUIRED
    maps_to_rule: RN-005
  - code: SAMPLE_BOUNDARY_VIOLATION
    maps_to_rule: RN-006
  - code: SAMPLE_SCOPE_MISMATCH
    maps_to_rule: RN-007
```
