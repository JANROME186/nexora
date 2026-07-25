---
id: HOP-EVT-BCM-PLT-005
format: markdown_structured_payload
type: events
name: API Management Events
version: 0.1.0
status: modeled
---

# Api Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-005
  type: events
  name: API Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-005
domain_events:
- name: ApiSurfaceClassified
  description: An API operation was classified as public, internal or partner.
  payload:
  - registrationId
  - ownerCapability
  - operationId
  - classification
  audit: true
- name: PartnerApiKeyIssued
  description: A scoped partner API key was issued to a consumer.
  payload:
  - keyId
  - consumerName
  - grantedScopes
  audit: true
- name: PartnerApiKeyRevoked
  description: A partner API key was revoked.
  payload:
  - keyId
  - consumerName
  audit: true
- name: ApiDeprecationScheduled
  description: A published operation was scheduled for deprecation with a documented
    window and migration note.
  payload:
  - registrationId
  - operationId
  - deprecationWindow
  audit: true
- name: ApiRateLimitExceeded
  description: A consumer exceeded its configured rate-limit policy.
  payload:
  - registrationId
  - consumerName
  audit: true
integration_events:
  published:
  - name: PartnerApiKeyRevoked
    description: Signals dependent partner-facing surfaces (e.g. doctor portal partner
      API) that a key is no longer valid.
    consumers: []
  consumed:
  - name: IntegrationAcknowledgementSent
    description: Consumed from BCM-PLT-004 to correlate integration-partner delivery
      status with API-level observability.
    producer: BCM-PLT-004
published_language:
- ApiSurfaceClassified
- PartnerApiKeyIssued
- ApiDeprecationScheduled
```
