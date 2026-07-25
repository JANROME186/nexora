---
id: HOP-BR-BCM-PLT-004
format: markdown_structured_payload
type: business-rules
name: Integration Management Business Rules
version: 0.1.0
status: modeled
---

# Integration Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-PLT-004
  type: business-rules
  name: Integration Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: All inbound external messages must pass through IntegrationAdapterPort
    normalization before reaching any domain module; no domain module may parse a
    raw external protocol payload directly.
  applies_to: IntegrationMessageRecord
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the anti-corruption-layer boundary required by BRM-001-R016
    and context-map.md REL-CTX-011.
  test_refs:
  - TST-INT-004-01
- id: RN-002
  statement: When normalization fails, the message must be assigned a canonical error
    code from the shared error model; raw provider error text must never propagate
    to a domain module.
  applies_to: IntegrationMessageRecord
  enforcement_point: command:NormalizeMessage
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Canonical error mapping requires protocol-specific translation logic.
  test_refs:
  - TST-INT-004-02
- id: RN-003
  statement: Every inbound or outbound message must be processed idempotently using
    its externalMessageId; reprocessing the same identifier must not create a duplicate
    domain effect.
  applies_to: IntegrationMessageRecord
  enforcement_point: command:ReceiveMessage
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Idempotency-key deduplication requires a stateful check against prior
    message records.
  test_refs:
  - TST-INT-004-03
- id: RN-004
  statement: Failed message delivery or processing must be retried using a bounded,
    auditable retry policy; retries must never bypass the owning domain's own commands.
  applies_to: IntegrationMessageRecord
  enforcement_point: command:RetryMessage
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Bounded backoff/retry-limit policy is a custom operational rule,
    not a generic CRUD operation.
  test_refs:
  - TST-INT-004-04
- id: RN-005
  statement: Every integration message lifecycle transition (received, normalized,
    acknowledged, normalization_failed, retrying, dead_lettered) must be audited with
    a correlation id linking inbound and outbound events.
  applies_to: IntegrationMessageRecord
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Correlation-id propagation across inbound/outbound events is a cross-cutting
    audit rule.
  test_refs:
  - TST-INT-004-05
- id: RN-006
  statement: Integration endpoint and message commands must execute within the actor's
    or calling system's tenant and laboratory scope.
  applies_to: IntegrationEndpoint
  enforcement_point: authorization:integration.endpoint.manage, authorization:integration.message.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-INT-004-06
enforcement_summary:
  generatable_rules:
  - RN-006
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
```
