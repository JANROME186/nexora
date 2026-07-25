---
id: HOP-UI-BCM-PLT-008
format: markdown_structured_payload
type: ui-model
name: Document Management UI Model
version: 1.2.0
status: modeled
---

# Document Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-008
  type: ui-model
  name: Document Management UI Model
  version: 1.2.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-008
  target_surface: employee_portal
screens:
- id: SCR-DOC-001
  name: Operational & Regulatory Document Library
  title:
    en: Document Library & Regulatory Evidence
    es: Biblioteca Documental y Evidencia Regulatoria
  route: /platform/documents
  permission_required: document.read
  components:
  - id: CMP-DOC-001
    type: DataTable
    title:
      en: Stored Operational & Regulatory Documents
      es: Documentos Operativos y Regulatorios Almacenados
    operations:
    - getDocumentMetadata
    - downloadDocumentBinary
  - id: CMP-DOC-002
    type: UploadModal
    title:
      en: Upload Document
      es: Cargar Documento
    operations:
    - uploadDocument
- id: SCR-DOC-002
  name: Retention & Legal Hold Governance
  title:
    en: Document Retention & Legal Hold
    es: Retención Documental y Bloqueo Legal
  route: /platform/documents/retention
  permission_required: document.retention.manage
  components:
  - id: CMP-DOC-003
    type: LegalHoldTable
    title:
      en: Documents Under Legal Hold or Extended Retention
      es: Documentos en Retención Legal o Archivo Extendido
    operations:
    - updateLegalHold
  - id: CMP-DOC-004
    type: BundleModal
    title:
      en: Create Compliance Evidence Package
      es: Crear Paquete de Evidencia de Cumplimiento
    operations:
    - createComplianceEvidencePackage
i18n:
  namespaces:
  - platform.document_management
  supported_locales:
  - es-MX
  - en-US
```
