# 10 Commands & Queries

## Commands

- CreateDoctorProfile.
- UpdateDoctorProfile.
- ActivateDoctor.
- SuspendDoctor.
- DeactivateDoctor.
- AssignDoctorToBranch.
- RemoveDoctorFromBranch.
- InviteDoctorToPortal.
- AcceptDoctorPortalInvitation.
- AssignDoctorToOrder.
- ValidateResultAsDoctor.

## Queries

- GetDoctorById.
- SearchDoctors.
- ListDoctorsByBranch.
- ListDoctorsBySpecialty.
- ListOrdersForDoctor.
- ListResultsForDoctor.
- GetDoctorAuditTrail.

## Reglas de consulta

Todas las consultas deben filtrar por tenant y aplicar restricciones de sucursal, relación con orden y permisos.
