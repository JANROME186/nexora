---
id: HOP-PRM-BCM-PLT-008
format: markdown_structured_payload
type: permissions
name: Document Management Permissions
version: 1.2.0
status: modeled
---

# Document Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-PLT-008
  type: permissions
  name: Document Management Permissions
  version: 1.2.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-008
permissions:
- code: document.read
  name:
    en: Read & Download Documents
    es: Leer y Descargar Documentos
  description: Grants view metadata and download access for stored documents.
  actions:
  - getDocumentMetadata
  - downloadDocumentBinary
- code: document.manage
  name:
    en: Manage & Upload Documents
    es: Gestionar y Cargar Documentos
  description: Grants permission to upload documents and generate evidence bundles.
  actions:
  - uploadDocument
  - createComplianceEvidencePackage
- code: document.retention.manage
  name:
    en: Manage Legal Hold & Retention Policies
    es: Gestionar Retención Legal y Políticas de Archival
  description: Grants compliance permission to toggle legal hold locks and update
    document retention schedules.
  actions:
  - updateLegalHold
```
