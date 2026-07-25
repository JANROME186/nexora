---
id: MVP-MOD-001-TRACE
format: markdown_structured_payload
type: traceability-record
name: Platform Foundation Traceability
version: 1.0.0
status: approved
---

# Platform Foundation Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-001-TRACE
  type: traceability-record
  name: Platform Foundation Traceability
  version: 1.0.0
  status: approved
module: MVP-MOD-001
source_artifacts:
- 06-delivery/mvp/healthcare-operations-platform-mvp-framework.md
- 02-domain-definition/actors/acm-001/actor-catalog.md
- 02-domain-definition/processes/hrp-001/healthcare-reference-processes.md
- 02-domain-definition/business-rules/brm-001/business-rules-catalog.md
- 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
capability_to_api:
  BCM-ORG-001:
  - /platform/tenants
  BCM-ORG-002:
  - /organization/laboratories
  BCM-ORG-003:
  - /organization/branches
  BCM-ORG-006:
  - /identity/users
  BCM-ORG-008:
  - /organization/laboratories
  - /organization/branches
  BCM-PLT-001:
  - /identity/users
  - /identity/users/{userId}/role-assignments
  BCM-PLT-002:
  - /platform/tenants
  BCM-PLT-006:
  - local_observability_profile
  BCM-PLT-007:
  - /audit/events
actor_to_permissions:
  ACT-001:
  - manage_tenants
  - manage_platform_settings
  - view_platform_audit
  ACT-002:
  - manage_laboratories
  - manage_branches
  - manage_users
  - manage_roles
  ACT-003:
  - manage_branch_operations
  - manage_branch_users
  - view_branch_audit
  ACT-018:
  - append_audit_event
  - query_audit_events_with_authorization
rules:
- BRM-001-R001
- BRM-001-R002
- BRM-001-R018
processes:
- HRP-001-P01
development_start_status:
  ready: true
  notes: Platform Foundation has actors, rules, process, APIs, data model, UI map,
    tests and traceability.
implementation_closeout_status:
  implemented: true
  ready_for_functional_validation: true
  evidence:
  - 08-qa/qa/platform-foundation/PF-BE-001-backend-skeleton.md
  - 08-qa/qa/platform-foundation/PF-OPS-001-local-runtime.md
  - 08-qa/qa/platform-foundation/PF-BE-002-organization-commands.md
  - 08-qa/qa/platform-foundation/PF-BE-003-identity-access.md
  - 08-qa/qa/platform-foundation/PF-BE-004-audit-event-recording.md
  - 08-qa/qa/platform-foundation/PF-FE-001-employee-portal-administration.md
  - 08-qa/qa/platform-foundation/PF-APP-001-mobile-app-foundation.md
  - 08-qa/qa/platform-foundation/PF-QA-001-smoke-and-contract-tests.md
  - 08-qa/qa/platform-foundation/MVP-MOD-001-closeout.md
```
