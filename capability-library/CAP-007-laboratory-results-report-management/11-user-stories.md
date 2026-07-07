# User Stories

## Epic RES-EPIC-001 Result Capture

### US-RES-001 Manual result entry
As a laboratory technician, I want to enter result values manually so that tests without instrument integration can be completed.

Acceptance criteria:
- Only authorized users can enter results.
- Required analytes must be completed before validation.
- Values are validated against configured type, unit and allowed format.

### US-RES-002 Analyzer result import
As a laboratory system, I want to ingest analyzer results so that manual transcription is reduced.

Acceptance criteria:
- Imported results are linked to the correct order/sample/test.
- Unmatched results are queued for reconciliation.
- Original payload is auditable.

## Epic RES-EPIC-002 Validation and Release

### US-RES-003 Technical validation
As a validator, I want to technically validate results so that only reviewed data advances in the workflow.

### US-RES-004 Clinical validation
As a clinical professional, I want to clinically validate results requiring approval so that patient reports are clinically safe.

### US-RES-005 Critical value alert
As a laboratory supervisor, I want critical values to trigger alerts so that urgent results receive proper attention.

## Epic RES-EPIC-003 Reports and Delivery

### US-RES-006 Generate report
As a laboratory user, I want to generate standardized reports so that patients and doctors receive consistent information.

### US-RES-007 Deliver result
As a patient or doctor, I want to access released results through allowed channels so that I can review them securely.

### US-RES-008 Amend result
As an authorized validator, I want to amend a released result with full versioning so that corrections are traceable.
