# ACM-001 — Actor Catalog

## Purpose

ACM-001 defines the human, system and external actors required to implement the Healthcare Operations Platform MVP.

Actors are defined independently from any identity provider or application framework. Implementation teams may map them to roles, groups, claims or policies, but the source of truth is this catalog.

## Actor Groups

| Group | Description |
| --- | --- |
| internal_staff | Employees and operational users inside the diagnostic organization. |
| clinical_external | Referring physicians and external clinical collaborators. |
| patient_external | Patients and authorized representatives. |
| system_external | External systems, devices, fiscal services and integration partners. |
| platform_system | Internal platform services using scoped service identities. |

## Minimum MVP Roles

The MVP must support these roles before implementation can start safely:

- Platform Super Administrator
- Tenant Administrator
- Branch Administrator
- Receptionist
- Cashier
- Sample Collector
- Laboratory Technician
- Technical Validator
- Medical Validator
- Catalog Manager
- Referring Doctor
- Patient
- Patient Representative
- Integration Partner System
- Service Identity

## Access Scopes

Access must be scoped by context:

- `platform`
- `tenant`
- `laboratory`
- `branch`
- `assigned_patients`
- `represented_patients`
- `self`
- `contract_scoped`
- `device_scoped`
- `fiscal_document_scoped`
- `notification_scoped`
- `audit_scoped`

## Implementation Rule

No MVP endpoint should be generated without mapping it to at least one actor, one permission and one audit expectation.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: ACM-001
  type: actor-catalog
  name: Healthcare Operations Platform Actor Catalog
  version: 1.0.0
  status: draft
  owner: Product Architecture Team
  source_of_truth: 02-domain-definition/actors/acm-001/actor-catalog.md
  depends_on:
  - BCM-001
  - BCM-002
  - HOP-MVP-FWK-001
principles:
- Actors describe intent and access responsibility, not implementation accounts.
- A person may hold multiple roles within a tenant, laboratory or branch.
- External actors interact only through authorized portals, APIs or adapters.
- Clinical and operational actions must be auditable.
- System actors must use scoped service identities.
actor_groups:
  internal_staff:
    description: Employees and operational users working inside the diagnostic organization.
    default_portal: employee_portal
  clinical_external:
    description: Referring physicians and external clinical collaborators.
    default_portal: doctor_portal
  patient_external:
    description: Patients and patient representatives.
    default_portal: patient_portal
  system_external:
    description: External systems, devices, fiscal services and integration partners.
    default_portal: api_or_adapter
  platform_system:
    description: Internal platform services acting through service identities.
    default_portal: system
actors:
- id: ACT-001
  name: Platform Super Administrator
  group: internal_staff
  description: Nexora-side or deployment owner role with environment-level administration
    responsibilities.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-001
  capabilities:
  - BCM-ORG-001
  - BCM-PLT-001
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  permissions:
  - manage_platform_settings
  - manage_tenants
  - view_platform_audit
  data_scope: platform
  audit_level: high
- id: ACT-002
  name: Tenant Administrator
  group: internal_staff
  description: Administrative owner for one diagnostic organization tenant.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-001
  - MVP-MOD-002
  - MVP-MOD-003
  capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-ORG-006
  - BCM-ORG-008
  - BCM-PLT-001
  - BCM-PLT-002
  permissions:
  - manage_laboratories
  - manage_branches
  - manage_users
  - manage_roles
  - manage_organization_configuration
  data_scope: tenant
  audit_level: high
- id: ACT-003
  name: Branch Administrator
  group: internal_staff
  description: Operational administrator for one or more branches.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-001
  - MVP-MOD-004
  - MVP-MOD-005
  - MVP-MOD-006
  capabilities:
  - BCM-ORG-003
  - BCM-ORG-006
  - BCM-ATT-003
  - BCM-ATT-005
  - BCM-LAB-002
  permissions:
  - manage_branch_operations
  - manage_branch_users
  - view_branch_audit
  data_scope: branch
  audit_level: high
- id: ACT-004
  name: Receptionist
  group: internal_staff
  description: Front desk user responsible for patient search, registration, appointments
    and reception.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-003
  - MVP-MOD-004
  capabilities:
  - BCM-PER-002
  - BCM-ATT-001
  - BCM-ATT-002
  - BCM-ATT-003
  - BCM-ATT-004
  - BCM-LAB-001
  permissions:
  - search_patients
  - register_patients
  - create_appointments
  - create_orders
  - manage_reception_queue
  data_scope: branch
  audit_level: high
- id: ACT-005
  name: Cashier
  group: internal_staff
  description: User responsible for cash session, payment and billing request operations.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-005
  capabilities:
  - BCM-ATT-005
  - BCM-ATT-008
  permissions:
  - open_cash_session
  - register_payments
  - cancel_sale_with_approval
  - request_billing
  - close_cash_session
  data_scope: branch
  audit_level: high
- id: ACT-006
  name: Sample Collector
  group: internal_staff
  description: Staff member responsible for sample collection and labeling.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-006
  capabilities:
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-LAB-005
  permissions:
  - view_collection_worklist
  - collect_sample
  - print_sample_label
  - reject_sample_at_collection
  data_scope: branch
  audit_level: high
- id: ACT-007
  name: Laboratory Technician
  group: internal_staff
  description: User responsible for sample reception, processing and result capture.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-006
  capabilities:
  - BCM-LAB-005
  - BCM-LAB-006
  - BCM-RES-001
  permissions:
  - receive_samples
  - process_samples
  - capture_results
  - flag_processing_exception
  data_scope: branch
  audit_level: high
- id: ACT-008
  name: Technical Validator
  group: internal_staff
  description: Authorized user who performs technical validation of laboratory results.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-006
  capabilities:
  - BCM-LAB-008
  - BCM-RES-001
  - BCM-RES-006
  permissions:
  - perform_technical_validation
  - request_result_correction
  - flag_critical_result
  data_scope: laboratory
  audit_level: high
- id: ACT-009
  name: Medical Validator
  group: internal_staff
  description: Licensed clinical authority responsible for medical validation and
    release approval.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-006
  - MVP-MOD-007
  capabilities:
  - BCM-LAB-009
  - BCM-LAB-010
  - BCM-RES-001
  - BCM-RES-006
  permissions:
  - perform_medical_validation
  - release_results
  - request_result_amendment
  data_scope: laboratory
  audit_level: critical
- id: ACT-010
  name: Catalog Manager
  group: internal_staff
  description: User responsible for diagnostic test, analyte, sample, reference range
    and price configuration.
  primary_portals:
  - employee_portal
  mvp_modules:
  - MVP-MOD-002
  capabilities:
  - BCM-SVC-001
  - BCM-SVC-002
  - BCM-SVC-003
  - BCM-SVC-004
  - BCM-SVC-005
  - BCM-SVC-006
  - BCM-SVC-007
  - BCM-SVC-009
  permissions:
  - manage_test_catalog
  - manage_reference_ranges
  - manage_sample_requirements
  - manage_prices
  - publish_catalog_items
  data_scope: laboratory
  audit_level: high
- id: ACT-011
  name: Referring Doctor
  group: clinical_external
  description: External physician who refers patients and reviews released results.
  primary_portals:
  - doctor_portal
  mvp_modules:
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-007
  capabilities:
  - BCM-PER-003
  - BCM-LAB-001
  - BCM-RES-004
  - BCM-RES-005
  permissions:
  - view_assigned_patients
  - create_or_request_order
  - view_released_results
  data_scope: assigned_patients
  audit_level: high
- id: ACT-012
  name: Patient
  group: patient_external
  description: Person receiving diagnostic services and accessing appointments, documents
    and released results.
  primary_portals:
  - patient_portal
  - mobile_apps
  mvp_modules:
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-007
  capabilities:
  - BCM-PER-002
  - BCM-ATT-001
  - BCM-RES-004
  - BCM-RES-005
  permissions:
  - manage_own_profile_limited
  - request_appointment
  - view_own_released_results
  - download_own_reports
  data_scope: self
  audit_level: high
- id: ACT-013
  name: Patient Representative
  group: patient_external
  description: Guardian or authorized representative acting on behalf of a patient.
  primary_portals:
  - patient_portal
  - employee_portal
  mvp_modules:
  - MVP-MOD-003
  - MVP-MOD-007
  capabilities:
  - BCM-PER-002
  - BCM-RES-004
  permissions:
  - view_represented_patient_profile
  - view_represented_patient_results
  data_scope: represented_patients
  audit_level: critical
- id: ACT-014
  name: Integration Partner System
  group: system_external
  description: Authorized external system consuming or producing messages through
    APIs, webhooks or adapters.
  primary_portals:
  - api_or_adapter
  mvp_modules:
  - MVP-MOD-008
  capabilities:
  - BCM-PLT-004
  - BCM-PLT-005
  permissions:
  - call_partner_api
  - receive_webhook
  - submit_normalized_message
  data_scope: contract_scoped
  audit_level: high
- id: ACT-015
  name: Laboratory Device
  group: system_external
  description: Analyzer or device that sends results through ASTM, HL7 or file-based
    integration.
  primary_portals:
  - adapter
  mvp_modules:
  - MVP-MOD-006
  - MVP-MOD-008
  capabilities:
  - BCM-LAB-006
  - BCM-RES-001
  - BCM-PLT-004
  permissions:
  - submit_device_result_message
  data_scope: device_scoped
  audit_level: high
- id: ACT-016
  name: Fiscal Authority Adapter
  group: system_external
  description: Country-pack adapter for fiscal invoice issuance, cancellation and
    delivery.
  primary_portals:
  - adapter
  mvp_modules:
  - MVP-MOD-005
  capabilities:
  - BCM-ATT-008
  - BCM-PLT-004
  permissions:
  - submit_invoice_request
  - receive_invoice_status
  data_scope: fiscal_document_scoped
  audit_level: critical
- id: ACT-017
  name: Notification Service
  group: platform_system
  description: Internal service identity responsible for notification dispatch.
  primary_portals:
  - system
  mvp_modules:
  - MVP-MOD-007
  capabilities:
  - BCM-PLT-003
  - BCM-RES-007
  permissions:
  - dispatch_notification
  - record_notification_status
  data_scope: notification_scoped
  audit_level: high
- id: ACT-018
  name: Audit Service
  group: platform_system
  description: Internal service identity responsible for immutable audit trace recording.
  primary_portals:
  - system
  mvp_modules:
  - MVP-MOD-001
  capabilities:
  - BCM-PLT-007
  permissions:
  - append_audit_event
  - query_audit_events_with_authorization
  data_scope: audit_scoped
  audit_level: critical
access_model:
  scopes:
  - platform
  - tenant
  - laboratory
  - branch
  - assigned_patients
  - represented_patients
  - self
  - contract_scoped
  - device_scoped
  - fiscal_document_scoped
  - notification_scoped
  - audit_scoped
  minimum_mvp_roles:
  - platform_super_admin
  - tenant_admin
  - branch_admin
  - receptionist
  - cashier
  - sample_collector
  - laboratory_technician
  - technical_validator
  - medical_validator
  - catalog_manager
  - referring_doctor
  - patient
  - patient_representative
  - integration_partner
  - service_identity
coverage:
  mvp_modules_covered:
  - MVP-MOD-001
  - MVP-MOD-002
  - MVP-MOD-003
  - MVP-MOD-004
  - MVP-MOD-005
  - MVP-MOD-006
  - MVP-MOD-007
  - MVP-MOD-008
  open_items:
  - Country-specific legal representative roles must be added inside country packs.
  - Imaging-specific roles are deferred until the imaging module is promoted.
```
