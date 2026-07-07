# ADR-002: Agent Agnostic Engineering

## Estado

Approved

## Contexto

El proyecto puede usar GitHub Copilot, Claude Code, Cursor, Codex, Gemini CLI u otros agentes futuros. Depender de un agente específico limitaría la evolución.

## Decisión

Los prompts, playbooks y manifiestos serán neutrales. Cada agente podrá tener adaptadores, pero la fuente de verdad será independiente del proveedor.

## Consecuencias

- Mayor portabilidad.
- Menor lock-in.
- Requiere estructura estricta de contexto y archivos.
