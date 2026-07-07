# Webhook Strategy

**Artifact ID:** IIA-WHK-001  
**Version:** 0.20.0  

## Purpose

Webhooks allow Nexora to notify external systems when relevant business events occur.

## Initial Webhook Events

- `patient.created`
- `patient.updated`
- `order.created`
- `order.cancelled`
- `sample.collected`
- `result.validated`
- `result.delivered`
- `payment.completed`
- `invoice.issued`

## Webhook Requirements

- HTTPS only.
- Signed payloads.
- Event versioning.
- Retry policy.
- Delivery log.
- Replay capability.
- Tenant isolation.
- Secret rotation.

## Payload Envelope

```json
{
  "eventId": "evt_123",
  "eventType": "result.validated",
  "eventVersion": "1.0",
  "tenantId": "tenant_123",
  "occurredAt": "2026-07-07T00:00:00Z",
  "correlationId": "corr_123",
  "data": {}
}
```
