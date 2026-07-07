# CAP-001 Patient Management - User Stories

## Epic CAP-001-EPIC-001 Patient Registration

### US-001 Register a new patient

As a receptionist, I want to register a patient with required identity, contact and clinical profile data so that the patient can receive diagnostic services.

**Acceptance criteria**

- Given required patient fields are complete, when I submit the form, then the system creates an active patient record.
- Given possible duplicates exist, when I submit the form, then the system shows duplicate candidates before final creation.
- Given the patient is a minor, when I submit the form, then the system requires guardian data according to policy.
- Given digital delivery is selected, when consent is missing, then the system prevents digital delivery activation.

### US-002 Search existing patients

As a receptionist, I want to search patients by name, phone, identifier or birth date so that I can reuse existing records and avoid duplicates.

**Acceptance criteria**

- Search results are tenant-isolated.
- Sensitive fields are masked when the user lacks permission.
- Results support pagination.
- Search records are auditable when configured by privacy policy.

### US-003 Update patient profile

As an authorized employee, I want to update patient demographic and contact information so that the patient record remains accurate.

**Acceptance criteria**

- Only users with `patients:update` can save changes.
- All changes are audited.
- Restricted fields require elevated permissions.
- Updates emit `PatientUpdated`.

### US-004 Record patient consent

As a receptionist, I want to record patient consent for digital delivery and data processing so that the laboratory can comply with privacy requirements.

**Acceptance criteria**

- Consent version and scope are recorded.
- Consent actor and timestamp are recorded.
- Consent cannot be silently overwritten.
- Consent changes emit `PatientConsentRecorded`.

### US-005 Deactivate patient

As a supervisor, I want to deactivate a patient record so that it cannot be used for new operational flows while preserving history.

**Acceptance criteria**

- Deactivation requires `patients:deactivate`.
- Patient history remains visible to authorized users.
- New orders cannot be created for inactive patients unless reactivated.
- Deactivation emits `PatientDeactivated`.

## MVP 1 story backlog summary

| ID | Story | Priority |
|---|---|---|
| US-001 | Register patient | Must |
| US-002 | Search patients | Must |
| US-003 | View patient summary | Must |
| US-004 | Update patient profile | Must |
| US-005 | Record consent | Must |
| US-006 | Add guardian | Must |
| US-007 | Detect duplicates | Should |
| US-008 | Deactivate/reactivate patient | Should |
| US-009 | View patient timeline | Could |
| US-010 | Patient portal profile | Could |
