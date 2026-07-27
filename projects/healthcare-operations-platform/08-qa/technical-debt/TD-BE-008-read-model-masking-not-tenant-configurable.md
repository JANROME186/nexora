---
id: TD-BE-008
format: markdown_structured_payload
type: technical-debt-item
name: PatientSnapshot/DoctorSnapshot document and credential number masking is fixed,
  not tenant-configurable
version: 1.0.0
status: closed
---

# Patientsnapshot/Doctorsnapshot Document And Credential Number Masking Is Fixed, Not Tenant Configurable

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-008
  type: technical-debt-item
  name: PatientSnapshot/DoctorSnapshot document and credential number masking is fixed,
    not tenant-configurable
  version: 1.0.0
  status: closed
  created_date: 2026-07-14
source:
  discovered_during_backlog_item: MVP-MOD-003-QA-001
  module: MVP-MOD-003 People and Clinical Master Data
  evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
classification:
  category: business_rule_completeness
  affected_area: patient_and_doctor_read_model_privacy
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/
  risk_level: medium
  blocking: false
  reason_non_blocking: 'Document numbers are masked in PatientSnapshot and DoctorSnapshot
    today (not shown in the clear), so there is no raw data-exposure regression; the
    gap is that the masking algorithm is a fixed, tenant-agnostic rule rather than
    the tenant-configured policy the rule statement describes, and credential numbers
    are not exposed via DoctorSnapshot at all (so that half of the rule is moot until
    credential numbers are ever added to a read model).

    '
current_state:
  issue: 'bcm-per-002-patient-management/business-rules.md RN-008 and bcm-per-003-doctor-management/business-rules.md
    RN-008 both require "tenant-configured masking" of document/credential numbers
    in read models. PatientSnapshot.from() and DoctorSnapshot.from() both call PersonDocument.maskedNumber(),
    but that method applies one fixed masking algorithm (last-4-visible) documented
    in its own Javadoc as a placeholder pending tenant configuration; TenantPeoplePolicyStore
    has no masking-related setting. Credential numbers are never exposed via DoctorSnapshot,
    so no masking decision applies to them yet in practice.

    '
  compensating_control:
  - Document numbers are never rendered unmasked in either snapshot projection today,
    so the minimum privacy expectation (no raw document number leakage in read models)
    is met.
target_state:
  preferred_open_source_tooling:
  - No new tooling required; extend TenantPeoplePolicyStore with a masking policy
    (visible-character count, mask character) consumed by PersonDocument.maskedNumber().
  expected_integration_points:
  - TenantPeoplePolicyStore
  - PersonDocument.maskedNumber()
  - PatientSnapshot.from() / DoctorSnapshot.from()
remediation:
  strategy: closed_by_HOP_HARD_BE_001
  recommended_trigger:
  - Any backlog item that extends TenantPeoplePolicyStore for another tenant-configurable
    policy
  - A future privacy/compliance-focused backlog item
  acceptance_criteria:
  - Masking visible-character count and mask character are tenant-configurable through
    TenantPeoplePolicyStore with safe defaults matching current behavior.
  - If/when credential numbers are added to a read model, they are masked using the
    same policy.
closure:
  closed_during_backlog_item: HOP-HARD-BE-001
  closure_evidence:
  - 08-qa/qa/final-hardening/HOP-HARD-BE-001-validation.md
  - 08-qa/security-quality/HOP-HARD-BE-001/security-quality-evidence.md
  validation_summary:
  - TenantPeoplePolicyStore now exposes masking policy.
  - Patient and doctor read-model snapshots consume tenant-configurable masking.
  - Backend quality gate passed with 528 tests and 84.62% line coverage.
```
