# Domain Model

## Bounded Context

Imaging Operations is a specialized diagnostic context that integrates with Orders, Patients, Physicians, Billing, Notifications, IAM and Storage/Integration capabilities.

## Aggregates

### Imaging Study Aggregate

Represents the operational lifecycle of an imaging study linked to a diagnostic order. It controls scheduling state, acquisition state, report state, branch, modality and patient linkage.

### Imaging Appointment Aggregate

Controls scheduling, room, modality, duration, preparation and check-in workflow.

### Modality Aggregate

Represents an imaging device or modality category available at a branch, including operational status and scheduling constraints.

### Imaging Room Aggregate

Represents a physical room or acquisition resource used for imaging studies.

### DICOM Study Aggregate

Represents a received DICOM study with study/series/instance metadata, reconciliation state and PACS storage references.

### Imaging Report Aggregate

Controls interpretation, draft, review, signature, release and amendment lifecycle.

### Viewer Access Aggregate

Controls secure, auditable access to image viewing sessions and expirable links.

## Value Objects

- AccessionNumber.
- StudyInstanceUID.
- SeriesInstanceUID.
- SOPInstanceUID.
- ModalityCode.
- ImagingPreparationInstruction.
- ViewerAccessToken.
- RadiologyFinding.
- ReportSignature.
- DicomMetadata.
- AcquisitionProtocol.

## Invariants

- A released imaging report must have a valid signature.
- A DICOM study must be linked only to an order within the same tenant.
- An imaging appointment cannot use an inactive modality.
- A report amendment cannot overwrite the released report version.
- Viewer access must always be auditable.
