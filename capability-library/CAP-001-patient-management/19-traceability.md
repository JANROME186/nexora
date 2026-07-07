# CAP-001 Patient Management - Traceability Matrix

| Artifact | Depends on | Produces / impacts |
|---|---|---|
| CAP-001 | Product Vision, Business Architecture | Patient processes, rules, APIs, entities |
| CAP-001-BR-020 | Country Pack, Patient Rule Set | US-001, US-006, API guardians, tests |
| DT-001 | CAP-001-BR-020 | Registration validation, UI guardian flow |
| Patient State Machine | Business rules | Events, API state actions, tests |
| ENT-001 Patient | Data Architecture | OpenAPI schemas, ORM model, UI forms |
| EVT-001 PatientRegistered | Event Storming | Notifications, Analytics, Audit |
| US-001 Register patient | Business rules, DDD | POST /v1/patients, UI form, tests |
| WEB-PAT-002 | US-001, Design System | Frontend implementation |
| MOB-PAT-005 | US-001, Mobile Standard | Staff mobile implementation |
| QA-PAT-001 | US-001, BR-010, BR-020 | Test automation |

## Impact analysis examples

Changing `CAP-001-BR-020` impacts:

- Decision table `DT-001`.
- User stories `US-001` and `US-006`.
- API endpoint `POST /v1/patients`.
- API endpoint `POST /v1/patients/{patientId}/guardians`.
- UI screens `WEB-PAT-002`, `WEB-PAT-006`.
- Mobile screens `MOB-PAT-005`.
- Tests for minor registration.
- Country pack legal age configuration.
