# Event Storming

## Domain Events

- ImagingStudyOrdered
- ImagingAppointmentScheduled
- ImagingAppointmentRescheduled
- ImagingAppointmentCancelled
- ImagingPatientCheckedIn
- ImagingAcquisitionStarted
- ImagingAcquisitionCompleted
- DicomStudyReceived
- DicomStudyMatched
- DicomStudyConflictDetected
- DicomStudyReconciled
- DicomStudyLinkedToOrder
- ImagingReportDrafted
- ImagingReportReviewed
- ImagingReportSigned
- ImagingReportReleased
- ImagingReportAmendmentRequested
- ImagingReportAmended
- ImagingViewerLinkCreated
- ImagingViewerAccessed
- ImagingCriticalFindingDetected
- ImagingCriticalFindingNotified
- ModalityActivated
- ModalitySentToMaintenance
- ModalityReturnedToService

## Commands

- ScheduleImagingAppointment
- CheckInImagingPatient
- StartImagingAcquisition
- CompleteImagingAcquisition
- IngestDicomStudy
- ReconcileDicomStudy
- DraftImagingReport
- SignImagingReport
- ReleaseImagingReport
- RequestImagingReportAmendment
- CreateViewerAccessLink
- RegisterModality
- UpdateModalityStatus

## External Systems

- PACS storage provider.
- DICOM modality.
- RIS/HL7 integration source.
- Notification provider.
- Patient portal.
- Physician portal.
- AI imaging assistant provider.
