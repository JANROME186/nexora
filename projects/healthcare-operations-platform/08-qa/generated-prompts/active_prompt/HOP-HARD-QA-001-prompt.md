# TASK: HOP-HARD-QA-001 - Final quality gates, evidence reconciliation and no-open-debt validation
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary
EXECUTION_FLOW: manual
CHANNEL: Manual / IDE task handoff

## 1. Alcance / Objetivos Directos
- Atender el slice final de hardening: Final quality gates, evidence reconciliation and no-open-debt validation.
- Reconciliar todos los items de HOP-FINAL-HARDENING, no solo TD-FMT-001.
- Validar que no exista deuda tecnica `open` o `materially_reduced` sin aceptacion formal de gobierno/producto.
- Bloquear el cierre final si cualquier evidencia, puntero, prompt activo/historico, coverage floor, gate o registro de deuda queda inconsistente.
- Confirmar que cada item cerrado tenga QA Evidence, Security Evidence, handoff y closure validation con `Hard findings: 0`.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Actualizar cada deuda mapeada con evidencia objetiva, estado resultante, riesgo residual y siguiente dueño si no puede cerrarse.
- Ejecutar gates de cierre, punteros, evidencias, deuda técnica, seguridad, cobertura y estado git.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Flujo de Ejecución
- Flujo preferente cuando no se permite o no conviene ejecutar CLI desde el orquestador.
- El operador debe entregar este prompt optimizado al IDE/agente local elegido, por ejemplo Antigravity, Kiro u otro entorno con suscripción existente.
- El agente de IDE debe trabajar en `ROOT`, usar `PROJECT` como carpeta objetivo, cerrar el backlog, hacer commit si no hay bloqueantes y ejecutar `tool: backlog_closure_validator` después del commit.
- No invocar CLI comerciales desde este prompt manual; si requiere permisos, login, Docker u otra acción externa, pedir apoyo explícito al operador y continuar cuando quede resuelto.

## 3. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/HOP-HARD-INT-001-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Deuda tecnica: `08-qa/technical-debt/technical-debt-index.md`.
- Progress ledger: `08-qa/project-tracking/progress-ledger/commercial-product-progress-detail.md`.
- Validaciones de cierre: `08-qa/backlog-validations/`.
- Contexto principal: `06-delivery/commercial-product/backlog-map/modules/HOP-FINAL-HARDENING.md`

## 4. Entregables
- Cambios quality y validaciones asociadas.
- QA Evidence: `08-qa/qa/final-hardening/HOP-HARD-QA-001-validation.md`
- Security Evidence: `08-qa/security-quality/HOP-HARD-QA-001/security-quality-evidence.md`
- Transición: crear `08-qa/handoffs/HOP-HARD-QA-001-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 5. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `test(hop): validate final hardening debt burn-down`.
- No lanzar subagentes comerciales para exploración, lectura masiva, QA documental o formateo; usar herramientas locales/Ollama y `tool: commercial_agent_router` solo para CLI con suscripción local o task ingestion por archivo. No usar API keys por consumo salvo ADR excepcional.
- Finalizar con protocolo handoff & exit: no pedir ni iniciar el siguiente backlog en el mismo chat/sesión.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/HOP-HARD-QA-001-closure-validation.md`.
- No modificar `backlog_validator.py` ni `tool-registry.md` para cerrar el backlog; son controles protegidos.
- Si el validador genera `HOP-HARD-QA-001-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; corregir solo producto/evidencia/registros y repetir commit + validación estricta.
- Máximo 3 intentos de cierre. Si después de 3 intentos el validador sigue fallando, detenerse y reportar hallazgos vigentes, correcciones realizadas y justificación técnica de por qué se considera que debería poder cerrar.
- `git status --short` limpio después del commit y de la validación final.

<!-- ollama_plan_hash: d1bab255eae4d15edca22422ac0e2f557f47c636322f61fc6eccb0b40e774752 -->
