---
id: CAP-003
format: markdown_structured_payload
type: business_capability
name: Identity, Access & Workforce Management
version: 0.25.0
status: draft
---

# Identity, Access & Workforce Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-003
name: Identity, Access & Workforce Management
type: business_capability
status: draft
version: 0.25.0
owner:
- Security
- Product
- Operations
- Compliance
principles:
- Security by Design
- Privacy by Design
- Least Privilege
- API Contract First
- Multi-Tenant
- Branch Scoped Access
- Auditability
- Agent Agnostic
maturity_levels:
- level: L1
  name: Basic IAM
  includes:
  - users
  - roles
  - permissions
  - branch access
- level: L2
  name: Workforce IAM
  includes:
  - employees
  - positions
  - access reviews
- level: L3
  name: Enterprise IAM
  includes:
  - SSO
  - MFA
  - emergency access
  - advanced ABAC
relationships:
  depends_on:
  - CAP-002
  protects:
  - CAP-001
  - CAP-004
  - CAP-005
  - CAP-006
  - CAP-007
  contracts:
  - API-IAM-001
  entities:
  - ENT-IAM-001
  - ENT-IAM-002
  - ENT-IAM-003
  - ENT-IAM-004
  events:
  - EVT-IAM-001
  - EVT-IAM-004
  - EVT-IAM-012
  - EVT-IAM-016
source_of_truth:
  capability: capability-library/CAP-003-identity-access-workforce-management/README.md
  rules: capability-library/CAP-003-identity-access-workforce-management/02-business-rules.md
  contract: 05-contracts/contracts/openapi/iam/iam.openapi.md
  knowledge_node: knowledge/capabilities/CAP-003-identity-access-workforce-management.md
```
