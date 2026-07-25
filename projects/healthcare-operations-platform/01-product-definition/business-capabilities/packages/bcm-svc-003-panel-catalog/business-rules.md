---
id: HOP-BR-BCM-SVC-003
format: markdown_structured_payload
type: business-rules
name: Panel Catalog Business Rules
version: 0.1.0
status: modeled
---

# Panel Catalog Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-SVC-003
  type: business-rules
  name: Panel Catalog Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-003
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A panel must have a unique code within its laboratory scope.
  applies_to: PanelDefinition
  enforcement_point: command:CreatePanelDefinition, command:UpdatePanelDefinition
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-003-01
- id: RN-002
  statement: A panel must contain at least two member tests before publication.
  applies_to: PanelDefinition
  enforcement_point: command:PublishPanelDefinition
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-003-02
- id: RN-003
  statement: A panel cannot be published if any member test is not published.
  applies_to: PanelDefinition
  enforcement_point: command:PublishPanelDefinition
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-aggregate member test publication validation.
  test_refs:
  - TST-SVC-003-03
- id: RN-004
  statement: A published panel version is immutable; edits create a new draft version.
  applies_to: PanelDefinition
  enforcement_point: command:UpdatePanelDefinition
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Snapshot copy and version increment logic.
  test_refs:
  - TST-SVC-003-04
- id: RN-005
  statement: Only users with catalog authoring permission may create, update or publish
    panels.
  applies_to: PanelDefinition
  enforcement_point: authorization:catalog.panel.write
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-003-05
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-002
  - RN-005
  custom_implementation_rules:
  - RN-003
  - RN-004
```
