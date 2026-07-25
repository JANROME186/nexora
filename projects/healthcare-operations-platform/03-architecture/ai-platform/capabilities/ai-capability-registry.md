# AI Capability Registry

El registro de capacidades de IA define qué puede hacer la plataforma sin acoplarse a un proveedor específico.

| ID | Capacidad | Uso inicial | Criticidad | Requiere humano |
|---|---|---|---|---|
| AI-CAP-001 | LLM Text Generation | Ayuda de redacción administrativa | Media | Sí cuando afecte comunicación clínica |
| AI-CAP-002 | Clinical Result Explanation | Explicación simple para paciente | Alta | Sí |
| AI-CAP-003 | OCR Extraction | Captura de documentos/facturas | Media | Según confianza |
| AI-CAP-004 | RAG Knowledge Search | Consulta de manuales internos | Media | No siempre |
| AI-CAP-005 | Speech to Text | Dictado de notas o indicaciones | Media | Sí |
| AI-CAP-006 | Image Assistance | Apoyo no diagnóstico en imagenología | Alta | Sí |
| AI-CAP-007 | Predictive Analytics | Demanda, inventario, tiempos | Media | No para acción automática crítica |
| AI-CAP-008 | Agentic Workflow | Automatización supervisada | Alta | Sí para acciones sensibles |

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: AI-CAP-REG-001
  type: ai_capability_registry
  name: AI Capability Registry
  version: 0.21.0
  status: draft
capabilities:
- id: AI-CAP-001
  name: LLM Text Generation
  category: generative_text
  provider_agnostic: true
  requires_human_review: conditional
- id: AI-CAP-002
  name: Clinical Result Explanation
  category: clinical_assistance
  provider_agnostic: true
  requires_human_review: true
- id: AI-CAP-003
  name: OCR Extraction
  category: document_ai
  provider_agnostic: true
  requires_human_review: conditional
- id: AI-CAP-004
  name: RAG Knowledge Search
  category: retrieval_augmented_generation
  provider_agnostic: true
  requires_human_review: false
- id: AI-CAP-008
  name: Agentic Workflow
  category: ai_agents
  provider_agnostic: true
  requires_human_review: true
```
