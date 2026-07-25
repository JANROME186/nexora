# BCM-AI-003 — Doctor Assistant Requirements

**Spanish:** Asistente Médico
**Domain:** DOM-11 — Artificial Intelligence
**Priority:** High
**Roadmap:** MVP3

## Actors

- AI Operator
- Doctor
- Administrator
- Patient

## Portals

- employee_portal
- doctor_portal
- patient_portal

## Related Aggregates

- AICapability
- AIRequest
- AIContextPackage
- AIRecommendation

## Functional Requirements

### FR-AI-003-001

The platform shall provide asistente médico as an AI-assisted capability with human oversight where clinical or administrative risk exists.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-AI-003-002

The platform shall use privacy-safe context packages and avoid exposing unnecessary sensitive data to AI providers.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-AI-003-003

The platform shall keep AI provider usage behind an abstraction layer.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-AI-003-004

The platform shall log AI requests, generated outputs, confidence metadata and user decisions.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-AI-003-005

The platform shall provide fallback behavior when AI is disabled, unavailable or not licensed.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-AI-003-001 — Manage Doctor Assistant

As an authorized user, I want to manage Doctor Assistant so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-AI-003-002 — Search Doctor Assistant

As an authorized user, I want to search and filter Doctor Assistant so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-AI-003-003 — Audit Doctor Assistant

As a supervisor, I want to review the audit history of Doctor Assistant so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-AI-003-004 — Configure Doctor Assistant

As an administrator, I want to configure Doctor Assistant according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-AI-003-005 — Report Doctor Assistant

As a manager, I want to view indicators for Doctor Assistant so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-AI-003
  type: capability-requirements
  name: Doctor Assistant Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-ai-003-doctor-assistant/requirements.md
  depends_on:
  - BCM-AI-003
  - BCM-001
capability:
  domain_id: DOM-11
  domain_name: Artificial Intelligence
  id: BCM-AI-003
  name_en: Doctor Assistant
  name_es: Asistente Médico
  priority: High
  roadmap: MVP3
actors:
- AI Operator
- Doctor
- Administrator
- Patient
portals:
- employee_portal
- doctor_portal
- patient_portal
mobile: supported_when_operationally_required
related_aggregates:
- AICapability
- AIRequest
- AIContextPackage
- AIRecommendation
primary_events:
- AIRequestCreated
- AIRecommendationGenerated
- AIReviewRequired
requirements:
- id: FR-AI-003-001
  type: functional
  capability: BCM-AI-003
  domain: DOM-11
  statement: The platform shall provide asistente médico as an AI-assisted capability
    with human oversight where clinical or administrative risk exists.
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
- id: FR-AI-003-002
  type: functional
  capability: BCM-AI-003
  domain: DOM-11
  statement: The platform shall use privacy-safe context packages and avoid exposing
    unnecessary sensitive data to AI providers.
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
- id: FR-AI-003-003
  type: functional
  capability: BCM-AI-003
  domain: DOM-11
  statement: The platform shall keep AI provider usage behind an abstraction layer.
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
- id: FR-AI-003-004
  type: functional
  capability: BCM-AI-003
  domain: DOM-11
  statement: The platform shall log AI requests, generated outputs, confidence metadata
    and user decisions.
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
- id: FR-AI-003-005
  type: functional
  capability: BCM-AI-003
  domain: DOM-11
  statement: The platform shall provide fallback behavior when AI is disabled, unavailable
    or not licensed.
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
- id: US-AI-003-001
  capability: BCM-AI-003
  title: Manage Doctor Assistant
  story: As an authorized user, I want to manage Doctor Assistant so that the organization
    can operate this capability consistently.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-AI-003-002
  capability: BCM-AI-003
  title: Search Doctor Assistant
  story: As an authorized user, I want to search and filter Doctor Assistant so that
    I can find records quickly.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-AI-003-003
  capability: BCM-AI-003
  title: Audit Doctor Assistant
  story: As a supervisor, I want to review the audit history of Doctor Assistant so
    that I can verify accountability and compliance.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-AI-003-004
  capability: BCM-AI-003
  title: Configure Doctor Assistant
  story: As an administrator, I want to configure Doctor Assistant according to laboratory
    and branch rules so that the workflow fits operational needs.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-AI-003-005
  capability: BCM-AI-003
  title: Report Doctor Assistant
  story: As a manager, I want to view indicators for Doctor Assistant so that I can
    make operational decisions.
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
