# TASK: COM-MOD-017-BE-001 - Marketplace & Entitlements Backend Compilation
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Compilar outputs backend para marketplace catalog, package manifest, offer, license plan, entitlement, installation y billing-adapter.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Preservar piso de cobertura Backend >= 84.25%.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates backend obligatorios: Maven, Java, Docker/BD local, SAST, dependencias, cobertura y scans de seguridad.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `projects/healthcare-operations-platform/08-qa/handoffs/COM-MOD-017-DEF-summary.md`
- Modelos base: `projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/`
- Prompts y estado: inspeccionar `projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml` y `projects/healthcare-operations-platform/PROJECT_STATE.yaml` bajo demanda.

## 3. Entregables
- Cambios backend y tests asociados.
- QA Evidence: `projects/healthcare-operations-platform/08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.[md|yaml]`
- Security Evidence: `projects/healthcare-operations-platform/08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.[md|yaml]`
- Transición: crear `projects/healthcare-operations-platform/08-qa/handoffs/COM-MOD-017-BE-001-summary.md`.
- Actualizar `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; YAML/MD parseables; `git diff --check` limpio.
- Commit: `feat(hop): compile marketplace backend outputs`.
- `git status --short` limpio si no hay bloqueantes.

<!-- ollama_plan_hash: 5257e56086c9390bedfc8880f6308913cdcd2e06dd9cac7bf0145b014b2f7737 -->
