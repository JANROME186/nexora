# BCM-SVC-002 — Test Catalog Requirements

**Spanish:** Catálogo de Pruebas
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

### FR-SVC-002-001

The platform shall allow authorized users to configure and version catálogo de pruebas.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-002-002

The platform shall prevent clinical use of incomplete, inactive or deprecated catálogo de pruebas.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-002-003

The platform shall support effective dates, branch availability and pricing relationships for catálogo de pruebas.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-002-004

The platform shall track changes to catálogo de pruebas and preserve the version used by each diagnostic order.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-002-005

The platform shall expose Test Catalog configuration through APIs used by ordering, results, billing and inventory.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-SVC-002-001 — Manage Test Catalog

As an authorized user, I want to manage Test Catalog so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-002-002 — Search Test Catalog

As an authorized user, I want to search and filter Test Catalog so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-002-003 — Audit Test Catalog

As a supervisor, I want to review the audit history of Test Catalog so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-002-004 — Configure Test Catalog

As an administrator, I want to configure Test Catalog according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-002-005 — Report Test Catalog

As a manager, I want to view indicators for Test Catalog so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-SVC-002
  type: capability-requirements
  name: Test Catalog Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-svc-002-test-catalog/requirements.md
  depends_on:
  - BCM-SVC-002
  - BCM-001
capability:
  domain_id: DOM-03
  domain_name: Diagnostic Services
  id: BCM-SVC-002
  name_en: Test Catalog
  name_es: Catálogo de Pruebas
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
- id: FR-SVC-002-001
  type: functional
  capability: BCM-SVC-002
  domain: DOM-03
  statement: The platform shall allow authorized users to configure and version catálogo
    de pruebas.
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
- id: FR-SVC-002-002
  type: functional
  capability: BCM-SVC-002
  domain: DOM-03
  statement: The platform shall prevent clinical use of incomplete, inactive or deprecated
    catálogo de pruebas.
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
- id: FR-SVC-002-003
  type: functional
  capability: BCM-SVC-002
  domain: DOM-03
  statement: The platform shall support effective dates, branch availability and pricing
    relationships for catálogo de pruebas.
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
- id: FR-SVC-002-004
  type: functional
  capability: BCM-SVC-002
  domain: DOM-03
  statement: The platform shall track changes to catálogo de pruebas and preserve
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
- id: FR-SVC-002-005
  type: functional
  capability: BCM-SVC-002
  domain: DOM-03
  statement: The platform shall expose Test Catalog configuration through APIs used
    by ordering, results, billing and inventory.
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
- id: US-SVC-002-001
  capability: BCM-SVC-002
  title: Manage Test Catalog
  story: As an authorized user, I want to manage Test Catalog so that the organization
    can operate this capability consistently.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-002-002
  capability: BCM-SVC-002
  title: Search Test Catalog
  story: As an authorized user, I want to search and filter Test Catalog so that I
    can find records quickly.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-002-003
  capability: BCM-SVC-002
  title: Audit Test Catalog
  story: As a supervisor, I want to review the audit history of Test Catalog so that
    I can verify accountability and compliance.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-002-004
  capability: BCM-SVC-002
  title: Configure Test Catalog
  story: As an administrator, I want to configure Test Catalog according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-002-005
  capability: BCM-SVC-002
  title: Report Test Catalog
  story: As a manager, I want to view indicators for Test Catalog so that I can make
    operational decisions.
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
