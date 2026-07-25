# BCM-LAB-005 — Sample Reception Requirements

**Spanish:** Recepción de Muestras
**Domain:** DOM-05 — Clinical Operations
**Priority:** High
**Roadmap:** MVP1

## Actors

- Sample Collector
- Laboratory Technician
- Clinical Validator
- Medical Director

## Portals

- employee_portal

## Related Aggregates

- DiagnosticOrder
- Sample
- SampleTransport
- ProcessingBatch
- Validation

## Functional Requirements

### FR-LAB-005-001

The platform shall support traceable execution of recepción de muestras from diagnostic order to result lifecycle.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-LAB-005-002

The platform shall enforce sample, test, branch and user authorization rules before allowing recepción de muestras.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-LAB-005-003

The platform shall record timestamps, responsible users, exceptions and status transitions for recepción de muestras.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-LAB-005-004

The platform shall support barcode/label-driven workflows where operationally required.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-LAB-005-005

The platform shall publish domain events for downstream results, billing, inventory, quality and notification processes.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-LAB-005-001 — Manage Sample Reception

As an authorized user, I want to manage Sample Reception so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-LAB-005-002 — Search Sample Reception

As an authorized user, I want to search and filter Sample Reception so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-LAB-005-003 — Audit Sample Reception

As a supervisor, I want to review the audit history of Sample Reception so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-LAB-005-004 — Configure Sample Reception

As an administrator, I want to configure Sample Reception according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-LAB-005-005 — Report Sample Reception

As a manager, I want to view indicators for Sample Reception so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-LAB-005
  type: capability-requirements
  name: Sample Reception Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-lab-005-sample-reception/requirements.md
  depends_on:
  - BCM-LAB-005
  - BCM-001
capability:
  domain_id: DOM-05
  domain_name: Clinical Operations
  id: BCM-LAB-005
  name_en: Sample Reception
  name_es: Recepción de Muestras
  priority: High
  roadmap: MVP1
actors:
- Sample Collector
- Laboratory Technician
- Clinical Validator
- Medical Director
portals:
- employee_portal
mobile: supported_when_operationally_required
related_aggregates:
- DiagnosticOrder
- Sample
- SampleTransport
- ProcessingBatch
- Validation
primary_events:
- DiagnosticOrderCreated
- SampleCollected
- SampleReceived
- ResultValidationRequested
requirements:
- id: FR-LAB-005-001
  type: functional
  capability: BCM-LAB-005
  domain: DOM-05
  statement: The platform shall support traceable execution of recepción de muestras
    from diagnostic order to result lifecycle.
  priority: High
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
- id: FR-LAB-005-002
  type: functional
  capability: BCM-LAB-005
  domain: DOM-05
  statement: The platform shall enforce sample, test, branch and user authorization
    rules before allowing recepción de muestras.
  priority: High
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
- id: FR-LAB-005-003
  type: functional
  capability: BCM-LAB-005
  domain: DOM-05
  statement: The platform shall record timestamps, responsible users, exceptions and
    status transitions for recepción de muestras.
  priority: High
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
- id: FR-LAB-005-004
  type: functional
  capability: BCM-LAB-005
  domain: DOM-05
  statement: The platform shall support barcode/label-driven workflows where operationally
    required.
  priority: High
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
- id: FR-LAB-005-005
  type: functional
  capability: BCM-LAB-005
  domain: DOM-05
  statement: The platform shall publish domain events for downstream results, billing,
    inventory, quality and notification processes.
  priority: High
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
- id: US-LAB-005-001
  capability: BCM-LAB-005
  title: Manage Sample Reception
  story: As an authorized user, I want to manage Sample Reception so that the organization
    can operate this capability consistently.
  priority: High
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-LAB-005-002
  capability: BCM-LAB-005
  title: Search Sample Reception
  story: As an authorized user, I want to search and filter Sample Reception so that
    I can find records quickly.
  priority: High
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-LAB-005-003
  capability: BCM-LAB-005
  title: Audit Sample Reception
  story: As a supervisor, I want to review the audit history of Sample Reception so
    that I can verify accountability and compliance.
  priority: High
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-LAB-005-004
  capability: BCM-LAB-005
  title: Configure Sample Reception
  story: As an administrator, I want to configure Sample Reception according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: High
  roadmap: MVP1
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-LAB-005-005
  capability: BCM-LAB-005
  title: Report Sample Reception
  story: As a manager, I want to view indicators for Sample Reception so that I can
    make operational decisions.
  priority: High
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
