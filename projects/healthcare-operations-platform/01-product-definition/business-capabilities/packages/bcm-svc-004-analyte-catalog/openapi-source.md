---
id: HOP-API-SRC-BCM-SVC-004
format: markdown_structured_payload
type: openapi-source
name: Analyte Catalog API Source Model
version: 0.1.0
status: modeled
---

# Analyte Catalog Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-SVC-004
  type: openapi-source
  name: Analyte Catalog API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
  note: 'Source contract model. Rendered OpenAPI, controllers, DTOs and SDKs are generated
    outputs declared in generation-plan.md.

    '
api:
  base_path: /api/catalog/analytes
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - catalog.analyte.read
resources:
- name: AnalyteDefinition
  operations:
  - id: listAnalytes
    method: GET
    path: /
    scopes:
    - catalog.analyte.read
    generatable: true
  - id: getAnalyte
    method: GET
    path: /{analyteId}
    scopes:
    - catalog.analyte.read
    generatable: true
  - id: createAnalyte
    method: POST
    path: /
    scopes:
    - catalog.analyte.write
    generatable: true
  - id: updateAnalyte
    method: PUT
    path: /{analyteId}
    scopes:
    - catalog.analyte.write
    generatable: true
  - id: publishAnalyte
    method: POST
    path: /{analyteId}/publish
    scopes:
    - catalog.analyte.publish
    generatable: false
    custom_reason: Snapshot freeze and dependent ripple flagging are custom rules.
  - id: deprecateAnalyte
    method: POST
    path: /{analyteId}/deprecate
    scopes:
    - catalog.analyte.publish
    generatable: true
  - id: getPublishedAnalyteSnapshot
    method: GET
    path: /{analyteId}/published-snapshot
    scopes:
    - catalog.analyte.read
    generatable: false
    custom_reason: Returns frozen version-aware snapshot for result capture and ranges.
schemas_source:
- AnalyteDefinition
- AnalyteResultConstraint
- AnalyteCodedValue
- PublishedAnalyteSnapshot
error_model:
  standard: rfc7807
  domain_errors:
  - code: CATALOG_ANALYTE_CODE_CONFLICT
    maps_to_rule: RN-001
  - code: CATALOG_ANALYTE_UNIT_REQUIRED
    maps_to_rule: RN-002
  - code: CATALOG_ANALYTE_CODED_VALUE_REQUIRED
    maps_to_rule: RN-003
  - code: CATALOG_ANALYTE_CONSTRAINT_INVALID
    maps_to_rule: RN-005
```
