# TASK: COM-MOD-014-FE-001 - Compile imaging operations UI outputs
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Atender el backlog activo: Compile imaging operations UI outputs.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates frontend obligatorios: typecheck, tests/cobertura, build, SAST, dependencias, i18n y scans de seguridad.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/COM-MOD-014-FE-001-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Contexto principal: `01-product-definition/business-capabilities/packages/`

## 3. Entregables
- Cambios frontend y validaciones asociadas.
- QA Evidence: `08-qa/qa/imaging-operations/COM-MOD-014-FE-001-validation.md`
- Security Evidence: `08-qa/security-quality/COM-MOD-014-FE-001/security-quality-evidence.md`
- Transición: crear `08-qa/handoffs/COM-MOD-014-FE-001-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `feat(hop): compile imaging operations UI`.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/COM-MOD-014-FE-001-closure-validation.md`.
- Si el validador genera `COM-MOD-014-FE-001-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; reportar los hallazgos, corregirlos y repetir commit + validación estricta.
- `git status --short` limpio después del commit y de la validación final.

<!-- ollama_plan_hash: 0a260291260ec050a7993d32d794972f439e9eaa96833595ca3d63a1678368f4 -->
