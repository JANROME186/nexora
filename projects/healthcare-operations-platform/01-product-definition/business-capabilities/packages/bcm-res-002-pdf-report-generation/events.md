---
id: HOP-EVT-BCM-RES-002
format: markdown_structured_payload
type: events
name: PDF Report Generation Events
version: 0.1.0
status: modeled
---

# Pdf Report Generation Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-RES-002
  type: events
  name: PDF Report Generation Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-002
domain_events:
- name: ReportGenerated
  description: A PDF report was rendered and persisted for a released or amended result.
  payload:
  - reportId
  - resultId
  - reportVersion
  - contentHash
  - generationTrigger
  audit: true
- name: ReportSuperseded
  description: A prior report version was superseded by a newer regeneration.
  payload:
  - reportId
  - resultId
  - reportVersion
  audit: true
- name: ReportGenerationFailed
  description: Report rendering or storage failed.
  payload:
  - resultId
  - reasonCode
  audit: true
integration_events:
  published:
  - name: ReportGenerated
    description: Signals digital delivery and notifications that a report is available.
    consumers:
    - laboratory-results
    - notifications
  consumed:
  - name: ResultReleased
    source: BCM-LAB-010
  - name: ResultAmended
    source: BCM-LAB-010
published_language:
- ReportGenerated
- ReportSuperseded
```
