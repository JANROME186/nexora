# BCM-PER-005 — Agreement Management Requirements

**Spanish:** Gestión de Convenios
**Domain:** DOM-02 — People
**Priority:** High
**Roadmap:** MVP2

## Actors

- Receptionist
- Administrator
- Patient
- Doctor

## Portals

- employee_portal
- patient_portal
- doctor_portal

## Related Aggregates

- Person
- Patient
- Doctor
- Company
- Agreement
- Supplier
- Contact

## Functional Requirements

### FR-PER-005-001

The platform shall manage master data for gestión de convenios, avoiding duplicate person records across roles.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-PER-005-002

The platform shall support search, registration, update, deactivation and audit of gestión de convenios.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-PER-005-003

The platform shall validate identity, contact and demographic data according to country-pack rules when applicable.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-PER-005-004

The platform shall allow authorized users to link gestión de convenios to clinical, administrative and portal workflows.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-PER-005-005

The platform shall expose privacy-safe views of gestión de convenios according to user role, consent and authorization.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-PER-005-001 — Manage Agreement Management

As an authorized user, I want to manage Agreement Management so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-PER-005-002 — Search Agreement Management

As an authorized user, I want to search and filter Agreement Management so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-PER-005-003 — Audit Agreement Management

As a supervisor, I want to review the audit history of Agreement Management so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-PER-005-004 — Configure Agreement Management

As an administrator, I want to configure Agreement Management according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-PER-005-005 — Report Agreement Management

As a manager, I want to view indicators for Agreement Management so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-PER-005
  type: capability-requirements
  name: Agreement Management Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-per-005-agreement-management/requirements.md
  depends_on:
  - BCM-PER-005
  - BCM-001
capability:
  domain_id: DOM-02
  domain_name: People
  id: BCM-PER-005
  name_en: Agreement Management
  name_es: Gestión de Convenios
  priority: High
  roadmap: MVP2
actors:
- Receptionist
- Administrator
- Patient
- Doctor
portals:
- employee_portal
- patient_portal
- doctor_portal
mobile: supported_when_operationally_required
related_aggregates:
- Person
- Patient
- Doctor
- Company
- Agreement
- Supplier
- Contact
primary_events:
- PersonRegistered
- PatientRegistered
- DoctorRegistered
requirements:
- id: FR-PER-005-001
  type: functional
  capability: BCM-PER-005
  domain: DOM-02
  statement: The platform shall manage master data for gestión de convenios, avoiding
    duplicate person records across roles.
  priority: High
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
- id: FR-PER-005-002
  type: functional
  capability: BCM-PER-005
  domain: DOM-02
  statement: The platform shall support search, registration, update, deactivation
    and audit of gestión de convenios.
  priority: High
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
- id: FR-PER-005-003
  type: functional
  capability: BCM-PER-005
  domain: DOM-02
  statement: The platform shall validate identity, contact and demographic data according
    to country-pack rules when applicable.
  priority: High
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
- id: FR-PER-005-004
  type: functional
  capability: BCM-PER-005
  domain: DOM-02
  statement: The platform shall allow authorized users to link gestión de convenios
    to clinical, administrative and portal workflows.
  priority: High
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
- id: FR-PER-005-005
  type: functional
  capability: BCM-PER-005
  domain: DOM-02
  statement: The platform shall expose privacy-safe views of gestión de convenios
    according to user role, consent and authorization.
  priority: High
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
- id: US-PER-005-001
  capability: BCM-PER-005
  title: Manage Agreement Management
  story: As an authorized user, I want to manage Agreement Management so that the
    organization can operate this capability consistently.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-PER-005-002
  capability: BCM-PER-005
  title: Search Agreement Management
  story: As an authorized user, I want to search and filter Agreement Management so
    that I can find records quickly.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-PER-005-003
  capability: BCM-PER-005
  title: Audit Agreement Management
  story: As a supervisor, I want to review the audit history of Agreement Management
    so that I can verify accountability and compliance.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-PER-005-004
  capability: BCM-PER-005
  title: Configure Agreement Management
  story: As an administrator, I want to configure Agreement Management according to
    laboratory and branch rules so that the workflow fits operational needs.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-PER-005-005
  capability: BCM-PER-005
  title: Report Agreement Management
  story: As a manager, I want to view indicators for Agreement Management so that
    I can make operational decisions.
  priority: High
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
