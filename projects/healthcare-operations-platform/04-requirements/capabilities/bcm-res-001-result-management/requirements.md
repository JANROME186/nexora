# BCM-RES-001 — Result Management Requirements

**Spanish:** Gestión de Resultados
**Domain:** DOM-07 — Results
**Priority:** Critical
**Roadmap:** MVP1

## Actors

- Laboratory Technician
- Clinical Validator
- Doctor
- Patient

## Portals

- employee_portal
- patient_portal
- doctor_portal

## Related Aggregates

- LaboratoryResult
- ResultReport
- ResultDelivery
- CriticalResultAlert

## Functional Requirements

### FR-RES-001-001

The platform shall support secure creation, validation, release and delivery of gestión de resultados.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-001-002

The platform shall preserve result history, amendments, signatures and delivery evidence.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-001-003

The platform shall enforce clinical approval and authorization rules before publishing gestión de resultados.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-001-004

The platform shall detect and route critical result workflows when configured.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-001-005

The platform shall expose Result Management through patient, doctor and employee portals according to permissions.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-RES-001-001 — Manage Result Management

As an authorized user, I want to manage Result Management so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-001-002 — Search Result Management

As an authorized user, I want to search and filter Result Management so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-001-003 — Audit Result Management

As a supervisor, I want to review the audit history of Result Management so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-001-004 — Configure Result Management

As an administrator, I want to configure Result Management according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-001-005 — Report Result Management

As a manager, I want to view indicators for Result Management so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-RES-001
  type: capability-requirements
  name: Result Management Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-res-001-result-management/requirements.md
  depends_on:
  - BCM-RES-001
  - BCM-001
capability:
  domain_id: DOM-07
  domain_name: Results
  id: BCM-RES-001
  name_en: Result Management
  name_es: Gestión de Resultados
  priority: Critical
  roadmap: MVP1
actors:
- Laboratory Technician
- Clinical Validator
- Doctor
- Patient
portals:
- employee_portal
- patient_portal
- doctor_portal
mobile: supported_when_operationally_required
related_aggregates:
- LaboratoryResult
- ResultReport
- ResultDelivery
- CriticalResultAlert
primary_events:
- ResultCaptured
- ResultValidated
- ResultReleased
- CriticalResultDetected
requirements:
- id: FR-RES-001-001
  type: functional
  capability: BCM-RES-001
  domain: DOM-07
  statement: The platform shall support secure creation, validation, release and delivery
    of gestión de resultados.
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
- id: FR-RES-001-002
  type: functional
  capability: BCM-RES-001
  domain: DOM-07
  statement: The platform shall preserve result history, amendments, signatures and
    delivery evidence.
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
- id: FR-RES-001-003
  type: functional
  capability: BCM-RES-001
  domain: DOM-07
  statement: The platform shall enforce clinical approval and authorization rules
    before publishing gestión de resultados.
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
- id: FR-RES-001-004
  type: functional
  capability: BCM-RES-001
  domain: DOM-07
  statement: The platform shall detect and route critical result workflows when configured.
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
- id: FR-RES-001-005
  type: functional
  capability: BCM-RES-001
  domain: DOM-07
  statement: The platform shall expose Result Management through patient, doctor and
    employee portals according to permissions.
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
- id: US-RES-001-001
  capability: BCM-RES-001
  title: Manage Result Management
  story: As an authorized user, I want to manage Result Management so that the organization
    can operate this capability consistently.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-001-002
  capability: BCM-RES-001
  title: Search Result Management
  story: As an authorized user, I want to search and filter Result Management so that
    I can find records quickly.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-001-003
  capability: BCM-RES-001
  title: Audit Result Management
  story: As a supervisor, I want to review the audit history of Result Management
    so that I can verify accountability and compliance.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-001-004
  capability: BCM-RES-001
  title: Configure Result Management
  story: As an administrator, I want to configure Result Management according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-001-005
  capability: BCM-RES-001
  title: Report Result Management
  story: As a manager, I want to view indicators for Result Management so that I can
    make operational decisions.
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
