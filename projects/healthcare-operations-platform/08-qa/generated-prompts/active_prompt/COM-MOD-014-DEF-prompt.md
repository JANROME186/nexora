# TASK: COM-MOD-014-DEF - Capability package models
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Atender el backlog activo: Capability package models.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates documentales: Markdown/frontmatter parseable, trazabilidad, punteros, deuda técnica y estado git.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/COM-MOD-017-CLOSEOUT-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Contexto principal: `01-product-definition/business-capabilities/packages/`

## 3. Entregables
- Cambios definition y validaciones asociadas.
- QA Evidence: `08-qa/qa/imaging-operations/COM-MOD-014-DEF-validation.md`
- Security Evidence: `08-qa/security-quality/COM-MOD-014-DEF/security-quality-evidence.md`
- Transición: crear `08-qa/handoffs/COM-MOD-014-DEF-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `docs(hop): define imaging operations capability packages`.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/COM-MOD-014-DEF-closure-validation.md`.
- Si el validador genera `COM-MOD-014-DEF-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; reportar los hallazgos, corregirlos y repetir commit + validación estricta.
- `git status --short` limpio después del commit y de la validación final.

<!-- ollama_plan_hash: 3c0143638a527605275860994d59242b56115fb43f474a87796fdce5c5d0d415 -->
