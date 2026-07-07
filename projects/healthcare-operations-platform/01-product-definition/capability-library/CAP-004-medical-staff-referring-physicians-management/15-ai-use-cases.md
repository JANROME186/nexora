# 15 AI Use Cases

## Casos permitidos MVP

- Sugerir especialidad del médico a partir de texto capturado, con revisión humana.
- Resumir historial de resultados de pacientes asociados al médico, si hay permiso.
- Generar borradores de mensajes al médico sobre disponibilidad de resultados.
- Ayudar al administrador a detectar perfiles duplicados.

## Guardrails

- La IA no debe otorgar permisos.
- La IA no debe validar resultados clínicos sin profesional autorizado.
- La IA debe respetar tenant, sucursal, relación clínica y permisos.
- Toda salida sensible debe indicar que requiere revisión humana.
