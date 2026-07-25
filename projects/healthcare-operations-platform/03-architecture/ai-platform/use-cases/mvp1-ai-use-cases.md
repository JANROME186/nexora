# MVP 1 AI Use Cases

## AI-UC-001: Asistente de recepción

Ayuda a la recepción a responder preguntas frecuentes, preparación para estudios, horarios, requisitos y orientación de servicios.

## AI-UC-002: Explicación simple de resultados para paciente

Genera una explicación en lenguaje sencillo del resultado liberado, con advertencia de que no sustituye consulta médica.

## AI-UC-003: Resumen para médico

Resume resultados liberados y destaca cambios históricos cuando existan datos previos.

## AI-UC-004: Ayuda en captura de documentos

Extrae datos de documentos administrativos o facturas usando OCR con validación humana.

## AI-UC-005: Soporte interno contextual

Permite consultar manuales operativos, políticas y documentación de Nexora mediante RAG.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: AI-UC-SET-001
  type: ai_use_case_set
  version: 0.21.0
use_cases:
- id: AI-UC-001
  name: Reception Assistant
  capability: AI-CAP-004
  risk_level: low
  requires_human_review: false
  fallback: static_faq_search
- id: AI-UC-002
  name: Patient Result Explanation
  capability: AI-CAP-002
  risk_level: high
  requires_human_review: true
  fallback: standard_result_legend
- id: AI-UC-003
  name: Doctor Result Summary
  capability: AI-CAP-002
  risk_level: high
  requires_human_review: true
  fallback: chronological_result_view
- id: AI-UC-004
  name: Document OCR Assistance
  capability: AI-CAP-003
  risk_level: medium
  requires_human_review: conditional
  fallback: manual_capture
```
