# CAP-001 Patient Management - Capability Definition

## Purpose

Enable laboratories, imaging centers and diagnostic organizations to maintain a reliable, secure, auditable and reusable patient record across all channels and operational processes.

## Business goals

- Provide a single patient identity across laboratories, branches, portals and integrations.
- Avoid duplicate patient records through deterministic and assisted matching.
- Support patient onboarding from reception, web, mobile, doctor referral and public APIs.
- Capture consent, communication preferences and required legal/clinical information.
- Support minors, guardians and responsible parties.
- Enable patient history for orders, results, invoices, appointments and interactions.
- Provide secure patient portal access with privacy controls.
- Support multi-tenant isolation and branch-level access rules.

## Scope included in MVP 1

- Register patient.
- Search patient.
- View patient summary.
- Update demographic information.
- Capture contact information.
- Capture emergency contact.
- Capture guardian/responsible party for minors.
- Capture basic clinical profile.
- Capture consent metadata.
- Detect possible duplicate patients.
- Activate/deactivate patient record.
- Audit patient changes.
- Expose patient API contract.
- Provide basic web and mobile patient flows.

## Scope excluded from MVP 1

- Advanced MPI integration.
- National identity validation per country.
- Insurance eligibility verification.
- Biometric identity verification.
- Advanced patient loyalty program.
- Full CRM segmentation.
- Patient family account management beyond basic guardianship.

## Primary actors

- Receptionist.
- Branch supervisor.
- Laboratory administrator.
- Patient.
- Doctor.
- Call center agent.
- Integration client.
- AI assistant under human supervision.

## Key dependencies

- CAP-002 Identity & Access Management.
- CAP-003 Organization & Branch Management.
- CAP-004 Catalog Management.
- CAP-005 Order Management.
- CAP-006 Results Management.
- CAP-007 Notification Management.
- CAP-008 Billing Management.

## Capability maturity levels

| Level | Description |
|---|---|
| L1 Basic | Manual patient registration and search. |
| L2 Operational | Duplicate detection, consent tracking and audit. |
| L3 Omnichannel | Web, portal, mobile and API registration. |
| L4 Intelligent | AI-assisted intake, deduplication and summarization. |
| L5 Ecosystem | Cross-organization identity, integrations and marketplace extensions. |
