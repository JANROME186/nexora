---
id: agent-registry
format: markdown_structured_payload
---

# Agent Registry

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
agents:
- id: master-agent
  name: Master Agent
  purpose: Orquestar tareas leyendo manifiestos, estado, fuentes de verdad y playbooks.
  inputs:
  - PROJECT_MANIFEST.md
  - PROJECT_STATE.md
  - KNOWLEDGE_INDEX.md
  - SOURCE_OF_TRUTH.md
  outputs:
  - execution_plan
  - updated_state
- id: business-architecture-agent
  name: Business Architecture Agent
  purpose: Generar procesos, capabilities, journeys, BPMN y reglas de negocio.
- id: api-contract-agent
  name: API Contract Agent
  purpose: Generar y validar contratos OpenAPI.
- id: database-agent
  name: Database Agent
  purpose: Generar modelo de datos, migraciones conceptuales y seeds.
- id: backend-agent
  name: Backend Agent
  purpose: Implementar casos de uso desde dominio y contratos.
- id: frontend-agent
  name: Frontend Agent
  purpose: Implementar experiencia web progresiva desde historias y design system.
- id: mobile-agent
  name: Mobile Agent
  purpose: Implementar experiencia móvil progresiva y compatible.
- id: security-agent
  name: Security Agent
  purpose: Validar seguridad, privacidad y cumplimiento.
- id: qa-agent
  name: QA Agent
  purpose: Validar pruebas, criterios de aceptación y quality gates.
- id: AGENT-APP-ARCH-001
  name: Application Architecture Agent
  path: agents/application-architecture-agent.md
  responsibilities:
  - application portfolio
  - channel architecture
  - application services
  - integration flows
- id: AGT-011
  name: Product Evolution Agent
  path: agents/product-evolution-agent.md
  status: draft
  version: 0.22.0
```
