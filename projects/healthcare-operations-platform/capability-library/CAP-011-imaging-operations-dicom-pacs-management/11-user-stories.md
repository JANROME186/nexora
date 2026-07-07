# User Stories

## Imaging Scheduling

| Story ID | User Story | Acceptance Criteria |
|---|---|---|
| IMG-US-001 | As a receptionist, I want to schedule an imaging appointment so that the patient has an assigned room, modality and time slot. | Validates room, modality, branch hours and study duration. |
| IMG-US-002 | As a receptionist, I want to see preparation instructions before confirming an appointment so that the patient receives correct guidance. | Instructions are displayed and can be sent by notification. |
| IMG-US-003 | As a supervisor, I want to block unavailable modalities so that they cannot be scheduled during maintenance. | Inactive/maintenance modalities are excluded from available slots. |
| IMG-US-004 | As a receptionist, I want to reschedule imaging appointments with reason so that changes are traceable. | Previous and new appointment details are audited. |

## Acquisition

| Story ID | User Story | Acceptance Criteria |
|---|---|---|
| IMG-US-010 | As a technician, I want a worklist of scheduled studies so that I can perform acquisitions in order. | Worklist filters by branch, room, modality and date. |
| IMG-US-011 | As a technician, I want to mark acquisition as completed so that interpretation can begin. | Study moves to pending interpretation. |
| IMG-US-012 | As a technician, I want to report acquisition issues so that supervisors can track operational problems. | Issue reason and notes are stored. |

## DICOM/PACS

| Story ID | User Story | Acceptance Criteria |
|---|---|---|
| IMG-US-020 | As a PACS administrator, I want incoming DICOM studies to be indexed by metadata so that they can be linked to orders. | Study UID, series UID and instance UID are stored. |
| IMG-US-021 | As a supervisor, I want to reconcile unmatched DICOM studies so that images are not lost. | Authorized user can link to the correct order/patient. |
| IMG-US-022 | As a security auditor, I want tenant mismatch to block DICOM linkage so that cross-tenant leakage is prevented. | Security event is recorded. |

## Interpretation & Reporting

| Story ID | User Story | Acceptance Criteria |
|---|---|---|
| IMG-US-030 | As a radiologist, I want to view my worklist so that I can prioritize pending studies. | Filters by modality, priority, branch and SLA. |
| IMG-US-031 | As a radiologist, I want to draft a report using templates so that reporting is standardized. | Templates are selected by imaging service/modality. |
| IMG-US-032 | As a radiologist, I want to sign a report so that it can be released. | Only authorized radiologists can sign. |
| IMG-US-033 | As a clinical supervisor, I want amendments to preserve prior versions so that changes are auditable. | Previous versions remain accessible to authorized users. |

## Viewer & Delivery

| Story ID | User Story | Acceptance Criteria |
|---|---|---|
| IMG-US-040 | As a patient, I want to access released imaging reports and images through a secure link so that I can share them with my doctor. | Link expires and all access is audited. |
| IMG-US-041 | As a physician, I want to access imaging studies for my referred patients so that I can review findings. | Access is restricted to linked orders/patients. |
| IMG-US-042 | As an administrator, I want to revoke viewer links so that access can be stopped when required. | Revoked links cannot be used. |
