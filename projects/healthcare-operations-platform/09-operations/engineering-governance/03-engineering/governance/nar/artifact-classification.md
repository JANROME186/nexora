---
id: NAR-ARTIFACT-CLASSIFICATION-001
format: markdown_structured_payload
type: governance-policy
version: 0.34.0
status: approved
---

# Nar Artifact Classification 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: NAR-ARTIFACT-CLASSIFICATION-001
type: governance-policy
owner: Architecture Governance
status: approved
version: 0.34.0
classes:
- name: SOURCE
  description: Artefacto editable manualmente y fuente de verdad.
- name: GENERATED
  description: Artefacto generado por compilador. No debe editarse manualmente.
- name: DERIVED
  description: Artefacto derivado que puede regenerarse o recalcularse.
- name: DEPRECATED
  description: Artefacto obsoleto que no debe ser cargado por agentes.
- name: ARCHIVED
  description: Artefacto histórico conservado solo para trazabilidad.
rules:
- Los agentes solo cargan SOURCE y GENERATED vigentes.
- Los documentos DEPRECATED requieren replacement o removal_reason.
- Los artefactos GENERATED deben declarar generated_from.
- El Knowledge Graph no debe depender de artefactos ARCHIVED.
```
