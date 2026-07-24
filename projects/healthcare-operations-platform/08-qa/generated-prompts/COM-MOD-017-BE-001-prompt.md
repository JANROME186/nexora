# TASK: COM-MOD-017-BE-001 - Marketplace & Entitlements Backend Compilation
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Compilar outputs backend para marketplace catalog, package manifest, offer, license plan, entitlement, installation y billing-adapter.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Preservar piso de cobertura Backend >= 84.25%.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates backend obligatorios: Maven, Java, Docker/BD local, SAST, dependencias, cobertura y scans de seguridad.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/COM-MOD-017-DEF-summary.md`
- Modelos base: `01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml` y `PROJECT_STATE.yaml` bajo demanda.

## 3. Entregables
- Cambios backend y tests asociados.
- QA Evidence: `08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.[md|yaml]`
- Security Evidence: `08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.[md|yaml]`
- Transición: crear `08-qa/handoffs/COM-MOD-017-BE-001-summary.md`.
- Actualizar `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; YAML/MD parseables; `git diff --check` limpio.
- Commit: `feat(hop): compile marketplace backend outputs`.
- `git status --short` limpio si no hay bloqueantes.

<!-- ollama_plan_hash: 9874b9b5234343d71ba583316ebc5b459643c45570726c8ea2c2f5ce6d8babe5 -->
