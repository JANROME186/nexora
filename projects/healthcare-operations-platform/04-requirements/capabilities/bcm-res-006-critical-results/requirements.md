# BCM-RES-006 — Critical Results Requirements

**Spanish:** Resultados Críticos
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

### FR-RES-006-001

The platform shall support secure creation, validation, release and delivery of resultados críticos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-006-002

The platform shall preserve result history, amendments, signatures and delivery evidence.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-006-003

The platform shall enforce clinical approval and authorization rules before publishing resultados críticos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-006-004

The platform shall detect and route critical result workflows when configured.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-RES-006-005

The platform shall expose Critical Results through patient, doctor and employee portals according to permissions.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-RES-006-001 — Manage Critical Results

As an authorized user, I want to manage Critical Results so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-006-002 — Search Critical Results

As an authorized user, I want to search and filter Critical Results so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-006-003 — Audit Critical Results

As a supervisor, I want to review the audit history of Critical Results so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-006-004 — Configure Critical Results

As an administrator, I want to configure Critical Results according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-RES-006-005 — Report Critical Results

As a manager, I want to view indicators for Critical Results so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-RES-006
  type: capability-requirements
  name: Critical Results Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-res-006-critical-results/requirements.md
  depends_on:
  - BCM-RES-006
  - BCM-001
capability:
  domain_id: DOM-07
  domain_name: Results
  id: BCM-RES-006
  name_en: Critical Results
  name_es: Resultados Críticos
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
- id: FR-RES-006-001
  type: functional
  capability: BCM-RES-006
  domain: DOM-07
  statement: The platform shall support secure creation, validation, release and delivery
    of resultados críticos.
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
- id: FR-RES-006-002
  type: functional
  capability: BCM-RES-006
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
- id: FR-RES-006-003
  type: functional
  capability: BCM-RES-006
  domain: DOM-07
  statement: The platform shall enforce clinical approval and authorization rules
    before publishing resultados críticos.
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
- id: FR-RES-006-004
  type: functional
  capability: BCM-RES-006
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
- id: FR-RES-006-005
  type: functional
  capability: BCM-RES-006
  domain: DOM-07
  statement: The platform shall expose Critical Results through patient, doctor and
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
- id: US-RES-006-001
  capability: BCM-RES-006
  title: Manage Critical Results
  story: As an authorized user, I want to manage Critical Results so that the organization
    can operate this capability consistently.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-006-002
  capability: BCM-RES-006
  title: Search Critical Results
  story: As an authorized user, I want to search and filter Critical Results so that
    I can find records quickly.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-006-003
  capability: BCM-RES-006
  title: Audit Critical Results
  story: As a supervisor, I want to review the audit history of Critical Results so
    that I can verify accountability and compliance.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-006-004
  capability: BCM-RES-006
  title: Configure Critical Results
  story: As an administrator, I want to configure Critical Results according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: Critical
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-RES-006-005
  capability: BCM-RES-006
  title: Report Critical Results
  story: As a manager, I want to view indicators for Critical Results so that I can
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
