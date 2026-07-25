---
id: TD-BE-007
format: markdown_structured_payload
type: technical-debt-item
name: Professional credential expiration is not proactively transitioned by a scheduler
  and does not flag doctors for re-verification
version: 1.0.0
status: open
---

# Professional Credential Expiration Is Not Proactively Transitioned By A Scheduler And Does Not Flag Doctors For Re Verification

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-007
  type: technical-debt-item
  name: Professional credential expiration is not proactively transitioned by a scheduler
    and does not flag doctors for re-verification
  version: 1.0.0
  status: open
  created_date: 2026-07-14
source:
  discovered_during_backlog_item: MVP-MOD-003-QA-001
  module: MVP-MOD-003 People and Clinical Master Data
  evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
classification:
  category: business_rule_completeness
  affected_area: doctor_management_credential_lifecycle
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/doctormanagement/
  risk_level: medium
  blocking: false
  reason_non_blocking: 'Doctor referring-eligibility (RN-006, isEligibleAsReferringDoctor)
    is computed live and already excludes credentials whose expiresAt has passed,
    so no incorrect eligibility decision results from the missing scheduler today.
    The gap is limited to (a) ProfessionalCredential.STATUS_EXPIRED never being proactively
    assigned by a background job, and (b) no explicit "flag doctor for re-verification"
    signal being raised when a credential lapses.

    '
current_state:
  issue: 'bcm-per-003-doctor-management/business-rules.md RN-005 states "A credential
    must transition to expired when its expiresAt date has passed and must trigger
    a doctor flag for re-verification" with enforcement_point scheduler:credential_expiration_watcher.
    No @Scheduled job, watcher class or equivalent exists in the backend. verifyDoctorCredential
    only reactively rejects verifying a credential that is already expired at verification
    time; no code path ever assigns ProfessionalCredential.STATUS_EXPIRED and no re-verification
    flag/notification is raised. MVP-MOD-003-BE-002-validation.md''s operations_still_deferred
    and model_gaps_identified lists did not previously call this out; it was identified
    during MVP-MOD-003-QA-001 review of the actual backend source against the modeled
    rule.

    '
  compensating_control:
  - DoctorManagementService#isEligibleAsReferringDoctor / DoctorDirectory#isEligibleAsReferringDoctor
    recompute credential expiry against expiresAt at query time, so referring-eligibility
    decisions are correct in real time regardless of the stored status field.
  - verifyDoctorCredential rejects verification of an already-expired credential with
    PeopleConflictException.
target_state:
  preferred_open_source_tooling:
  - Spring's built-in @Scheduled / TaskScheduler (already an available dependency;
    no new third-party package required).
  expected_integration_points:
  - A scheduled job in the doctormanagement package that scans credentials nearing
    or past expiresAt, transitions ProfessionalCredential.status to EXPIRED, and raises
    a DoctorCredentialExpired (or similar) audited event that can drive a re-verification
    notification/worklist.
remediation:
  strategy: gradual_when_doctor_credential_lifecycle_is_next_touched
  recommended_trigger:
  - MVP-MOD-003-CLOSEOUT or a future doctor-management backlog item
  - Any change to ProfessionalCredential lifecycle handling
  acceptance_criteria:
  - A scheduled or event-driven mechanism transitions expired credentials to STATUS_EXPIRED
    without requiring a verification attempt.
  - An audited signal is raised so doctors/administrators can be notified of the need
    for re-verification.
  - RN-005's test_refs (TST-DOC-003-06) cover the proactive transition, not only the
    reactive verify-time rejection.
```
