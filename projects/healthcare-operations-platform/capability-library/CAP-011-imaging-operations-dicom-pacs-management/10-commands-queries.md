# Commands & Queries

## Commands

| Command | Description |
|---|---|
| ScheduleImagingAppointment | Schedule imaging study in a branch room/modality. |
| RescheduleImagingAppointment | Move appointment to another slot. |
| CancelImagingAppointment | Cancel appointment with reason. |
| CheckInImagingPatient | Mark patient as ready for imaging acquisition. |
| CompleteImagingAcquisition | Mark acquisition as completed. |
| IngestDicomStudy | Register DICOM study metadata and storage references. |
| ReconcileDicomStudy | Link unmatched/conflicted DICOM study to order. |
| DraftImagingReport | Create radiology report draft. |
| SignImagingReport | Sign report by authorized radiologist. |
| ReleaseImagingReport | Publish report and optionally viewer access. |
| AmendImagingReport | Create amended report version. |
| CreateViewerAccessLink | Generate secure expirable viewer link. |
| RegisterModality | Register imaging modality/device. |
| UpdateModalityStatus | Change modality operational state. |

## Queries

| Query | Description |
|---|---|
| GetImagingStudyById | Retrieve imaging study details. |
| SearchImagingStudies | Search studies by patient, order, status, branch or date. |
| GetTechnicianWorklist | Retrieve technician acquisition tasks. |
| GetRadiologistWorklist | Retrieve studies pending interpretation. |
| GetDicomStudyMetadata | Retrieve DICOM metadata by UID. |
| GetUnmatchedDicomStudies | Retrieve reconciliation worklist. |
| GetImagingReport | Retrieve current report version. |
| GetImagingReportHistory | Retrieve report versions and amendments. |
| GetViewerAccessAudit | Retrieve audit log of image access. |
| GetModalityAvailability | Retrieve availability by branch/modality/date. |
