# US-001 Register Patient

As a reception employee, I want to register a patient so that the patient can be associated with quotations, orders, payments and diagnostic results.

## Acceptance Criteria

1. The user can search existing patients before creating a new record.
2. The user can capture mandatory demographic information.
3. The system validates minor/guardian requirements.
4. The system emits `PatientCreated` after successful registration.
5. The patient becomes available for order creation.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: US-001
type: userStory
name: Register Patient
status: draft
version: 0.15.0
owner: Product
actor: Reception Employee
goal: Register a patient for diagnostic service workflows.
benefit: Patient can be used in quotations, orders, billing, results and portals.
acceptanceCriteria:
- Patient search is available before creation.
- Mandatory demographic data is validated.
- Minor patient requires guardian data.
- PatientCreated event is emitted.
- Patient is available for order creation.
relations:
- type: realizes
  target: CAP-001
- type: partOf
  target: BPR-001
- type: constrainedBy
  target: BR-001
- type: implementedBy
  target: API-001
- type: usesEntity
  target: ENT-001
- type: emits
  target: EVT-001
- type: representedBy
  target: UI-001
- type: representedBy
  target: MOB-001
- type: verifiedBy
  target: QA-001
```
