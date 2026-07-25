---
id: HOP-API-SRC-BCM-RES-006
format: markdown_structured_payload
type: openapi-source
name: Critical Results API Source Model
version: 0.1.0
status: modeled
---

# Critical Results Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-RES-006
  type: openapi-source
  name: Critical Results API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/results/critical-escalations
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - escalation.manage
    - escalation.read
resources:
- name: CriticalResultEscalation
  operations:
  - id: listOpenEscalations
    method: GET
    path: /
    scopes:
    - escalation.read
    generatable: true
  - id: acknowledgeCriticalResult
    method: POST
    path: /{escalationId}/acknowledge
    scopes:
    - escalation.manage
    generatable: false
    custom_reason: Terminal-state guard requiring both acknowledgement fields.
schemas_source:
- CriticalResultEscalation
error_model:
  standard: rfc7807
  domain_errors:
  - code: ESCALATION_MANDATORY_CREATION_VIOLATION
    maps_to_rule: RN-001
  - code: ESCALATION_ACKNOWLEDGEMENT_INCOMPLETE
    maps_to_rule: RN-003
  - code: ESCALATION_BOUNDARY_VIOLATION
    maps_to_rule: RN-004
  - code: ESCALATION_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
