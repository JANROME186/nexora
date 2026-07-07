# 02 Business Rules

| ID | Regla | Prioridad |
|---|---|---|
| BR-MED-001 | Todo médico debe pertenecer a un laboratorio/tenant. | Alta |
| BR-MED-002 | Un médico puede estar asociado a una o varias sucursales según permisos. | Alta |
| BR-MED-003 | El número de cédula profesional debe ser único por país dentro del tenant cuando se capture. | Alta |
| BR-MED-004 | Un médico externo no puede ver pacientes u órdenes no asociados a él. | Crítica |
| BR-MED-005 | Un médico interno solo puede validar resultados si tiene permiso clínico explícito. | Crítica |
| BR-MED-006 | La consulta de resultados por un médico debe generar auditoría. | Crítica |
| BR-MED-007 | Un médico suspendido no puede acceder al portal médico ni recibir nuevas órdenes referidas. | Alta |
| BR-MED-008 | Una orden puede tener médico solicitante, médico referidor, médico intérprete y médico validador. | Alta |
| BR-MED-009 | Los datos fiscales del médico son opcionales en MVP, salvo que el país/country pack los requiera. | Media |
| BR-MED-010 | El médico debe aceptar términos y aviso de privacidad antes de usar el portal médico. | Alta |
| BR-MED-011 | La especialidad debe tomarse de catálogo controlado. | Media |
| BR-MED-012 | Los médicos pueden recibir notificaciones únicamente por canales autorizados. | Alta |
| BR-MED-013 | La baja de un médico no elimina su participación histórica en órdenes/resultados. | Crítica |
| BR-MED-014 | La asignación de médico a sucursal debe respetar el alcance organizacional del usuario que la realiza. | Alta |
| BR-MED-015 | Los cambios en credenciales profesionales deben quedar versionados. | Alta |
