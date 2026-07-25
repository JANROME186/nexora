---
id: HOP-BR-BCM-SVC-001
format: markdown_structured_payload
type: business-rules
name: Diagnostic Service Catalog Business Rules
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-SVC-001
  type: business-rules
  name: Diagnostic Service Catalog Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A diagnostic service must have a unique code within its laboratory scope.
  applies_to: DiagnosticService
  enforcement_point: command:CreateDiagnosticService, command:UpdateDiagnosticService
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-001-01
- id: RN-002
  statement: A service cannot transition to published unless it references at least
    one published test or panel.
  applies_to: DiagnosticService
  enforcement_point: command:PublishDiagnosticService
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-aggregate validation against published component snapshots.
  test_refs:
  - TST-SVC-001-02
  - TST-SVC-001-03
- id: RN-003
  statement: A published service becomes an immutable version; changes create a new
    draft version.
  applies_to: DiagnosticService
  enforcement_point: command:UpdateDiagnosticService
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Versioning strategy requires snapshot copy and version increment
    logic.
  test_refs:
  - TST-SVC-001-04
- id: RN-004
  statement: A deprecated service cannot be added to new orders but remains valid
    for historical references.
  applies_to: DiagnosticService
  enforcement_point: query:PublishedServiceSnapshot
  severity: high
  audit_required: false
  generatable: false
  custom_reason: Requires order-time eligibility policy.
  test_refs:
  - TST-SVC-001-05
- id: RN-005
  statement: Service category assignment must reference an existing active category
    in the same tenant.
  applies_to: DiagnosticService
  enforcement_point: command:CreateDiagnosticService, command:UpdateDiagnosticService
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-001-06
- id: RN-006
  statement: Only users with catalog authoring permission may create, update or publish
    services.
  applies_to: DiagnosticService
  enforcement_point: authorization:catalog.service.write
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-SVC-001-07
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-005
  - RN-006
  custom_implementation_rules:
  - RN-002
  - RN-003
  - RN-004
```
