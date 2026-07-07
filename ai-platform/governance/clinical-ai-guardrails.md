# Clinical AI Guardrails

## Reglas no negociables

1. La IA no sustituye al médico, químico, radiólogo ni responsable sanitario.
2. La IA no libera resultados clínicos por sí sola.
3. La IA no debe modificar resultados validados sin flujo de corrección autorizado.
4. Toda explicación para pacientes debe ser educativa y recomendar consultar a su médico.
5. Las alertas críticas generadas o priorizadas por IA deben quedar auditadas.
6. Las respuestas clínicas deben evitar lenguaje determinista cuando no exista validación humana.

## Clasificación de riesgo

| Nivel | Ejemplo | Control |
|---|---|---|
| Bajo | Ayuda de navegación | Sin revisión obligatoria |
| Medio | Resumen administrativo | Revisión configurable |
| Alto | Explicación de resultados | Revisión o disclaimer obligatorio |
| Crítico | Validación clínica | Humano obligatorio |
