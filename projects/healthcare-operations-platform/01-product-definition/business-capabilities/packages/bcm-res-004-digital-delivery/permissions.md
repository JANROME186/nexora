---
id: HOP-PERM-BCM-RES-004
format: markdown_structured_payload
type: permissions
name: Digital Delivery Permissions
version: 0.1.0
status: modeled
---

# Digital Delivery Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-RES-004
  type: permissions
  name: Digital Delivery Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  depends_on_capability: BCM-PLT-001
scopes:
- code: delivery.view
  description: View one's own (or represented, or referred) delivered results.
roles:
- role: patient
  grants:
  - delivery.view
- role: patient-representative
  grants:
  - delivery.view
- role: referring-doctor
  grants:
  - delivery.view
- role: tenant-administrator
  grants:
  - delivery.view
access_policies:
- id: POL-DLV-004-01
  statement: A patient may view only delivery tickets where recipientId matches their
    own PatientId.
  enforcement: self_scope_policy
- id: POL-DLV-004-02
  statement: A patient representative may view only delivery tickets for patients
    they hold a currently active, verified representative authorization for.
  enforcement: represented_patient_scope_policy
- id: POL-DLV-004-03
  statement: A referring doctor may view only delivery tickets for results whose source
    order references them as referring or treating physician.
  enforcement: referral_scope_policy
- id: POL-DLV-004-04
  statement: This capability never mutates LaboratoryResult, Patient or Doctor.
  enforcement: read_only_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ResultDeliveryAuthorized
    fields:
    - deliveryTicketId
    - resultId
    - recipientType
    - deliveryChannel
  - event: ResultViewed
    fields:
    - deliveryTicketId
    - resultId
    - recipientId
```
