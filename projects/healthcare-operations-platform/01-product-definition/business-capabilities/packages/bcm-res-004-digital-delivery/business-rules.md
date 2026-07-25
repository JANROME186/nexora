---
id: HOP-BR-BCM-RES-004
format: markdown_structured_payload
type: business-rules
name: Digital Delivery Business Rules
version: 0.1.0
status: modeled
---

# Digital Delivery Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-RES-004
  type: business-rules
  name: Digital Delivery Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A result can be delivered through any external channel only when its
    LaboratoryResult status is released; delivery of a non-released result is refused.
  applies_to: ResultDeliveryTicket
  enforcement_point: command:AuthorizeResultDelivery
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability status check against LaboratoryResult
    via BCM-RES-001 or the ResultReleased event.
  brm_alignment: BRM-001-R012
  test_refs:
  - TST-DLV-004-01
- id: RN-002
  statement: A patient recipient may be authorized only for their own results.
  applies_to: ResultDeliveryTicket
  enforcement_point: command:AuthorizeResultDelivery
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only identity match against BCM-PER-002's patient
    identity, not a static role check.
  brm_alignment: BRM-001-R014
  test_refs:
  - TST-DLV-004-02
- id: RN-003
  statement: A patient_representative recipient requires a verified, currently active
    representative authorization; an expired, revoked or unverified representative
    relationship blocks delivery.
  applies_to: ResultDeliveryTicket
  enforcement_point: command:AuthorizeResultDelivery
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability check of the representative
    relationship's current state, sourced from BCM-PER-002.
  brm_alignment: BRM-001-R015
  test_refs:
  - TST-DLV-004-03
- id: RN-004
  statement: A referring_doctor recipient may be authorized only for a result whose
    source order references that doctor as the referring or treating physician.
  applies_to: ResultDeliveryTicket
  enforcement_point: command:AuthorizeResultDelivery
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability check against the order's doctor
    reference, sourced from BCM-LAB-001/BCM-PER-003.
  brm_alignment: BRM-001-R014
  test_refs:
  - TST-DLV-004-04
- id: RN-005
  statement: An amendment to a delivered result marks its existing delivery tickets
    withheld until a new DeliveryAuthorizationCheck confirms redelivery is authorized;
    the prior delivered view is never silently kept visible or silently replaced.
  applies_to: ResultDeliveryTicket
  enforcement_point: event:ResultAmended
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability event handling and a state-machine transition,
    not a generic field update.
  test_refs:
  - TST-DLV-004-05
- id: RN-006
  statement: This capability never mutates LaboratoryResult, Patient or Doctor state;
    all authorization checks are read-only.
  applies_to: ResultDeliveryTicket
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement consistent with AGG-009's ownership
    rules and patient/doctor master-data ownership.
  test_refs:
  - TST-DLV-004-06
- id: RN-007
  statement: Opening a delivered result by an authorized recipient must record ResultViewed
    with actor/recipient identity and timestamp.
  applies_to: ResultDeliveryTicket
  enforcement_point: query:GetDeliveredResult
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires recording view state transitions distinct from a generic
    read audit entry.
  test_refs:
  - TST-DLV-004-07
- id: RN-008
  statement: Delivery commands and queries must execute within the recipient's own
    authorization scope; no cross-tenant or cross-patient access is permitted regardless
    of actor role.
  applies_to: ResultDeliveryTicket
  enforcement_point: authorization:delivery.authorize, authorization:delivery.view
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-DLV-004-08
enforcement_summary:
  generatable_rules:
  - RN-008
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
  - RN-006
  - RN-007
```
