# 16 Test Specification

## Pruebas funcionales

- Alta de médico interno/externo.
- Duplicidad de cédula/correo.
- Asignación a sucursal.
- Suspensión y bloqueo de portal.
- Consulta de resultados con relación autorizada.
- Denegación sin relación autorizada.
- Auditoría de consulta y descarga.

## Pruebas de seguridad

- Cross-tenant access denied.
- Branch-scope enforcement.
- Least privilege para validación de resultados.
- Portal access revoked.

## Contract tests

Deben generarse desde `05-contracts/contracts/openapi/doctors/doctors.openapi.md`.
