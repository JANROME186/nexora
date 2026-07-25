---
id: HOP-BR-BCM-QLT-007
format: markdown_structured_payload
type: business-rules
name: Audit Management Business Rules
version: 0.1.0
status: modeled
---

# Audit Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-QLT-007
  type: business-rules
  name: Audit Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
rules:
- id: RN-AUD-001
  name: Mandatory Lead Auditor Qualified Role Check
  description: Only users with accredited lead auditor permission scope (quality.audit.execute)
    can be assigned as leadAuditorId.
  enforcement: synchronous
  error_code: AUDIT_LEAD_AUDITOR_UNQUALIFIED
  message:
    en: Assigned lead auditor does not possess required audit execution authority.
    es: El auditor líder asignado no posee la autorización de ejecución de auditorías
      requerida.
- id: RN-AUD-002
  name: Mandatory CAPA Trigger for Critical & Major Non-Conformities
  description: Logging a finding with severity 'critical' or 'major' must automatically
    create a CAPA investigation in BCM-QLT-006.
  enforcement: automatic
  error_code: AUDIT_CAPA_TRIGGER_FAILED
  message:
    en: Failed to trigger CAPA investigation for critical/major audit finding.
    es: Error al activar la investigación CAPA para el hallazgo de auditoría crítico/mayor.
- id: RN-AUD-003
  name: Report Publication Gating
  description: Audit report cannot be published until all finding items have severity
    and evidence references recorded.
  enforcement: synchronous
  error_code: AUDIT_REPORT_INCOMPLETE
  message:
    en: All audit findings must be complete with severity and evidence before report
      publishing.
    es: Todos los hallazgos deben estar documentados con severidad y evidencia antes
      de publicar el informe.
```
