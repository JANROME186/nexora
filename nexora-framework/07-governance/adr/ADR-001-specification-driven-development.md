# ADR-001: Specification Driven Development

## Estado

Approved

## Contexto

Nexora será una plataforma empresarial con múltiples dominios, APIs, aplicaciones, integraciones, modelos de datos, agentes de IA y despliegues. El desarrollo directo desde requerimientos sueltos provocaría inconsistencias.

## Decisión

Todo desarrollo debe partir de especificaciones versionadas y trazables. Antes de implementar una funcionalidad deben existir, como mínimo, proceso/capacidad, historia, reglas, contrato OpenAPI si aplica, modelo de dominio y criterios de aceptación.

## Consecuencias

- Mejora la trazabilidad.
- Reduce retrabajo.
- Permite trabajo consistente con agentes de IA.
- Exige disciplina documental.
