# 03 Decision Tables

## DT-MED-001 Acceso al portal médico

| Estado médico | Usuario activo | Términos aceptados | Resultado |
|---|---|---|---|
| Active | Sí | Sí | Permitir acceso |
| Active | Sí | No | Solicitar aceptación |
| Suspended | Cualquiera | Cualquiera | Bloquear acceso |
| Inactive | Cualquiera | Cualquiera | Bloquear acceso |

## DT-MED-002 Visibilidad de resultados

| Tipo médico | Relación con orden | Permiso | Resultado |
|---|---|---|---|
| Externo | Solicitante/referidor | Ver resultados | Permitir |
| Externo | Sin relación | Ver resultados | Denegar |
| Interno | Sucursal asignada | Ver resultados | Permitir |
| Interno | Fuera de sucursal | Sin permiso global | Denegar |

## DT-MED-003 Validación de resultado

| Rol clínico | Permiso validar | Resultado completo | Resultado |
|---|---|---|---|
| Químico/Radiólogo/Patólogo | Sí | Sí | Permitir validación |
| Químico/Radiólogo/Patólogo | No | Sí | Denegar |
| Otro | Sí | Sí | Requiere autorización especial |
| Cualquiera | Sí | No | Bloquear validación |
