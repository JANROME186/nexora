# BCM-QLT-002 — External Quality Controls Requirements

**Spanish:** Controles Externos
**Domain:** DOM-09 — Quality
**Priority:** High
**Roadmap:** MVP3

## Actors

- Quality Manager
- Laboratory Supervisor
- Auditor

## Portals

- employee_portal

## Related Aggregates

- QualityControl
- Calibration
- Equipment
- MaintenanceRecord
- CAPA
- Audit

## Functional Requirements

### FR-QLT-002-001

The platform shall support documented execution and traceability of controles externos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-QLT-002-002

The platform shall allow authorized users to record observations, evidence, corrective actions and approvals for controles externos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-QLT-002-003

The platform shall link controles externos to equipment, tests, branches, users and affected results where applicable.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-QLT-002-004

The platform shall generate audit-ready evidence and reports for controles externos.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-QLT-002-005

The platform shall support configurable workflows and notifications for quality exceptions.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-QLT-002-001 — Manage External Quality Controls

As an authorized user, I want to manage External Quality Controls so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-QLT-002-002 — Search External Quality Controls

As an authorized user, I want to search and filter External Quality Controls so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-QLT-002-003 — Audit External Quality Controls

As a supervisor, I want to review the audit history of External Quality Controls so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-QLT-002-004 — Configure External Quality Controls

As an administrator, I want to configure External Quality Controls according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-QLT-002-005 — Report External Quality Controls

As a manager, I want to view indicators for External Quality Controls so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-QLT-002
  type: capability-requirements
  name: External Quality Controls Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-qlt-002-external-quality-controls/requirements.md
  depends_on:
  - BCM-QLT-002
  - BCM-001
capability:
  domain_id: DOM-09
  domain_name: Quality
  id: BCM-QLT-002
  name_en: External Quality Controls
  name_es: Controles Externos
  priority: High
  roadmap: MVP3
actors:
- Quality Manager
- Laboratory Supervisor
- Auditor
portals:
- employee_portal
mobile: supported_when_operationally_required
related_aggregates:
- QualityControl
- Calibration
- Equipment
- MaintenanceRecord
- CAPA
- Audit
primary_events:
- QualityControlExecuted
- CalibrationCompleted
- NonConformityDetected
requirements:
- id: FR-QLT-002-001
  type: functional
  capability: BCM-QLT-002
  domain: DOM-09
  statement: The platform shall support documented execution and traceability of controles
    externos.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-QLT-002-002
  type: functional
  capability: BCM-QLT-002
  domain: DOM-09
  statement: The platform shall allow authorized users to record observations, evidence,
    corrective actions and approvals for controles externos.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-QLT-002-003
  type: functional
  capability: BCM-QLT-002
  domain: DOM-09
  statement: The platform shall link controles externos to equipment, tests, branches,
    users and affected results where applicable.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-QLT-002-004
  type: functional
  capability: BCM-QLT-002
  domain: DOM-09
  statement: The platform shall generate audit-ready evidence and reports for controles
    externos.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - Given an authorized user, when the action is performed, then the platform validates
    permissions before executing it.
  - Given valid input data, when the transaction is submitted, then the platform persists
    the change and records audit metadata.
  - Given invalid or incomplete data, when the transaction is submitted, then the
    platform rejects it with a standardized error response.
  - Given a successful state change, when the transaction is completed, then the platform
    publishes or records the corresponding domain event when applicable.
- id: FR-QLT-002-005
  type: functional
  capability: BCM-QLT-002
  domain: DOM-09
  statement: The platform shall support configurable workflows and notifications for
    quality exceptions.
  priority: High
  roadmap: MVP3
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
- id: US-QLT-002-001
  capability: BCM-QLT-002
  title: Manage External Quality Controls
  story: As an authorized user, I want to manage External Quality Controls so that
    the organization can operate this capability consistently.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-QLT-002-002
  capability: BCM-QLT-002
  title: Search External Quality Controls
  story: As an authorized user, I want to search and filter External Quality Controls
    so that I can find records quickly.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-QLT-002-003
  capability: BCM-QLT-002
  title: Audit External Quality Controls
  story: As a supervisor, I want to review the audit history of External Quality Controls
    so that I can verify accountability and compliance.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-QLT-002-004
  capability: BCM-QLT-002
  title: Configure External Quality Controls
  story: As an administrator, I want to configure External Quality Controls according
    to laboratory and branch rules so that the workflow fits operational needs.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-QLT-002-005
  capability: BCM-QLT-002
  title: Report External Quality Controls
  story: As a manager, I want to view indicators for External Quality Controls so
    that I can make operational decisions.
  priority: High
  roadmap: MVP3
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
