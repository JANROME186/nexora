# BCM-SVC-008 — Container Catalog Requirements

**Spanish:** Catálogo de Contenedores
**Domain:** DOM-03 — Diagnostic Services
**Priority:** Medium
**Roadmap:** MVP2

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

### FR-SVC-008-001

The platform shall allow authorized users to configure and version catálogo de contenedores.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-008-002

The platform shall prevent clinical use of incomplete, inactive or deprecated catálogo de contenedores.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-008-003

The platform shall support effective dates, branch availability and pricing relationships for catálogo de contenedores.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-008-004

The platform shall track changes to catálogo de contenedores and preserve the version used by each diagnostic order.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-SVC-008-005

The platform shall expose Container Catalog configuration through APIs used by ordering, results, billing and inventory.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-SVC-008-001 — Manage Container Catalog

As an authorized user, I want to manage Container Catalog so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-008-002 — Search Container Catalog

As an authorized user, I want to search and filter Container Catalog so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-008-003 — Audit Container Catalog

As a supervisor, I want to review the audit history of Container Catalog so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-008-004 — Configure Container Catalog

As an administrator, I want to configure Container Catalog according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-SVC-008-005 — Report Container Catalog

As a manager, I want to view indicators for Container Catalog so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-SVC-008
  type: capability-requirements
  name: Container Catalog Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-svc-008-container-catalog/requirements.md
  depends_on:
  - BCM-SVC-008
  - BCM-001
capability:
  domain_id: DOM-03
  domain_name: Diagnostic Services
  id: BCM-SVC-008
  name_en: Container Catalog
  name_es: Catálogo de Contenedores
  priority: Medium
  roadmap: MVP2
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
- id: FR-SVC-008-001
  type: functional
  capability: BCM-SVC-008
  domain: DOM-03
  statement: The platform shall allow authorized users to configure and version catálogo
    de contenedores.
  priority: Medium
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
- id: FR-SVC-008-002
  type: functional
  capability: BCM-SVC-008
  domain: DOM-03
  statement: The platform shall prevent clinical use of incomplete, inactive or deprecated
    catálogo de contenedores.
  priority: Medium
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
- id: FR-SVC-008-003
  type: functional
  capability: BCM-SVC-008
  domain: DOM-03
  statement: The platform shall support effective dates, branch availability and pricing
    relationships for catálogo de contenedores.
  priority: Medium
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
- id: FR-SVC-008-004
  type: functional
  capability: BCM-SVC-008
  domain: DOM-03
  statement: The platform shall track changes to catálogo de contenedores and preserve
    the version used by each diagnostic order.
  priority: Medium
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
- id: FR-SVC-008-005
  type: functional
  capability: BCM-SVC-008
  domain: DOM-03
  statement: The platform shall expose Container Catalog configuration through APIs
    used by ordering, results, billing and inventory.
  priority: Medium
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
- id: US-SVC-008-001
  capability: BCM-SVC-008
  title: Manage Container Catalog
  story: As an authorized user, I want to manage Container Catalog so that the organization
    can operate this capability consistently.
  priority: Medium
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-008-002
  capability: BCM-SVC-008
  title: Search Container Catalog
  story: As an authorized user, I want to search and filter Container Catalog so that
    I can find records quickly.
  priority: Medium
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-008-003
  capability: BCM-SVC-008
  title: Audit Container Catalog
  story: As a supervisor, I want to review the audit history of Container Catalog
    so that I can verify accountability and compliance.
  priority: Medium
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-008-004
  capability: BCM-SVC-008
  title: Configure Container Catalog
  story: As an administrator, I want to configure Container Catalog according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: Medium
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-SVC-008-005
  capability: BCM-SVC-008
  title: Report Container Catalog
  story: As a manager, I want to view indicators for Container Catalog so that I can
    make operational decisions.
  priority: Medium
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
