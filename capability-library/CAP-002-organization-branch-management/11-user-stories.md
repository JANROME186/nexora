# 11 User Stories

## Epic CAP-002-E01 Tenant & Organization Setup

### US-ORG-001 Create laboratory tenant

As a SaaS administrator, I want to create a laboratory tenant so that a new customer can operate independently in Nexora.

Acceptance criteria:

- Tenant has unique identifier.
- Tenant default country, timezone, currency and language are required.
- Tenant is created in Draft status.
- Audit event is recorded.
- `TenantCreated` is published.

### US-ORG-002 Activate tenant

As a SaaS administrator, I want to activate a tenant so that its users and branches can start operating.

Acceptance criteria:

- Tenant can only be activated when required configuration is complete.
- Activation publishes `TenantActivated`.
- Inactive tenants cannot create operational transactions.

## Epic CAP-002-E02 Branch Management

### US-ORG-010 Create branch

As a laboratory administrator, I want to create a branch so that my laboratory can operate in multiple locations.

Acceptance criteria:

- Branch code is unique within tenant.
- Branch belongs to exactly one tenant.
- Branch starts in Draft status.
- Branch cannot accept orders until activated.

### US-ORG-011 Activate branch

As a laboratory administrator, I want to activate a branch after completing its configuration so that it can receive patients and orders.

Acceptance criteria:

- Branch has at least one address.
- Branch has at least one schedule.
- Branch has at least one enabled service.
- Branch activation publishes `BranchActivated`.

### US-ORG-012 Deactivate branch

As a laboratory administrator, I want to deactivate a branch so that it no longer appears for new operations.

Acceptance criteria:

- Branch with pending orders cannot be deactivated.
- Primary branch requires reassignment before deactivation.
- Historical records remain accessible.
- `BranchDeactivated` is published.

## Epic CAP-002-E03 Branch Services & Schedule

### US-ORG-020 Define branch schedule

As a branch supervisor, I want to define branch operating hours so that scheduling and service availability are accurate.

Acceptance criteria:

- Schedule supports weekdays, exceptions and holidays.
- Timezone is considered.
- Schedule changes are audited.

### US-ORG-021 Enable branch service

As a laboratory administrator, I want to enable services per branch so that each branch only offers what it can perform.

Acceptance criteria:

- Service must exist in catalog.
- Service must be allowed by license plan.
- `BranchServiceEnabled` is published.

## Epic CAP-002-E04 Organizational Structure

### US-ORG-030 Create organizational unit

As a laboratory administrator, I want to create departments and units so that users can be organized by responsibility.

Acceptance criteria:

- Unit may belong to tenant or branch scope.
- Unit can have parent-child relationship.
- Changes are audited.

### US-ORG-031 Define position hierarchy

As a laboratory administrator, I want to define position hierarchy so that permissions and approvals can reflect the real organization.

Acceptance criteria:

- Position has level and reporting relationship.
- Position can be linked to permission templates later.
