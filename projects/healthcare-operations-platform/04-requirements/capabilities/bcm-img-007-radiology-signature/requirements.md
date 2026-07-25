# BCM-IMG-007 — Radiology Signature Requirements

**Spanish:** Firma Radiológica
**Domain:** DOM-06 — Imaging
**Priority:** High
**Roadmap:** MVP3

## Actors

- Radiology Technician
- Radiologist
- Patient
- Doctor

## Portals

- employee_portal
- patient_portal
- doctor_portal

## Related Aggregates

- ImagingStudy
- DicomStudyReference
- RadiologyReport

## Functional Requirements

### FR-IMG-007-001

The platform shall support operational management of firma radiológica for imaging centers and branches.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-IMG-007-002

The platform shall preserve the relationship between patient, order, imaging study, DICOM metadata and radiology report.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-IMG-007-003

The platform shall enforce access controls for imaging records, reports and viewer access.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-IMG-007-004

The platform shall support status tracking from scheduling to study release.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

### FR-IMG-007-005

The platform shall integrate Radiology Signature workflows with orders, results, patient portal and doctor portal.

**Acceptance Criteria**

- Given an authorized user, when the action is performed, then the platform validates permissions before executing it.
- Given valid input data, when the transaction is submitted, then the platform persists the change and records audit metadata.
- Given invalid or incomplete data, when the transaction is submitted, then the platform rejects it with a standardized error response.
- Given a successful state change, when the transaction is completed, then the platform publishes or records the corresponding domain event when applicable.

## User Stories

### US-IMG-007-001 — Manage Radiology Signature

As an authorized user, I want to manage Radiology Signature so that the organization can operate this capability consistently.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-IMG-007-002 — Search Radiology Signature

As an authorized user, I want to search and filter Radiology Signature so that I can find records quickly.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-IMG-007-003 — Audit Radiology Signature

As a supervisor, I want to review the audit history of Radiology Signature so that I can verify accountability and compliance.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-IMG-007-004 — Configure Radiology Signature

As an administrator, I want to configure Radiology Signature according to laboratory and branch rules so that the workflow fits operational needs.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

### US-IMG-007-005 — Report Radiology Signature

As a manager, I want to view indicators for Radiology Signature so that I can make operational decisions.

**Acceptance Criteria**

- Scenario: authorized access is allowed and unauthorized access is denied.
- Scenario: required fields and business rules are validated.
- Scenario: the action is auditable.
- Scenario: the UI provides clear feedback and supports localization.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-IMG-007
  type: capability-requirements
  name: Radiology Signature Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-img-007-radiology-signature/requirements.md
  depends_on:
  - BCM-IMG-007
  - BCM-001
capability:
  domain_id: DOM-06
  domain_name: Imaging
  id: BCM-IMG-007
  name_en: Radiology Signature
  name_es: Firma Radiológica
  priority: High
  roadmap: MVP3
actors:
- Radiology Technician
- Radiologist
- Patient
- Doctor
portals:
- employee_portal
- patient_portal
- doctor_portal
mobile: supported_when_operationally_required
related_aggregates:
- ImagingStudy
- DicomStudyReference
- RadiologyReport
primary_events:
- ImagingStudyScheduled
- DicomStudyReceived
- RadiologyReportSigned
requirements:
- id: FR-IMG-007-001
  type: functional
  capability: BCM-IMG-007
  domain: DOM-06
  statement: The platform shall support operational management of firma radiológica
    for imaging centers and branches.
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
- id: FR-IMG-007-002
  type: functional
  capability: BCM-IMG-007
  domain: DOM-06
  statement: The platform shall preserve the relationship between patient, order,
    imaging study, DICOM metadata and radiology report.
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
- id: FR-IMG-007-003
  type: functional
  capability: BCM-IMG-007
  domain: DOM-06
  statement: The platform shall enforce access controls for imaging records, reports
    and viewer access.
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
- id: FR-IMG-007-004
  type: functional
  capability: BCM-IMG-007
  domain: DOM-06
  statement: The platform shall support status tracking from scheduling to study release.
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
- id: FR-IMG-007-005
  type: functional
  capability: BCM-IMG-007
  domain: DOM-06
  statement: The platform shall integrate Radiology Signature workflows with orders,
    results, patient portal and doctor portal.
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
- id: US-IMG-007-001
  capability: BCM-IMG-007
  title: Manage Radiology Signature
  story: As an authorized user, I want to manage Radiology Signature so that the organization
    can operate this capability consistently.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-IMG-007-002
  capability: BCM-IMG-007
  title: Search Radiology Signature
  story: As an authorized user, I want to search and filter Radiology Signature so
    that I can find records quickly.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-IMG-007-003
  capability: BCM-IMG-007
  title: Audit Radiology Signature
  story: As a supervisor, I want to review the audit history of Radiology Signature
    so that I can verify accountability and compliance.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-IMG-007-004
  capability: BCM-IMG-007
  title: Configure Radiology Signature
  story: As an administrator, I want to configure Radiology Signature according to
    laboratory and branch rules so that the workflow fits operational needs.
  priority: High
  roadmap: MVP3
  acceptance_criteria:
  - 'Scenario: authorized access is allowed and unauthorized access is denied.'
  - 'Scenario: required fields and business rules are validated.'
  - 'Scenario: the action is auditable.'
  - 'Scenario: the UI provides clear feedback and supports localization.'
- id: US-IMG-007-005
  capability: BCM-IMG-007
  title: Report Radiology Signature
  story: As a manager, I want to view indicators for Radiology Signature so that I
    can make operational decisions.
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
