# ADR-002: Agent Agnostic Engineering

## Estado

Approved

## Contexto

El proyecto puede ser desarrollado por agentes de IA, automatizaciones determinísticas o ingenieros humanos. Depender de un agente específico limitaría la evolución.

## Decisión

Los prompts, playbooks y manifiestos serán neutrales. Cada agente podrá tener adaptadores, pero la fuente de verdad será independiente del proveedor.

## Consecuencias

- Mayor portabilidad.
- Menor lock-in.
- Requiere estructura estricta de contexto y archivos.
