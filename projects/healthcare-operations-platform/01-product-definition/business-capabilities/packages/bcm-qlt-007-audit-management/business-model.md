---
id: HOP-BM-BCM-QLT-007
format: markdown_structured_payload
type: business-model
name: Audit Management Business Model
version: 0.1.0
status: modeled
---

# Audit Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-QLT-007
  type: business-model
  name: Audit Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
  bounded_context: external-quality-compliance
  primary_aggregate: AuditSchedule
  model_kind: aggregate_owner
entities:
- id: ENT-AUD-001
  name: AuditSchedule
  is_aggregate_root: true
  description: 'Aggregate root managing an audit cycle, audit scope, assigned lead
    auditor, checklist findings, and published audit report.

    '
  fields:
  - name: auditId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: auditCode
    type: string
    required: true
    description: Human-readable reference (e.g., AUD-2026-ISO-01).
  - name: title
    type: string
    required: true
  - name: category
    type: enum
    values:
    - internal_quality_audit
    - regulatory_inspection
    - vendor_supplier_audit
    - accreditation_assessment
    required: true
  - name: standardReference
    type: string
    required: false
    description: Target standard (e.g., ISO 15189, ISO 9001, COFEPRIS NOM-007-SSA3-2011,
      CLIA).
  - name: status
    type: enum
    values:
    - planned
    - in_progress
    - report_pending
    - closed
    - cancelled
    required: true
  - name: leadAuditorId
    type: UserId
    required: true
  - name: plannedStartDate
    type: date
    required: true
  - name: plannedEndDate
    type: date
    required: true
  - name: actualStartDate
    type: datetime
    required: false
  - name: actualEndDate
    type: datetime
    required: false
  - name: findings
    type: list[AuditFindingItem]
    required: false
  - name: summaryReportDocumentId
    type: uuid
    required: false
    description: Document ID of published report stored in BCM-PLT-008.
  - name: audit
    type: AuditMetadata
    required: true
entities_child:
- id: ENT-AUD-002
  name: AuditFindingItem
  is_aggregate_root: false
  description: Non-conformity or finding logged during an audit execution.
  fields:
  - name: findingId
    type: uuid
    required: true
  - name: findingCode
    type: string
    required: true
  - name: clauseReference
    type: string
    required: false
  - name: severity
    type: enum
    values:
    - critical
    - major
    - minor
    - opportunity_for_improvement
    required: true
  - name: observation
    type: string
    required: true
  - name: evidenceReference
    type: string
    required: false
  - name: capaInvestigationId
    type: uuid
    required: false
    description: Linked CAPA investigation ID (BCM-QLT-006) for critical/major findings.
invariants:
- id: INV-AUD-001
  statement: An audit cannot transition to 'closed' while any linked CAPA investigation
    for a 'critical' finding remains open.
- id: INV-AUD-002
  statement: All findings marked 'critical' or 'major' must have a linked CapaInvestigation
    reference created before audit report publishing.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-QLT-006 CAPA Management (downstream target for critical/major findings)
  - BCM-PLT-008 Document Management (report storage and retention tagging)
```
