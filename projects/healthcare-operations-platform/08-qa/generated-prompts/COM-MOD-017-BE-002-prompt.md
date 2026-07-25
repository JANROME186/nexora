# TASK: COM-MOD-017-BE-002 - Marketplace Entitlement Enforcement and Billing Boundary
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary_deterministic_prompt

## 1. Alcance / Objetivos Directos
- Implementar enforcement custom de entitlements para instalación, activación y consumo runtime de paquetes marketplace.
- Implementar boundary provider-agnostic para billing sin acoplar HOP a un proveedor propietario.
- Retomar desde el handoff compacto de NXF-FMT-002; no precargar inventarios YAML amplios.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Preservar piso de cobertura Backend >= 84.53%.
- Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.
- Ejecutar gates backend obligatorios: Maven, Java, Docker/BD local, SAST, dependencias, cobertura y scans de seguridad.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Handoff previo: `08-qa/handoffs/NXF-FMT-002-summary.md`
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.
- Contexto principal: `01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/`

## 3. Entregables
- Cambios backend y validaciones asociadas.
- QA Evidence: `08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md/yaml`
- Security Evidence: `08-qa/security-quality/COM-MOD-017-BE-002/security-quality-evidence.md/yaml`
- Transición: crear `08-qa/handoffs/COM-MOD-017-BE-002-summary.md`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; YAML/MD parseables; `git diff --check` limpio.
- Commit: `feat(hop): implement marketplace entitlement enforcement`.
- `git status --short` limpio si no hay bloqueantes.
