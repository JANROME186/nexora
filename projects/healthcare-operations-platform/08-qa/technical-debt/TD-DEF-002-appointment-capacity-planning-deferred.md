---
id: TD-DEF-002
format: markdown_structured_payload
type: technical-debt-item
name: Appointment capacity planning against detailed branch schedules is deferred
  to BCM-ORG-007
version: 1.1.0
status: open
---

# Appointment Capacity Planning Against Detailed Branch Schedules Is Deferred To Bcm Org 007

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-DEF-002
  type: technical-debt-item
  name: Appointment capacity planning against detailed branch schedules is deferred
    to BCM-ORG-007
  version: 1.1.0
  status: open
  created_date: 2026-07-15
  updated_date: 2026-07-25
source:
  discovered_during_backlog_item: MVP-MOD-004-DEF
  module: MVP-MOD-004 Front Desk and Care Delivery
  evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-DEF-validation.md
  partial_remediation_backlog_item: COM-MOD-014-BE-001
  partial_remediation_evidence: 08-qa/qa/imaging-operations/COM-MOD-014-BE-001-validation.md
classification:
  category: capability_scope_boundary
  affected_area: appointment_scheduling_capacity_validation
  affected_components:
  - 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-model.md
  - 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-rules.md
  - 01-product-definition/business-capabilities/packages/bcm-img-001-imaging-appointment-scheduling/business-model.md
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/imagingoperations/appointmentscheduling/application/ImagingAppointmentSchedulingService.java
  risk_level: low
  blocking: false
  reason_non_blocking: 'BCM-ATT-001 RN-001 and RN-002 validate branch operational
    status and same-patient overlap, which is sufficient for MVP-MOD-004''s Front
    Desk and Care Delivery scope. Detailed capacity planning (staff schedules, equipment
    slots, per-service concurrency limits) depends on BCM-ORG-007 Schedule Management,
    which is roadmapped for MVP2 and is explicitly declared as an optional_capabilities
    dependency, not a required one.

    '
current_state:
  issue: 'MVP-MOD-004-BE-002 added a simple, tenant-configurable daily branch appointment
    capacity check (FrontDeskPolicyStore.branchDailyAppointmentCapacityFor, default
    200/day) enforced in AppointmentSchedulingService.confirm(). COM-MOD-014-DEF
    further expanded capacity modeling for diagnostic imaging in BCM-IMG-001 by introducing
    ImagingModalitySlot and ProcedureRoomSchedule aggregate entities, modality concurrency
    ceilings, DICOM study preparation lead times, and room allocation constraints.
    COM-MOD-014-BE-001 compiled these models into runtime backend execution: ImagingAppointmentSchedulingService.scheduleSlot()
    enforces procedure room schedule concurrency checks against overlapping slots, throwing a ROOM_NOT_AVAILABLE
    domain exception. This further materially reduces unconstrained scheduling risk for imaging operations while
    full BCM-ORG-007 schedule management remains planned for MVP2.

    '
  compensating_control:
  - RN-001 blocks confirmation for a branch that is not operationally active.
  - RN-002 blocks a duplicate confirmed slot for the same patient and now also blocks
    confirmation once the tenant's daily branch capacity is reached (partial remediation,
    MVP-MOD-004-BE-002).
  - capability-package.md optional_capabilities already lists BCM-ORG-007 as the
    intended future capacity source.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - bcm-att-001-appointment-scheduling/business-model.md (AppointmentSlot capacity
    check)
  - bcm-att-001-appointment-scheduling/business-rules.md (new capacity-limit rule)
  - future BCM-ORG-007 Schedule Management business-model.md (BranchSchedule capacity
    source)
remediation:
  strategy: gradual_when_bcm_org_007_schedule_management_is_modeled
  recommended_trigger:
  - BCM-ORG-007 Schedule Management capability package modeling (MVP2)
  acceptance_criteria:
  - bcm-att-001-appointment-scheduling business-rules.md adds a capacity validation
    rule sourced from BCM-ORG-007 BranchSchedule without breaking the existing operational-status
    and overlap rules.
  - The flat tenant-configurable daily capacity ceiling introduced by MVP-MOD-004-BE-002
    is replaced (or subsumed) by schedule-based, time-of-day-aware capacity sourced
    from BCM-ORG-007 BranchSchedule.
```
