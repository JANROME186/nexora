---
id: HOP-BM-BCM-RES-007
format: markdown_structured_payload
type: business-model
name: Result Notifications Business Model
version: 0.1.0
status: modeled
---

# Result Notifications Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-RES-007
  type: business-model
  name: Result Notifications Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
  bounded_context: notifications
  primary_aggregate: ResultNotificationRequest
  model_kind: aggregate_owner
entities:
- id: ENT-RNT-001
  name: ResultNotificationRequest
  is_aggregate_root: true
  description: 'The business decision record of "notify this recipient about this
    result, for this reason". Owns the notification''s business rationale; delegates
    all physical dispatch to BCM-PLT-003 via its NotificationRequest.

    '
  fields:
  - name: resultNotificationRequestId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: resultId
    type: ResultId
    required: true
  - name: recipientId
    type: uuid
    required: true
  - name: recipientType
    type: enum
    values:
    - patient
    - patient_representative
    - referring_doctor
    required: true
  - name: triggerReason
    type: enum
    values:
    - result_delivered
    - result_critical
    - result_amended
    required: true
  - name: composedTemplateReference
    type: string
    required: true
  - name: dispatchReference
    type: uuid
    required: false
    description: References the BCM-PLT-003 NotificationRequest created for this decision,
      once submitted.
  - name: dispatchStatus
    type: enum
    values:
    - pending_submission
    - submitted
    - dispatched
    - delivered
    - failed
    required: true
  - name: audit
    type: AuditMetadata
    required: true
invariants:
- id: INV-RNT-001
  statement: A ResultNotificationRequest with triggerReason result_delivered may only
    be created after ResultDeliveryAuthorized (BCM-RES-004); it must never precede
    authorization.
- id: INV-RNT-002
  statement: A ResultNotificationRequest with triggerReason result_critical must be
    created for every ResultFlaggedCritical event without exception, satisfying the
    traceable-notification requirement.
- id: INV-RNT-003
  statement: This capability composes template references and parameters only; it
    never dispatches directly and never addresses a channel provider.
- id: INV-RNT-004
  statement: This capability never mutates LaboratoryResult, Patient or Doctor state.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-009 ResultId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-LAB-010 Result Release (read-only event source: ResultReleased, ResultAmended)
  - BCM-RES-004 Digital Delivery (read-only event source: ResultDeliveryAuthorized)
  - BCM-RES-006 Critical Results (read-only event source: critical escalation context,
      when available)
  - BCM-PLT-003 Notification Management (delegated dispatch target)
- brm_alignment:
  - BRM-001-R013 (critical results require notification trace)
```
