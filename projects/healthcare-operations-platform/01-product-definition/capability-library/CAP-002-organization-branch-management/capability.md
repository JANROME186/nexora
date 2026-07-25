---
id: CAP-002
format: markdown_structured_payload
type: business_capability
name: Organization & Branch Management
version: 0.24.0
status: draft
---

# Organization & Branch Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-002
name: Organization & Branch Management
type: business_capability
status: draft
version: 0.24.0
owner:
- Product
- Operations
- Security
- Data Stewardship
principles:
- Multi-Tenant
- Anywhere First
- API Contract First
- Security by Design
- Configuration over Customization
- Country Pack Ready
maturity_levels:
- level: L1
  name: Single Branch
  description: One laboratory and one branch.
- level: L2
  name: Multi Branch
  description: Multiple branches with schedules and services.
- level: L3
  name: Operational Network
  description: Branch-level users, inventory, caja and reporting.
- level: L4
  name: Intelligent Operations
  description: AI-assisted setup and optimization.
- level: L5
  name: Franchise/Ecosystem
  description: Country packs, marketplace and organization templates.
actors:
- saas_administrator
- laboratory_administrator
- branch_supervisor
- operations_manager
- security_administrator
- integration_client
business_rules:
- id: CAP-002-BR-001
- id: CAP-002-BR-002
- id: CAP-002-BR-003
- id: CAP-002-BR-004
- id: CAP-002-BR-005
- id: CAP-002-BR-006
- id: CAP-002-BR-007
- id: CAP-002-BR-008
- id: CAP-002-BR-009
- id: CAP-002-BR-010
- id: CAP-002-BR-011
- id: CAP-002-BR-012
- id: CAP-002-BR-013
- id: CAP-002-BR-014
- id: CAP-002-BR-015
- id: CAP-002-BR-016
- id: CAP-002-BR-017
- id: CAP-002-BR-018
- id: CAP-002-BR-019
- id: CAP-002-BR-020
state_machines:
- id: SM-ORG-001
  entity: Tenant
- id: SM-ORG-002
  entity: Branch
- id: SM-ORG-003
  entity: ServiceAvailability
decision_tables:
- id: DT-ORG-001
  name: Branch Activation Eligibility
- id: DT-ORG-002
  name: Branch Service Availability
- id: DT-ORG-003
  name: Branch Deactivation
entities:
- ENT-ORG-001
- ENT-ORG-002
- ENT-ORG-003
- ENT-ORG-004
- ENT-ORG-005
- ENT-ORG-006
- ENT-ORG-007
- ENT-ORG-008
- ENT-ORG-009
- ENT-ORG-010
- ENT-ORG-011
- ENT-ORG-012
- ENT-ORG-013
- ENT-ORG-014
- ENT-ORG-015
events:
- EVT-ORG-001
- EVT-ORG-002
- EVT-ORG-003
- EVT-ORG-004
- EVT-ORG-005
- EVT-ORG-006
- EVT-ORG-007
- EVT-ORG-008
- EVT-ORG-009
- EVT-ORG-010
- EVT-ORG-011
- EVT-ORG-012
- EVT-ORG-013
- EVT-ORG-014
apis:
  openapi: 05-contracts/contracts/openapi/organizations/organizations.openapi.md
user_stories:
- US-ORG-001
- US-ORG-002
- US-ORG-010
- US-ORG-011
- US-ORG-012
- US-ORG-020
- US-ORG-021
- US-ORG-030
- US-ORG-031
dependencies:
  upstream:
  - Licensing
  - Country Packs
  downstream:
  - IAM
  - Patients
  - Orders
  - Caja
  - Inventory
  - Billing
  - Reporting
traceability:
  knowledge_index: knowledge/capabilities/CAP-002-organization-branch-management.md
  source_of_truth:
    rules: capability-library/CAP-002-organization-branch-management/02-business-rules.md
    api: 05-contracts/contracts/openapi/organizations/organizations.openapi.md
    domain: capability-library/CAP-002-organization-branch-management/07-domain-model.md
```
