# 07 Domain Model

## Bounded Context

Medical Staff Management.

## Agregados

### DoctorProfile

Raíz de agregado que representa a un profesional médico asociado a un tenant. Controla identidad profesional, estado, especialidades, alcance por sucursal y relación con portal médico.

### DoctorPortalAccess

Agregado encargado de la invitación, aceptación, revocación y auditoría de acceso al portal médico.

### DoctorOrderParticipation

Representa la participación clínica/comercial de un médico en una orden o resultado.

## Value Objects

- ProfessionalLicense.
- SpecialtyCode.
- MedicalStaffType.
- ContactPreference.
- PortalInvitationToken.
- ClinicalResponsibility.

## Servicios de dominio

- DoctorVisibilityPolicy.
- DoctorResultAccessPolicy.
- DoctorValidationEligibilityPolicy.
- DoctorUniquenessPolicy.
