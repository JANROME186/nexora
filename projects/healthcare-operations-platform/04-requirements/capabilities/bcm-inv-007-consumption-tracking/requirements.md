# BCM-INV-007 — Consumption Tracking Requirements

**Spanish:** Consumos
**Domain:** DOM-08 — Inventory
**Priority:** Critical
**Roadmap:** MVP2

## Actors

- Inventory Manager
- Purchasing Officer
- Laboratory Technician

## Portals

- employee_portal

## Related Aggregates

- InventoryItem
- StockLot
- PurchaseOrder
- StockMovement
- WasteRecord

## Functional Requirements

### FR-INV-007-001

The platform shall manage consumos with branch-level stock control, lot traceability and expiration handling.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-INV-007-002

The platform shall prevent the use of expired, inactive or blocked inventory items in clinical operations.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-INV-007-003

The platform shall record stock movements, responsible users and business justification for consumos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-INV-007-004

The platform shall support alerts and reporting for low stock, expiration and consumption trends.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-INV-007-005

The platform shall relate Consumption Tracking to diagnostic services and quality workflows when applicable.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-INV-007-001 — Manage Consumption Tracking

As an authorized user, I want to manage Consumption Tracking so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-INV-007-002 — Search Consumption Tracking

As an authorized user, I want to search and filter Consumption Tracking so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-INV-007-003 — Audit Consumption Tracking

As a supervisor, I want to review the audit history of Consumption Tracking so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-INV-007-004 — Configure Consumption Tracking

As an administrator, I want to configure Consumption Tracking according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-INV-007-005 — Report Consumption Tracking

As a manager, I want to view indicators for Consumption Tracking so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-INV-007
  type: capability-requirements
  name: Consumption Tracking Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-inv-007-consumption-tracking/requirements.md
  depends_on:
  - BCM-INV-007
  - BCM-001
capability:
  domain_id: DOM-08
  domain_name: Inventory
  id: BCM-INV-007
  name_en: Consumption Tracking
  name_es: Consumos
  priority: Critical
  roadmap: MVP2
actors:
- Inventory Manager
- Purchasing Officer
- Laboratory Technician
portals:
- employee_portal
mobile: supported_when_operationally_required
related_aggregates:
- InventoryItem
- StockLot
- PurchaseOrder
- StockMovement
- WasteRecord
primary_events:
- StockReceived
- StockConsumed
- StockExpired
requirements:
- id: FR-INV-007-001
  type: functional
  capability: BCM-INV-007
  domain: DOM-08
  statement: The platform shall manage consumos with branch-level stock control, lot
    traceability and expiration handling.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-INV-007-002
  type: functional
  capability: BCM-INV-007
  domain: DOM-08
  statement: The platform shall prevent the use of expired, inactive or blocked inventory
    items in clinical operations.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-INV-007-003
  type: functional
  capability: BCM-INV-007
  domain: DOM-08
  statement: The platform shall record stock movements, responsible users and business
    justification for consumos.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-INV-007-004
  type: functional
  capability: BCM-INV-007
  domain: DOM-08
  statement: The platform shall support alerts and reporting for low stock, expiration
    and consumption trends.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-INV-007-005
  type: functional
  capability: BCM-INV-007
  domain: DOM-08
  statement: The platform shall relate Consumption Tracking to diagnostic services
    and quality workflows when applicable.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
user_stories:
- id: US-INV-007-001
  capability: BCM-INV-007
  title: Manage Consumption Tracking
  story: As an authorized user, I want to manage Consumption Tracking so that the
    organization can operate this capability consistently.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-INV-007-002
  capability: BCM-INV-007
  title: Search Consumption Tracking
  story: As an authorized user, I want to search and filter Consumption Tracking so
    that I can find records quickly.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-INV-007-003
  capability: BCM-INV-007
  title: Audit Consumption Tracking
  story: As a supervisor, I want to review the audit history of Consumption Tracking
    so that I can verify accountability and compliance.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-INV-007-004
  capability: BCM-INV-007
  title: Configure Consumption Tracking
  story: As an administrator, I want to configure Consumption Tracking according to
    laboratory and branch rules so that the workflow fits operational needs.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-INV-007-005
  capability: BCM-INV-007
  title: Report Consumption Tracking
  story: As a manager, I want to view indicators for Consumption Tracking so that
    I can make operational decisions.
  priority: Critical
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
non_functional_requirements:
- Must support internationalization for visible labels, validation messages and notifications.
- Must record audit metadata for create, update, delete, approve, cancel and release
  actions.
- Must enforce tenant, laboratory and branch isolation.
- Must expose contract-first API behavior when external or frontend integration is
  required.
- Must support accessibility and low-resource device constraints where user-facing.
definition_of_ready:
- Capability exists in BCM-001.
- Owning domain and bounded context are identified.
- Actors and portals are identified.
- Functional requirements are approved.
- Traceability to aggregates and events is present.
definition_of_done:
- OpenAPI contracts generated or updated.
- Backend use cases implemented.
- Frontend/mobile interactions implemented if applicable.
- Unit, integration and contract tests pass.
- Audit, security and observability requirements verified.
```
