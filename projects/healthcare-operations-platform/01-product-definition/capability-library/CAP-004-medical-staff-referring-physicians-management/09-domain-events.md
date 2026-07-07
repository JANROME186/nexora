# 09 Domain Events

| ID | Evento | Payload mínimo |
|---|---|---|
| EVT-MED-001 | DoctorProfileCreated | doctorId, tenantId, type, createdBy |
| EVT-MED-002 | DoctorProfileUpdated | doctorId, changedFields, updatedBy |
| EVT-MED-003 | DoctorActivated | doctorId, activatedBy |
| EVT-MED-004 | DoctorSuspended | doctorId, reason, suspendedBy |
| EVT-MED-005 | DoctorDeactivated | doctorId, reason, deactivatedBy |
| EVT-MED-006 | DoctorBranchAssigned | doctorId, branchId, assignedBy |
| EVT-MED-007 | DoctorPortalInvitationSent | doctorId, invitationId |
| EVT-MED-008 | DoctorPortalAccessAccepted | doctorId, userId |
| EVT-MED-009 | DoctorViewedResult | doctorId, resultId, viewedAt |
| EVT-MED-010 | DoctorDownloadedResult | doctorId, resultId, downloadedAt |
| EVT-MED-011 | DoctorAssignedToOrder | doctorId, orderId, participationType |
| EVT-MED-012 | ResultValidatedByDoctor | doctorId, resultId, validationRole |
