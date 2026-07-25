---
id: DOMAIN-OWNERSHIP-MAP-001
format: markdown_structured_payload
type: domain-ownership-map
version: 0.34.0
status: approved
---

# Domain Ownership Map 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: DOMAIN-OWNERSHIP-MAP-001
type: domain-ownership-map
owner: Domain Architecture
status: approved
version: 0.34.0
ownership:
- aggregate: Patient
  owner_context: BC-CLINICAL
  external_access:
  - BC-REVENUE
  - BC-MIGRATION
  - BC-AI
  - BC-AUDIT
- aggregate: DiagnosticOrder
  owner_context: BC-CLINICAL
  external_access:
  - BC-REVENUE
  - BC-SUPPLY
  - BC-AUDIT
- aggregate: Invoice
  owner_context: BC-REVENUE
  external_access:
  - BC-AUDIT
  - BC-MIGRATION
- aggregate: StockLot
  owner_context: BC-SUPPLY
  external_access:
  - BC-CLINICAL
  - BC-AUDIT
- aggregate: UserAccount
  owner_context: BC-IAM
  external_access:
  - BC-ORGANIZATION
  - BC-AUDIT
- aggregate: Laboratory
  owner_context: BC-ORGANIZATION
  external_access:
  - ALL_AUTHORIZED_CONTEXTS
- aggregate: MigrationProject
  owner_context: BC-MIGRATION
  external_access:
  - BC-AUDIT
  - BC-AI
rules:
- External contexts cannot mutate aggregates they do not own.
- Cross-context changes require commands on the owner context or domain events.
```
