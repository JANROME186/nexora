---
id: TD-FE-002
format: markdown_structured_payload
type: technical-debt-item
name: Employee portal is missing patient/doctor update, patient document management
  and doctor specialty assignment UI
version: 2.0.0
status: closed
---

# Employee Portal Is Missing Patient/Doctor Update, Patient Document Management And Doctor Specialty Assignment Ui

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-002
  type: technical-debt-item
  name: Employee portal is missing patient/doctor update, patient document management
    and doctor specialty assignment UI
  version: 2.0.0
  status: closed
  created_date: 2026-07-14
  updated_date: 2026-07-27
  closed_by_backlog_item: HOP-HARD-FE-001
source:
  discovered_during_backlog_item: MVP-MOD-003-QA-001
  module: MVP-MOD-003 People and Clinical Master Data
  evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
classification:
  category: ui_completeness
  affected_area: employee_portal_people_and_clinical_master_data_screens
  affected_components:
  - 07-implementation/employee-portal/src/api/peopleApi.ts
  - 07-implementation/employee-portal/src/components/screens/PatientsScreen.tsx
  - 07-implementation/employee-portal/src/components/screens/DoctorsScreen.tsx
  risk_level: low
  blocking: false
  reason_non_blocking: 'All operations were already explicitly scoped out of MVP-MOD-003-FE-001''s
    minimum required scope (see MVP-MOD-003-FE-001-validation.md out_of_scope_confirmed,
    which named the patient/doctor editor screens, patient documents panel and doctor
    specialty panel as deliberately not built). The backend already supports every
    one of these operations as generatable, tested endpoints (verified by PeopleClinicalMasterDataContractTest
    and the BE-001 contract check), so this is a pure client/UI gap with no backend
    risk. Registering it here makes the gap trackable for a future backlog item instead
    of leaving it as an undocumented omission.

    '
current_state:
  issue: 'Comparing the four capability packages'' openapi-source.md operations
    against 07-implementation/employee-portal/src/api/peopleApi.ts, 11 of 42 modeled
    operations have no exported client function and therefore no UI: updatePatient,
    deactivatePatient, updatePatientRepresentative, listPatientDocuments, attachPatientDocument,
    removePatientDocument (all BCM-PER-002), and updateDoctor, retireDoctor, listSpecialtyAssignments,
    assignSpecialty, unassignSpecialty (all BCM-PER-003). Additionally, 3 functions
    that do exist in peopleApi.ts (getPatient, getDoctor, getPatientRegistration)
    are exported but never called from any screen (PatientsScreen/DoctorsScreen use
    the *Snapshot variants instead, and PatientRegistrationsScreen relies on the in-memory
    list rather than a per-row detail fetch), which is minor dead code rather than
    a functional gap.

    '
  compensating_control:
  - Every operation in this list is reachable and already tested through the backend
    REST API directly (e.g. via the smoke validation SMOKE-004 in the local runbook),
    so administrators with API access are not blocked; only the employee-portal UI
    convenience is missing.
target_state:
  preferred_open_source_tooling:
  - No new tooling required; extend peopleApi.ts with the 11 missing client functions
    and add edit/documents/specialty panels to PatientsScreen.tsx and DoctorsScreen.tsx
    following the same useAsyncAction/StatusBanner/ConfirmDialog patterns already
    used in those files.
  expected_integration_points:
  - 07-implementation/employee-portal/src/api/peopleApi.ts
  - 07-implementation/employee-portal/src/components/screens/PatientsScreen.tsx (SCR-PAT-002-02
    editor, SCR-PAT-002-06 documents panel, representative update)
  - 07-implementation/employee-portal/src/components/screens/DoctorsScreen.tsx (SCR-DOC-003-02
    editor, SCR-DOC-003-04 specialty panel)
remediation:
  strategy: gradual_when_a_future_ui_backlog_item_covers_patient_or_doctor_editing
  recommended_trigger:
  - A future MVP-MOD-003 follow-up UI backlog item, or a subsequent module that requires
    editing patient/doctor demographic data, documents or doctor specialties from
    the portal
  acceptance_criteria:
  - peopleApi.ts exports a function for every operation declared in the 4 capability
    packages' openapi-source.md files.
  - PatientsScreen.tsx and DoctorsScreen.tsx expose update, and PatientsScreen.tsx
    exposes document management, and DoctorsScreen.tsx exposes specialty assignment,
    each with loading/error/empty/confirmation states consistent with the rest of
    the module.
  - getPatient/getDoctor/getPatientRegistration are either wired into a screen or
    removed if confirmed unnecessary.
closure:
  backlog_item: HOP-HARD-FE-001
  evidence: 08-qa/qa/final-hardening/HOP-HARD-FE-001-validation.md
  summary: 'peopleApi.ts now exports all 11 previously-missing client functions (updatePatient,
    deactivatePatient, updatePatientRepresentative, listPatientDocuments, attachPatientDocument,
    removePatientDocument, updateDoctor, retireDoctor, listSpecialtyAssignments, assignSpecialty,
    unassignSpecialty), each calling the already-tested backend endpoint confirmed present in
    PatientController.java/DoctorController.java. PatientsScreen.tsx gained an edit-patient panel
    (update), a deactivate action, a documents panel (list/attach/remove) and representative
    edit-in-place (update); DoctorsScreen.tsx gained an edit-doctor panel (update), a retire
    action and a specialty-assignment panel (list/assign/unassign) -- all following the existing
    useAsyncAction/StatusBanner/ConfirmDialog pattern with loading/error/empty/confirmation/success
    states. The three dead exports named in this item''s current_state (getPatient, getDoctor,
    getPatientRegistration) were confirmed still unreferenced by any screen and removed from
    peopleApi.ts (and their tests), resolving the acceptance criterion''s "or removed if confirmed
    unnecessary" branch. All three acceptance criteria are met; closed.'
  new_tests:
  - 'PatientsScreen.test.tsx -- "updates and deactivates a selected patient after explicit
    confirmation", "attaches and removes a patient document after explicit confirmation"'
  - 'DoctorsScreen.test.tsx -- "updates and retires a selected doctor after explicit
    confirmation", "assigns and unassigns a doctor specialty after explicit confirmation"'
  - peopleApi.test.ts extended with request-shape assertions for all 11 new functions
```
