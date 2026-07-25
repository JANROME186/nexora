# CAP-001 Patient Management

Patient Management is the business capability responsible for creating, maintaining and protecting the patient profile across Nexora.

It supports reception, orders, billing, results, portals, mobile applications, medical users, AI assistants and analytics.

## Goals

- Register patients safely and quickly.
- Avoid duplicate patient records.
- Support minors and guardians.
- Keep contact and demographic data updated.
- Maintain auditability and privacy.

## Channels

- Employee web access.
- Patient portal.
- Patient mobile app.
- Doctor portal.
- Public API.
- AI-assisted reception.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-001
type: businessCapability
name: Patient Management
status: draft
version: 0.15.0
owner: Product
description: Manages patient identity, profile, contact data, documents, consent and
  patient-related access across Nexora.
tags:
- mvp1
- patient
- master-data
capabilities:
- patient registration
- patient profile update
- duplicate prevention
- guardian management
- patient documents
- patient consent
- patient portal access
channels:
- employee-web
- patient-portal
- patient-mobile
- doctor-portal
- public-api
- ai-assistant
relations:
- type: governedBy
  target: BR-001
- type: implementedBy
  target: API-001
- type: modeledBy
  target: DOM-001
- type: usesEntity
  target: ENT-001
- type: verifiedBy
  target: QA-001
```
