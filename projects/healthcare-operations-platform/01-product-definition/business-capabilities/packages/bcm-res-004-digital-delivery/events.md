---
id: HOP-EVT-BCM-RES-004
format: markdown_structured_payload
type: events
name: Digital Delivery Events
version: 0.1.0
status: modeled
---

# Digital Delivery Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-RES-004
  type: events
  name: Digital Delivery Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
domain_events:
- name: ResultDeliveryAuthorized
  description: A delivery ticket passed its authorization check and became visible
    to its recipient's channel.
  payload:
  - deliveryTicketId
  - resultId
  - recipientType
  - deliveryChannel
  audit: true
- name: ResultViewed
  description: An authorized recipient opened their delivered result.
  payload:
  - deliveryTicketId
  - resultId
  - recipientId
  - viewedAt
  audit: true
- name: ResultDeliveryWithheld
  description: An existing delivery was withheld pending re-authorization after an
    amendment.
  payload:
  - deliveryTicketId
  - resultId
  audit: true
integration_events:
  published:
  - name: ResultDeliveryAuthorized
    description: Signals notification dispatch that a recipient should be informed
      a result is available.
    consumers:
    - notifications
  - name: ResultViewed
    description: Signals audit-compliance of external result access.
    consumers:
    - audit-compliance
  consumed:
  - name: ResultReleased
    source: BCM-LAB-010
  - name: ResultAmended
    source: BCM-LAB-010
  - name: ReportGenerated
    source: BCM-RES-002
published_language:
- ResultDeliveryAuthorized
- ResultViewed
```
