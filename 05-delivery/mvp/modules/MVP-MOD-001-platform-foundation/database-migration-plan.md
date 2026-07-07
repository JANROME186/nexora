# MVP-MOD-001 — Database Migration Plan

## Schemas

- `organization`
- `identity`
- `audit`

## Initial Tables

### organization.tenants

- `tenant_id`
- `name`
- `status`
- `created_at`
- `updated_at`

### organization.laboratories

- `laboratory_id`
- `tenant_id`
- `name`
- `status`
- `created_at`
- `updated_at`

### organization.branches

- `branch_id`
- `tenant_id`
- `laboratory_id`
- `name`
- `status`
- `created_at`
- `updated_at`

### identity.user_accounts

- `user_id`
- `tenant_id`
- `display_name`
- `email`
- `status`
- `created_at`
- `updated_at`

### identity.role_assignments

- `role_assignment_id`
- `user_id`
- `role_code`
- `scope_type`
- `scope_id`
- `created_at`
- `created_by`

### audit.audit_events

- `audit_event_id`
- `occurred_at`
- `tenant_id`
- `actor_id`
- `actor_type`
- `action`
- `subject_type`
- `subject_id`
- `metadata_json`

## Rules

- Use UUID or ULID identifiers consistently.
- Enforce foreign keys inside the same schema where possible.
- Audit events are append-only.
- Do not store secrets in application tables.
