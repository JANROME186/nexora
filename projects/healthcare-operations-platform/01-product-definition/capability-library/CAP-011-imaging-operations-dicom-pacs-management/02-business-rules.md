# Business Rules

| Rule ID | Rule | Priority |
|---|---|---|
| IMG-BR-001 | Every imaging study must belong to an organization, branch and diagnostic order. | Critical |
| IMG-BR-002 | Imaging appointments must validate branch operating hours, room availability and modality availability. | High |
| IMG-BR-003 | A modality cannot be scheduled if it is inactive, under maintenance or not available at the selected branch. | High |
| IMG-BR-004 | Imaging studies requiring preparation must expose patient instructions before appointment confirmation. | High |
| IMG-BR-005 | A DICOM study must not be linked to a patient without patient/order matching validation. | Critical |
| IMG-BR-006 | DICOM metadata must never replace validated patient demographic data without authorized reconciliation. | Critical |
| IMG-BR-007 | Radiology reports must be signed by an authorized radiologist before release. | Critical |
| IMG-BR-008 | Released imaging reports are immutable; corrections require amendment workflow. | Critical |
| IMG-BR-009 | Image access links must be secure, expirable and auditable. | Critical |
| IMG-BR-010 | Patients can view released reports only when portal publication rules allow it. | High |
| IMG-BR-011 | Referring physicians can access only studies linked to their patients/orders unless additional permission is granted. | Critical |
| IMG-BR-012 | PACS storage must preserve study UID, series UID, instance UID and object metadata. | Critical |
| IMG-BR-013 | DICOM studies with reconciliation conflicts must remain in review state. | High |
| IMG-BR-014 | Imaging rooms may have capacity, modality and duration constraints. | Medium |
| IMG-BR-015 | AI assistance for imaging must be optional, auditable and never release reports without human validation. | Critical |
| IMG-BR-016 | Imaging study access must be logged with user, patient, branch, timestamp and purpose where applicable. | Critical |
| IMG-BR-017 | Deleted or archived imaging objects must follow retention and compliance policy. | Critical |
| IMG-BR-018 | Emergency or stat imaging studies must be prioritized according to branch policy. | High |
| IMG-BR-019 | External DICOM ingestion must pass malware/file safety controls before indexing. | High |
| IMG-BR-020 | Viewer compatibility must prioritize lightweight access for standard devices while enabling advanced tools progressively. | High |
