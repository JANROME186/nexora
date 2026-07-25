---
id: HOP-EVT-BCM-PLT-004
format: markdown_structured_payload
type: events
name: Integration Management Events
version: 0.1.0
status: modeled
---

# Integration Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-004
  type: events
  name: Integration Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
domain_events:
- name: IntegrationEndpointRegistered
  description: A new external system, device or partner connection point was registered.
  payload:
  - endpointId
  - protocol
  - direction
  audit: true
- name: ExternalMessageReceived
  description: A raw external message was received by a registered endpoint.
  payload:
  - endpointId
  - externalMessageId
  audit: true
- name: MessageNormalized
  description: An external message was successfully normalized into a canonical, domain-safe
    record.
  payload:
  - endpointId
  - externalMessageId
  - targetBoundedContext
  audit: true
- name: MessageNormalizationFailed
  description: An external message failed normalization and was assigned a canonical
    error code.
  payload:
  - endpointId
  - externalMessageId
  - canonicalErrorCode
  audit: true
- name: IntegrationAcknowledgementSent
  description: A delivery/processing acknowledgement was returned to the external
    system.
  payload:
  - endpointId
  - externalMessageId
  - status
  audit: true
- name: MessageRetryScheduled
  description: A failed message was scheduled for a bounded, auditable retry.
  payload:
  - endpointId
  - externalMessageId
  - retryCount
  audit: true
- name: MessageDeadLettered
  description: A message exhausted its retry budget and was moved to dead-letter status
    for manual review.
  payload:
  - endpointId
  - externalMessageId
  audit: true
integration_events:
  published:
  - name: MessageNormalized
    description: Signals the owning domain that a normalized message is available
      for its own command.
    consumers:
    - laboratory-results
    - orders-samples
  consumed: []
published_language:
- ExternalMessageEnvelope
- NormalizedClinicalMessage
- IntegrationAcknowledgement
```
