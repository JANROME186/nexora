# BCM-SVC-006 — Reference Range Management Requirements

**Spanish:** Valores de Referencia
**Domain:** DOM-03 — Diagnostic Services
**Priority:** Critical
**Roadmap:** MVP1

## Actors

- Laboratory Administrator
- Catalog Manager
- Clinical Supervisor

## Portals

- employee_portal
- public_website

## Related Aggregates

- DiagnosticService
- TestDefinition
- PanelDefinition
- AnalyteDefinition
- ReferenceRange
- PriceList

## Functional Requirements

### FR-SVC-006-001

The platform shall allow authorized users to configure and version valores de referencia.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-006-002

The platform shall prevent clinical use of incomplete, inactive or deprecated valores de referencia.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-006-003

The platform shall support effective dates, branch availability and pricing relationships for valores de referencia.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-006-004

The platform shall track changes to valores de referencia and preserve the version used by each diagnostic order.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-006-005

The platform shall expose Reference Range Management configuration through APIs used by ordering, results, billing and inventory.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-SVC-006-001 — Manage Reference Range Management

As an authorized user, I want to manage Reference Range Management so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-006-002 — Search Reference Range Management

As an authorized user, I want to search and filter Reference Range Management so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-006-003 — Audit Reference Range Management

As a supervisor, I want to review the audit history of Reference Range Management so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-006-004 — Configure Reference Range Management

As an administrator, I want to configure Reference Range Management according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-006-005 — Report Reference Range Management

As a manager, I want to view indicators for Reference Range Management so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-SVC-006
  type: capability-requirements
  name: Reference Range Management Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-svc-006-reference-range-management/requirements.md
  depends_on:
  - BCM-SVC-006
  - BCM-001
capability:
  domain_id: DOM-03
  domain_name: Diagnostic Services
  id: BCM-SVC-006
  name_en: Reference Range Management
  name_es: Valores de Referencia
  priority: Critical
  roadmap: MVP1
actors:
- Laboratory Administrator
- Catalog Manager
- Clinical Supervisor
portals:
- employee_portal
- public_website
mobile: supported_when_operationally_required
related_aggregates:
- DiagnosticService
- TestDefinition
- PanelDefinition
- AnalyteDefinition
- ReferenceRange
- PriceList
primary_events:
- TestDefinitionPublished
- ReferenceRangeUpdated
- PriceListActivated
requirements:
- id: FR-SVC-006-001
  type: functional
  capability: BCM-SVC-006
  domain: DOM-03
  statement: The platform shall allow authorized users to configure and version valores
    de referencia.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-SVC-006-002
  type: functional
  capability: BCM-SVC-006
  domain: DOM-03
  statement: The platform shall prevent clinical use of incomplete, inactive or deprecated
    valores de referencia.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-SVC-006-003
  type: functional
  capability: BCM-SVC-006
  domain: DOM-03
  statement: The platform shall support effective dates, branch availability and pricing
    relationships for valores de referencia.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-SVC-006-004
  type: functional
  capability: BCM-SVC-006
  domain: DOM-03
  statement: The platform shall track changes to valores de referencia and preserve
    the version used by each diagnostic order.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-SVC-006-005
  type: functional
  capability: BCM-SVC-006
  domain: DOM-03
  statement: The platform shall expose Reference Range Management configuration through
    APIs used by ordering, results, billing and inventory.
  priority: Critical
  roadmap: MVP1
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
- id: US-SVC-006-001
  capability: BCM-SVC-006
  title: Manage Reference Range Management
  story: As an authorized user, I want to manage Reference Range Management so that
    the organization can operate this capability consistently.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-006-002
  capability: BCM-SVC-006
  title: Search Reference Range Management
  story: As an authorized user, I want to search and filter Reference Range Management
    so that I can find records quickly.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-006-003
  capability: BCM-SVC-006
  title: Audit Reference Range Management
  story: As a supervisor, I want to review the audit history of Reference Range Management
    so that I can verify accountability and compliance.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-006-004
  capability: BCM-SVC-006
  title: Configure Reference Range Management
  story: As an administrator, I want to configure Reference Range Management according
    to laboratory and branch rules so that the workflow fits operational needs.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-006-005
  capability: BCM-SVC-006
  title: Report Reference Range Management
  story: As a manager, I want to view indicators for Reference Range Management so
    that I can make operational decisions.
  priority: Critical
  roadmap: MVP1
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
