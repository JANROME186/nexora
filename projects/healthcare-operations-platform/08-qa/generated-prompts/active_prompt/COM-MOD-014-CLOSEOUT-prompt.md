# TASK: COM-MOD-014-CLOSEOUT - Module closeout and registry update
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Atender el backlog activo: Module closeout and registry update.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates de cierre, punteros, evidencias, deuda técnica, seguridad, cobertura y estado git.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/COM-MOD-014-QA-001-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Contexto principal: `01-product-definition/business-capabilities/packages/`

## 3. Entregables
- Cambios quality y validaciones asociadas.
- QA Evidence: `08-qa/qa/imaging-operations/COM-MOD-014-CLOSEOUT-validation.md`
- Security Evidence: `08-qa/security-quality/COM-MOD-014-CLOSEOUT/security-quality-evidence.md`
- Transición: crear `08-qa/handoffs/COM-MOD-014-CLOSEOUT-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `test(hop): validate imaging operations backlog closure`.
- No lanzar subagentes comerciales para exploración, lectura masiva, QA documental o formateo; usar herramientas locales/Ollama y `tool: commercial_agent_router` solo para CLI con suscripción local o task ingestion por archivo. No usar API keys por consumo salvo ADR excepcional.
- Finalizar con protocolo handoff & exit: no pedir ni iniciar el siguiente backlog en el mismo chat/sesión.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/COM-MOD-014-CLOSEOUT-closure-validation.md`.
- No modificar `backlog_validator.py` ni `tool-registry.md` para cerrar el backlog; son controles protegidos.
- Si el validador genera `COM-MOD-014-CLOSEOUT-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; corregir solo producto/evidencia/registros y repetir commit + validación estricta.
- Máximo 3 intentos de cierre. Si después de 3 intentos el validador sigue fallando, detenerse y reportar hallazgos vigentes, correcciones realizadas y justificación técnica de por qué se considera que debería poder cerrar.
- `git status --short` limpio después del commit y de la validación final.

<!-- ollama_plan_hash: 77b783a5889114822fb7ddb489fda03bc2758aa15eb4e41b4a8b5ece61892c49 -->
