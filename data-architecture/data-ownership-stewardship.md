# Data Ownership & Stewardship

## Propósito

Establecer responsables claros sobre la calidad, seguridad y uso de los datos.

## Roles

| Rol | Responsabilidad |
|---|---|
| Data Owner | Responsable funcional del dato |
| Data Steward | Responsable operativo de calidad y consistencia |
| Data Custodian | Responsable técnico de almacenamiento y seguridad |
| Privacy Officer | Responsable de privacidad y cumplimiento |
| Security Officer | Responsable de controles de seguridad |
| AI Data Reviewer | Responsable de autorizar uso de datos para IA |

## Matriz inicial

| Dato | Owner | Steward | Custodian |
|---|---|---|---|
| Paciente | Operaciones clínicas | Recepción/Supervisión | Data Platform |
| Resultado | Dirección técnica | Químico responsable | Data Platform |
| Factura | Finanzas | Caja/Contabilidad | Data Platform |
| Usuario | Seguridad | Administrador tenant | IAM Platform |
| Estudio DICOM | Imagenología | Radiólogo/Supervisor | Imaging Platform |

## Regla

Ningún dato sensible debe integrarse a IA, analítica o exportaciones masivas sin propietario funcional y política de uso definida.
