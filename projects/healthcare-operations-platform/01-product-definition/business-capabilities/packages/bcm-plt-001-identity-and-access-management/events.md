---
id: HOP-EVT-BCM-PLT-001
format: markdown_structured_payload
type: events
name: Identity and Access Management Domain Events
version: 0.1.0
status: modeled
---

# Identity And Access Management Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-001
  type: events
  name: Identity and Access Management Domain Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-001
events:
  published:
  - name: UserAuthenticated
    description: Emitted immediately after a user successfully logs in.
    payload:
    - name: userId
      type: uuid
    - name: tenantId
      type: TenantId
    - name: roleCodes
      type: list
    - name: authenticatedAt
      type: datetime
  - name: UserAuthenticationFailed
    description: Emitted after an incorrect login attempt.
    payload:
    - name: username
      type: string
    - name: tenantId
      type: TenantId
    - name: clientIp
      type: string
    - name: failedAt
      type: datetime
  - name: SupportSessionAssisted
    description: Emitted when support personnel impersonate or log in to assist a
      user.
    payload:
    - name: assistantUserId
      type: uuid
    - name: assistedUserId
      type: uuid
    - name: ticketReference
      type: string
    - name: assistedAt
      type: datetime
  consumed: []
```
