# HOP Customer Acceptance and Commercial Handoff

## Overview

This document defines the customer acceptance criteria and the commercial handoff protocol for moving a HOP customer from sales through implementation, hypercare, and business-as-usual operations.

## Customer Acceptance Criteria

### Operational Acceptance

- [ ] Customer can configure tenant, laboratory, and branches
- [ ] Customer can manage diagnostic catalog (services, tests, panels, prices)
- [ ] Customer can register patients and doctors
- [ ] Customer can create and process diagnostic orders end to end
- [ ] Customer can collect samples, process results, and release reports
- [ ] Customer can operate cash sessions, payments, and billing requests

### Digital Channel Acceptance

- [ ] Patients can access results through patient portal and mobile app
- [ ] Doctors can access referred patient results through doctor portal
- [ ] Public website displays published catalog and accepts requests

### Quality and Compliance Acceptance

- [ ] Internal quality controls can be recorded and reviewed
- [ ] External QC, CAPA, and audit management functional (Enterprise tier)
- [ ] Audit trail is searchable and complete

### Integration and Data Acceptance

- [ ] Legacy data migrated with reconciliation report signed off
- [ ] Integration endpoints configured and tested
- [ ] API keys and rate limits configured

### Operations and Support Acceptance

- [ ] Customer administrators trained on employee portal
- [ ] Key operational roles completed role-specific training
- [ ] Support escalation path documented and communicated

## Commercial Handoff Protocol

### Phase 1: Sales to Implementation

**Trigger:** Contract signed
**From:** Sales Lead → Professional Services Lead

**Deliverables:**
- Signed contract with package tier
- Customer profile
- Pilot/production designation
- Target go-live date

### Phase 2: Implementation to Hypercare

**Trigger:** Customer acceptance criteria met
**From:** Professional Services Lead → L2 Support Lead

**Deliverables:**
- Tenant Onboarding Report
- Data Migration Reconciliation Report
- Training Completion Records
- Customer Acceptance Signoff
- Open issues list

### Phase 3: Hypercare to BAU

**Trigger:** 30-day hypercare with zero P1/P2 for 10 consecutive days
**From:** L2 Support Lead → Customer Success Manager

**Deliverables:**
- Hypercare Summary Report
- Customer satisfaction survey
- Formal Handoff Signoff
- Transition to standard SLA

## Pilot to GA Promotion

**Criteria:**
- Onboarding checklist completed
- 500+ diagnostic orders processed
- Structured feedback provided
- Zero unresolved P1 incidents during pilot

**Actions:**
- Convert pilot subscription to production
- Upgrade tenant configuration
- Transition to standard/premium SLA
- Archive pilot metrics for reference case

## Related Documents

- [Launch Readiness Checklist](launch-readiness-checklist.md)
- [Implementation to Ops Handoff](../../../09-operations/governance/implementation-support-ops-handoff.md)
- [Support Model](../../../09-operations/governance/support-model-and-escalation-matrix.md)
- [Onboarding Guides](../../../09-operations/onboarding/README.md)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CUST-ACCEPT-001
  type: customer-acceptance-and-commercial-handoff
  name: HOP Customer Acceptance and Commercial Handoff
  version: 1.0.0
  status: approved
  human_readable: customer-acceptance-and-commercial-handoff.md
  machine_readable: customer-acceptance-and-commercial-handoff.md
  backlog_item: COM-MOD-016-COM-001
  created_date: 2026-07-24
  owner: Nexora Professional Services and Commercial Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
customer_acceptance_criteria:
- category: Operational Acceptance
  criteria:
  - Customer can configure tenant, laboratory, and branches.
  - Customer can manage diagnostic catalog (services, tests, panels, prices).
  - Customer can register patients and doctors.
  - Customer can create and process diagnostic orders end to end.
  - Customer can collect samples, process results, and release reports.
  - Customer can operate cash sessions, payments, and billing requests.
  evidence: Walkthrough with customer using the employee portal.
- category: Digital Channel Acceptance
  criteria:
  - Patients can access released results through patient portal and mobile app.
  - Doctors can access referred patient results through doctor portal.
  - Public website displays published catalog and accepts appointment requests.
  evidence: Customer-validated access from each digital channel.
- category: Quality and Compliance Acceptance
  criteria:
  - Internal quality controls can be recorded and reviewed.
  - External quality controls, CAPA, and audit management workflows are functional
    (Enterprise tier).
  - Audit trail is searchable and complete.
  evidence: Quality manager walkthrough with sample data.
- category: Integration and Data Acceptance
  criteria:
  - Legacy data has been migrated using BCM-PLT-010 with reconciliation report signed
    off.
  - Integration endpoints are configured and tested.
  - API keys and rate limits are configured for partner integrations.
  evidence: Migration reconciliation report and integration test results.
- category: Operations and Support Acceptance
  criteria:
  - Customer administrators are trained on the employee portal.
  - Key operational roles have completed role-specific training.
  - Support escalation path is documented and communicated.
  - Customer knows how to contact L1/L2 support.
  evidence: Training completion records and support contact documentation.
commercial_handoff_protocol:
  phases:
  - phase: 1_Sales_to_Implementation
    trigger: Contract signed and onboarding scheduled.
    owner: Sales Lead to Professional Services Lead.
    deliverables:
    - Signed contract with package tier and add-ons.
    - Customer profile (organization type, branch count, user count, special requirements).
    - Pilot or production designation.
    - Target go-live date.
    handoff_document: Sales to Implementation Handoff Form.
  - phase: 2_Implementation_to_Hypercare
    trigger: Customer acceptance criteria met and signoff obtained.
    owner: Professional Services Lead to L2 Support Lead.
    deliverables:
    - Tenant Onboarding Report (ONB-GUIDE-001 completion evidence).
    - Data Migration Reconciliation Report (ONB-GUIDE-006 completion evidence).
    - Training Completion Records (ONB-GUIDE-007 completion evidence).
    - Customer Acceptance Signoff Document.
    - Open issues and known limitations list.
    handoff_document: Implementation to Hypercare Handoff Document.
    reference: 09-operations/governance/implementation-support-ops-handoff.md.
  - phase: 3_Hypercare_to_BAU
    trigger: 30-day hypercare period completed with zero P1/P2 incidents for 10 consecutive
      days.
    owner: L2 Support Lead to Customer Success Manager.
    deliverables:
    - Hypercare Summary Report (incidents, resolutions, feedback).
    - Customer satisfaction survey results.
    - Formal Handoff Protocol Signoff Document.
    - Transition to standard support SLA.
    handoff_document: Hypercare to BAU Handoff Document.
    reference: 09-operations/governance/implementation-support-ops-handoff.md.
  pilot_to_ga_promotion:
    trigger: Pilot exit criteria met (from pricing-model.md pilot_program section).
    criteria:
    - Customer completed onboarding checklist.
    - Customer processed at least 500 diagnostic orders through the full workflow.
    - Customer provided structured feedback.
    - Zero unresolved P1 incidents during pilot.
    promotion_actions:
    - Convert pilot subscription to production contract.
    - Upgrade tenant configuration from pilot to production settings.
    - Transition from pilot support to standard or premium SLA.
    - Archive pilot metrics for reference case development.
escalation_path:
  commercial_escalation:
  - level: Account Manager
    scope: Contract, billing, and package change requests.
  - level: Sales Director
    scope: Pricing disputes, custom terms, and renewal negotiations.
  - level: VP Commercial
    scope: Strategic account decisions and executive-level escalations.
  technical_escalation:
    reference: 09-operations/governance/support-model-and-escalation-matrix.md.
open_source_first: true
agent_agnostic: true
no_proprietary_agent_dependencies: true
```
