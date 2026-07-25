---
id: TD-IAM-002
format: markdown_structured_payload
type: technical-debt-item
name: Permission model is screen-level only; per-action and per-API-operation granularity,
  and the full domain.resource.action.scope grammar from AUTHZ-ARCH-001, remain unmodeled
version: 1.0.0
status: materially_reduced
---

# Permission Model Is Screen Level Only; Per Action And Per Api Operation Granularity, And The Full Domain.Resource.Action.Scope Grammar From Authz Arch 001, Remain Unmodeled

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-IAM-002
  type: technical-debt-item
  name: Permission model is screen-level only; per-action and per-API-operation granularity,
    and the full domain.resource.action.scope grammar from AUTHZ-ARCH-001, remain
    unmodeled
  version: 1.0.0
  status: materially_reduced
  created_date: 2026-07-17
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/security-compliance/iam-permission-model.md
classification:
  category: security_foundation_gap
  affected_area: iam_permission_granularity
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/PermissionCode.java
  - 07-implementation/employee-portal/src/state/permissions.ts
  risk_level: medium
  urgency: medium
  blocking: false
  reason_non_blocking: 'Corrective closure added EndpointPermissionRegistry, binding
    backend API paths to capability, PermissionCode and HTTP action before controller
    execution. Remaining scope is finer in-screen button-level and domain.resource.action.scope
    grammar expansion, not the absence of API/action mapping.

    '
current_state:
  issue: 'PermissionCode has 40 capability/screen/action values (30 baseline plus
    8 granular portal action permissions added by COM-MOD-009-BE-001, plus 2 more
    doctor-portal permissions added by COM-MOD-009-PORTAL-002) and EndpointPermissionRegistry
    maps protected backend API paths to a PermissionCode, HTTP action and business
    capability id. COM-MOD-009-BE-001 added granular action permissions for patient
    profiles, appointments, orders, results, notifications, and support impersonation,
    showing material progress towards action-level granularity. COM-MOD-009-PORTAL-002
    went a step further than the coarse path-to-permission registry mapping: it added
    real per-request ownership enforcement for the doctor portal (HopAuthorizationInterceptor
    verifies a REFERRING_DOCTOR''s own doctorId query parameter matches their authenticated
    identity before allowing the orders list, and ResultHistoryService verifies via
    a new ReferringDoctorAuthorizationPort that the doctor has actually referred the
    requested patient before returning result history) -- a real instance of the "domain.resource.action.scope"
    ownership check this item''s target_state describes, not just a coarser path/permission
    mapping.

    '
  compensating_control:
  - Screen-level filtering already prevents unauthorized navigation to entire feature
    areas.
  - Backend request-time enforcement now protects mapped API paths with 401/403 decisions.
  - Active support impersonation is sandboxed specifically with the read-only SUPPORT
    role.
  - COM-MOD-009-PORTAL-002 added real doctor/patient-referral ownership checks (not
    just coarse permission checks) for the doctor portal's orders and results-history
    endpoints.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - identityaccess/domain/PermissionCode.java (extend with per-action values as needed)
  - Per-screen action buttons in 07-implementation/employee-portal/src/components/screens/
remediation:
  strategy: gradual_when a future backlog item adds or touches a protected action
  owner: security_architecture_team
  estimated_effort: medium (incremental, per-action, spread across many future backlog
    items)
  estimated_cost_impact: low
  target_backlog: gradual_when_AUTHZ-ARCH-001_is_promoted_from_Draft_or_a_new_protected_action_ships
  dependencies_or_prerequisites:
  - Future authorization-model promotion if domain.resource.action.scope becomes mandatory.
  incremental_remediation_triggers:
  - A future backlog item adds a new protected action.
  - AUTHZ-ARCH-001 is promoted out of Draft status.
  acceptance_criteria:
  - Every protected backend command/query has its own permission code.
  - Button/action-level UI filtering exists for at least the highest-risk actions
    (cancel order, void sale, release critical result).
  progress_evidence:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/EndpointPermissionRegistry.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/EndpointPermissionRegistryTest.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/PermissionCode.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/RolePermissionCatalog.java
  owner_or_responsible_role: security_architecture_team
```
