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
