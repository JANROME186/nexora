---
id: CAP-001
name: Patient Management
status: draft
version: 0.13.0
owner: Business Architecture
---

# CAP-001 Patient Management

## Objective

Manage patients consistently across reception, patient portal, doctor portal, mobile applications, public APIs and AI-assisted workflows.

## Scope

Included:

- Patient registration.
- Patient update.
- Patient search.
- Patient duplicate detection.
- Patient contact information.
- Patient identity references.
- Guardians for minors.
- Consent references.
- Patient lifecycle events.

Excluded from MVP 1:

- Complete clinical history.
- Advanced patient segmentation.
- Loyalty programs.
- AI-based patient risk scoring.

## Related artifacts

- Domain: `DOM-001 Patient Domain`.
- Story: `US-001 Register Patient`.
- API: `API-001 Patients API`.
- Entity: `ENT-001 Patient`.
- Event: `EVT-001 PatientCreated`.
- Rule: `BR-001 Minor Patient Requires Guardian`.

## MVP behavior

Patient Management must be simple enough for reception workflows and robust enough for future integrations with orders, results, billing, portals and mobile apps.
