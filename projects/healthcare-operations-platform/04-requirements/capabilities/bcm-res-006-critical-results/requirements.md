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
