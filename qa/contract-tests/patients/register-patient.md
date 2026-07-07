---
id: TEST-001
name: Register Patient Contract Test
status: draft
version: 0.13.0
---

# TEST-001 Register Patient Contract Test

## Purpose

Validate that `POST /patients` follows `API-001 Patients API` and enforces required MVP business rules.

## Scenarios

- Register valid adult patient.
- Reject missing required fields.
- Reject minor patient without guardian when country pack requires it.
- Emit `PatientCreated` after successful registration.
