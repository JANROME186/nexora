---
id: HOP-API-SRC-BCM-ATT-001
format: markdown_structured_payload
type: openapi-source
name: Appointment Scheduling API Source Model
version: 0.2.0
status: modeled
---

# Appointment Scheduling Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-ATT-001
  type: openapi-source
  name: Appointment Scheduling API Source Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/care-delivery/appointments
  surface_classification: internal
  public_surface:
    status: required
    classification: public
    security: anonymous_rate_limited
    governed_by: BCM-PLT-005 ApiSurfaceRegistration/RateLimitPolicy (classification=public)
    exposed_operations:
    - operation_ref: requestAppointment
      scopes:
      - appointment.request.public
      rate_limit_classification: public
      custom_reason: Anonymous public-website requests create an AppointmentSlot in
        status=requested only (never confirmed); staff confirmation still requires
        appointment.manage. See RN-008.
    note: Realizes the future_surfaces placeholder, now serving COM-MOD-011 (the patient
      portal appointment handoff planned here was delivered separately by COM-MOD-009
      as an authenticated patient_portal surface). Reuses the existing requestAppointment
      operation and the ProspectiveContact schema already defined by BCM-ATT-006;
      no new resource or duplicate schema.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - appointment.manage
resources:
- name: AppointmentSlot
  operations:
  - id: listAppointments
    method: GET
    path: /
    scopes:
    - appointment.read
    generatable: true
  - id: getAppointment
    method: GET
    path: /{appointmentId}
    scopes:
    - appointment.read
    generatable: true
  - id: requestAppointment
    method: POST
    path: /
    scopes:
    - appointment.manage
    generatable: false
    custom_reason: Preparation-instruction surfacing and catalog publication check.
  - id: confirmAppointment
    method: POST
    path: /{appointmentId}/confirm
    scopes:
    - appointment.manage
    generatable: false
    custom_reason: Branch operational-status and overlap validation.
  - id: checkInAppointment
    method: POST
    path: /{appointmentId}/check-in
    scopes:
    - appointment.manage
    generatable: false
    custom_reason: Handoff to order creation.
  - id: cancelAppointment
    method: POST
    path: /{appointmentId}/cancel
    scopes:
    - appointment.manage
    generatable: true
  - id: markAppointmentNoShow
    method: POST
    path: /{appointmentId}/no-show
    scopes:
    - appointment.manage
    generatable: false
    custom_reason: Tenant-configurable grace-period policy.
  - id: getRequestedItems
    method: GET
    path: /{appointmentId}/requested-items
    scopes:
    - appointment.read
    generatable: true
  - id: getAppointmentPreparationInstructions
    method: GET
    path: /{appointmentId}/preparation-instructions
    scopes:
    - appointment.read
    generatable: false
    custom_reason: Aggregates published PreparationSummary content across every requested
      catalog item (RN-007).
schemas_source:
- AppointmentSlot
- RequestedCatalogItem
- PreparationSummary
error_model:
  standard: rfc7807
  domain_errors:
  - code: APPOINTMENT_BRANCH_NOT_ACTIVE
    maps_to_rule: RN-001
  - code: APPOINTMENT_WINDOW_OVERLAP
    maps_to_rule: RN-002
  - code: APPOINTMENT_BRANCH_CAPACITY_EXCEEDED
    maps_to_rule: RN-002
  - code: APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED
    maps_to_rule: RN-003
  - code: APPOINTMENT_SCOPE_MISMATCH
    maps_to_rule: RN-004
  - code: APPOINTMENT_ORDER_HANDOFF_VIOLATION
    maps_to_rule: RN-005
  - code: APPOINTMENT_NO_SHOW_GRACE_PERIOD_ACTIVE
    maps_to_rule: RN-006
```
