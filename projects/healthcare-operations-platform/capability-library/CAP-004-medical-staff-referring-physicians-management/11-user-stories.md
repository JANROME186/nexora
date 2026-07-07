# 11 User Stories

## Administración médica

| ID | Historia | Criterios de aceptación resumidos |
|---|---|---|
| US-MED-001 | Como administrador quiero registrar médicos internos y externos para asociarlos a órdenes y resultados. | Valida tenant, duplicidad, estado inicial y auditoría. |
| US-MED-002 | Como administrador quiero asignar médicos a sucursales para controlar su alcance operativo. | Solo sucursales del tenant; audita cambios. |
| US-MED-003 | Como administrador quiero suspender médicos para bloquear accesos y nuevas asignaciones. | Bloquea portal; conserva historial. |
| US-MED-004 | Como recepcionista quiero buscar médicos por nombre, cédula o especialidad para asignarlos a una orden. | Respeta sucursal y estado activo. |
| US-MED-005 | Como médico externo quiero acceder al portal médico para consultar resultados de mis pacientes. | Requiere usuario activo, términos aceptados y relación autorizada. |
| US-MED-006 | Como médico quiero descargar un resultado en PDF para integrarlo a mi expediente clínico. | Registra auditoría de descarga. |
| US-MED-007 | Como químico/radiólogo quiero validar resultados si tengo permiso para ello. | Valida rol, permiso, resultado completo y auditoría. |
| US-MED-008 | Como auditor quiero consultar accesos de médicos a resultados para investigar incidentes. | Filtra por fecha, médico, paciente, orden y resultado. |
| US-MED-009 | Como administrador quiero capturar especialidades para clasificar médicos y facilitar búsqueda. | Usa catálogo controlado. |
| US-MED-010 | Como sistema quiero notificar al médico cuando un resultado esté disponible si su preferencia lo permite. | Respeta consentimiento, canal y feature flag. |

## Portal médico

- Ver panel de pacientes/órdenes relacionados.
- Consultar resultados liberados.
- Descargar PDF.
- Ver historial limitado por relación clínica.
- Recibir notificaciones autorizadas.
