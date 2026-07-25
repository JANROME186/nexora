# TASK: COM-MOD-017-CLOSEOUT - Marketplace readiness closeout and registry update
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Atender el backlog activo: Marketplace readiness closeout and registry update.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates de cierre, punteros, evidencias, deuda técnica, seguridad, cobertura y estado git.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/COM-MOD-017-QA-001-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Contexto principal: `01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/`

## 3. Entregables
- Cambios quality y validaciones asociadas.
- QA Evidence: `08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-CLOSEOUT-validation.md`
- Security Evidence: `08-qa/security-quality/COM-MOD-017-CLOSEOUT/security-quality-evidence.md`
- Transición: crear `08-qa/handoffs/COM-MOD-017-CLOSEOUT-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `test(hop): validate marketplace backlog closure`.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/COM-MOD-017-CLOSEOUT-closure-validation.md`.
- Si el validador genera `COM-MOD-017-CLOSEOUT-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; reportar los hallazgos, corregirlos y repetir commit + validación estricta.
- `git status --short` limpio después del commit y de la validación final.

<!-- ollama_plan_hash: e194b87588a6f56d90c637b53b3b1597128fb29d54bf30f4bf8b9a51580e56c3 -->
