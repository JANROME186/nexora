# TASK: HOP-HARD-WEB-001 - Cierre Correctivo de Backlog
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform

## 1. Objetivo
- Corregir únicamente los hallazgos que impiden cerrar `HOP-HARD-WEB-001`.
- No implementar funcionalidad nueva fuera del cierre.
- Mantener ejecución agent-agnostic y no avanzar punteros hasta que todo quede consistente.

## 2. Hallazgos a Cerrar
- product_baseline_stale_active_item: HOP commercial product backlog baseline still points to the closed task.
- git_worktree_not_clean: M PROJECT_STATE.md
 M projects/healthcare-operations-platform/06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md
 M projects/healthcare-operations-platform/08-qa/project-tracking/progress-ledger/commercial-product-progress-detail.md
 M projects/healthcare-operations-platform/08-qa/qa/final-hardening/HOP-HARD-WEB-001-validation.md
 M projects/healthcare-operations-platform/08-qa/security-quality/HOP-HARD-WEB-001/security-quality-evidence.md
?? projects/healthcare-operations-platform/08-qa/backlog-validations/HOP-HARD-WEB-001-closure-validation.md
?? projects/healthcare-operations-platform/08-qa/generated-prompts/HOP-HARD-WEB-001-closure-fix-prompt.md

## 3. Acciones Obligatorias
- Sincronizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, `06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md` y `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md`.
- Confirmar que QA Evidence, Security Evidence y handoff de `HOP-HARD-WEB-001` existan, estén validados y estén referenciados en `SOURCE_OF_TRUTH.md`.
- Ejecutar parseo de Markdown/frontmatter estructurado, sweep de punteros obsoletos y `git diff --check`.
- Si no hay bloqueantes, hacer commit y dejar `git status --short` limpio.

## 4. Criterios de Cierre
- El validador local reporta `decision: closed`.
- No existen hallazgos P0/P1 sin registrar o corregir.
- El siguiente backlog activo queda alineado en todos los registros.

<!-- local_ollama_review: informational_only -->
