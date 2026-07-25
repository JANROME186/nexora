---
id: HOP-BM-BCM-RES-004
format: markdown_structured_payload
type: business-model
name: Digital Delivery Business Model
version: 0.1.0
status: modeled
---

# Digital Delivery Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-RES-004
  type: business-model
  name: Digital Delivery Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  bounded_context: laboratory-results
  primary_aggregate: ResultDeliveryTicket
  model_kind: aggregate_owner
entities:
- id: ENT-DLV-001
  name: ResultDeliveryTicket
  is_aggregate_root: true
  description: 'Tracks one authorized channel''s delivery of a released result. This
    capability owns this entity exclusively; LaboratoryResult remains owned by BCM-LAB-006
    and is only read here through BCM-RES-001''s projection or the ResultReleased/ResultAmended
    events.

    '
  fields:
  - name: deliveryTicketId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: resultId
    type: ResultId
    required: true
  - name: reportId
    type: uuid
    required: false
    description: References the GeneratedResultReport (BCM-RES-002) attached to this
      delivery, when a PDF is available.
  - name: recipientType
    type: enum
    values:
    - patient
    - patient_representative
    - referring_doctor
    required: true
  - name: recipientId
    type: uuid
    required: true
    description: PatientId, representative id or DoctorId depending on recipientType.
  - name: deliveryChannel
    type: enum
    values:
    - patient_portal
    - doctor_portal
    - mobile_app
    required: true
  - name: authorizationCheck
    type: DeliveryAuthorizationCheck
    required: true
  - name: status
    type: enum
    values:
    - pending_authorization
    - authorized
    - delivered
    - viewed
    - withheld
    required: true
  - name: deliveredAt
    type: datetime
    required: false
  - name: viewedAt
    type: datetime
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-DLV-001
  name: DeliveryAuthorizationCheck
  description: Structured authorization decision recorded before a delivery ticket
    is allowed to become visible externally.
  fields:
  - name: resultReleased
    type: boolean
    required: true
  - name: recipientOwnershipOrReferralVerified
    type: boolean
    required: true
    description: True when patient identity matches, representative authorization
      is active, or the doctor is the order's referring/treating physician.
  - name: representativeAuthorizationValid
    type: boolean
    required: false
    description: Populated only when recipientType is patient_representative.
  - name: checkedAt
    type: datetime
    required: true
invariants:
- id: INV-DLV-001
  statement: A ResultDeliveryTicket can become authorized only when the referenced
    LaboratoryResult is released; a ticket for a non-released result remains pending_authorization
    indefinitely and is never surfaced externally.
- id: INV-DLV-002
  statement: A patient recipient may only be authorized for their own results; a patient_representative
    recipient requires a verified, active representative authorization; a referring_doctor
    recipient may only be authorized for results linked to their own referral or treatment
    relationship on the source order.
- id: INV-DLV-003
  statement: This capability never mutates LaboratoryResult, Patient or Doctor state;
    authorization checks are read-only cross-capability queries.
- id: INV-DLV-004
  statement: An amended result's existing delivery tickets are marked withheld until
    a new authorization check confirms the amendment is safe to redeliver; amendment
    never silently republishes the prior delivered view.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-005 PatientId
  - VO-ID-006 DoctorId
  - VO-ID-009 ResultId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-LAB-010 Result Release (read-only event source: ResultReleased, ResultAmended)
  - BCM-RES-001 Result Management (read-only projection source)
  - BCM-RES-002 PDF Report Generation (read-only GeneratedResultReport reference)
  - BCM-PER-002 Patient aggregate (read-only identity/representative source)
  - BCM-PER-003 Doctor aggregate (read-only referral/treatment relationship source)
- brm_alignment:
  - BRM-001-R012 (release required before external visibility)
  - BRM-001-R014 (external portals show released results only)
  - BRM-001-R015 (patient representative authorization required)
```
