# Domain Events

| Event ID | Event | Payload Highlights |
|---|---|---|
| IMG-EVT-001 | ImagingStudyOrdered | organizationId, branchId, orderId, patientId, imagingStudyId |
| IMG-EVT-002 | ImagingAppointmentScheduled | imagingStudyId, appointmentId, roomId, modalityId, scheduledAt |
| IMG-EVT-003 | ImagingAcquisitionCompleted | imagingStudyId, technicianId, completedAt |
| IMG-EVT-004 | DicomStudyReceived | dicomStudyId, studyInstanceUid, accessionNumber |
| IMG-EVT-005 | DicomStudyConflictDetected | dicomStudyId, conflictType, orderCandidates |
| IMG-EVT-006 | DicomStudyReconciled | dicomStudyId, orderId, reconciledBy |
| IMG-EVT-007 | ImagingReportSigned | reportId, radiologistId, signatureId |
| IMG-EVT-008 | ImagingReportReleased | reportId, imagingStudyId, releasedToPortal, releasedToPhysician |
| IMG-EVT-009 | ViewerAccessLinkCreated | viewerLinkId, imagingStudyId, audience, expiresAt |
| IMG-EVT-010 | ImagingCriticalFindingDetected | imagingStudyId, reportId, severity |
| IMG-EVT-011 | ImagingReportAmended | reportId, previousVersionId, newVersionId |
| IMG-EVT-012 | ModalitySentToMaintenance | modalityId, reason, unavailableFrom |
