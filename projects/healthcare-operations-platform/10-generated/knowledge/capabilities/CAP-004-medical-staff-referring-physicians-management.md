---
id: CAP-004
format: markdown_structured_payload
type: business_capability
name: Medical Staff & Referring Physicians Management
version: 0.26.0
status: draft
---

# Medical Staff & Referring Physicians Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-004
name: Medical Staff & Referring Physicians Management
type: business_capability
status: draft
version: 0.26.0
owner:
- Product
- Clinical Operations
- Security
- Compliance
principles:
- Security by Design
- Privacy by Design
- Least Privilege
- API Contract First
- Multi-Tenant
- Branch Scoped Access
- Clinical Traceability
- Progressive Experience
relationships:
  depends_on:
  - CAP-001
  - CAP-002
  - CAP-003
  enables:
  - CAP-005-order-management
  - CAP-006-results-reporting
  - CAP-009-doctor-portal
  contracts:
  - API-MED-001
  entities:
  - ENT-MED-001
  - ENT-MED-002
  - ENT-MED-003
  - ENT-MED-004
  - ENT-MED-005
  events:
  - EVT-MED-001
  - EVT-MED-007
  - EVT-MED-009
  - EVT-MED-012
source_of_truth:
  capability: capability-library/CAP-004-medical-staff-referring-physicians-management/README.md
  rules: capability-library/CAP-004-medical-staff-referring-physicians-management/02-business-rules.md
  contract: 05-contracts/contracts/openapi/doctors/doctors.openapi.md
  knowledge_node: knowledge/capabilities/CAP-004-medical-staff-referring-physicians-management.md
```
