---
id: TD-BE-006
format: markdown_structured_payload
type: technical-debt-item
name: PatientRegistrationService.commit() orchestration is not wrapped in a database
  transaction
version: 1.0.0
status: open
---

# Patientregistrationservice.Commit() Orchestration Is Not Wrapped In A Database Transaction

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-006
  type: technical-debt-item
  name: PatientRegistrationService.commit() orchestration is not wrapped in a database
    transaction
  version: 1.0.0
  status: open
  created_date: 2026-07-09
source:
  discovered_during_backlog_item: MVP-MOD-003-BE-002
  module: MVP-MOD-003 People and Clinical Master Data
  evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-002-validation.md
classification:
  category: data_consistency
  affected_area: patient_registration_commit_orchestration
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/patientregistration/application/PatientRegistrationService.java
  risk_level: medium
  blocking: false
  reason_non_blocking: 'PatientRegistrationService#commit calls PatientManagementService
    (patient creation, representative attach, consent recording) and then the registration
    repository across several sequential, non-transactional steps. This is a profile-agnostic
    @Service also exercised against the in-memory adapter profile used by most tests,
    where a PlatformTransactionManager bean is not guaranteed to exist; @Transactional
    in this codebase is used only inside @Profile("local") JDBC repositories (confirmed
    by inspection). Adding @Transactional here without verifying bean availability
    in every active profile risked a NoSuchBeanDefinitionException that could not
    be confirmed without a working local Maven/test run in this session. Failure mid-commit
    is a narrow window (patient creation, then representative attach, then consent
    capture) and is recoverable: a partially-committed registration stays OUTCOME_PENDING
    and can be retried with resolvedExistingPatientId pointing at the already-created
    patient.

    '
current_state:
  issue: 'If patient creation succeeds but a later step (representative attach or
    consent recording) throws, the created Patient is not rolled back, while the PatientRegistrationRequest
    remains OUTCOME_PENDING (its own save happens only after all steps succeed). This
    can leave an orphaned but valid Patient record until the registration is retried
    or investigated.

    '
  compensating_control:
  - The registration record itself is only marked OUTCOME_COMMITTED after every step
    succeeds, so the inconsistency is visible (pending registration + an extra patient)
    rather than silently reported as fully committed.
  - Retrying commit with resolvedExistingPatientId set to the orphaned patient's id
    completes the registration without creating a duplicate patient.
target_state:
  preferred_open_source_tooling:
  - Spring's declarative @Transactional, backed by a PlatformTransactionManager bean
    registered for every active profile (including the in-memory/default test profile),
    e.g. a no-op or ResourcelessTransactionManager for non-JDBC profiles.
  expected_integration_points:
  - PatientRegistrationService#commit
  - Spring Boot test configuration for the default (non-"local") profile
remediation:
  strategy: gradual_when_backend_transaction_infrastructure_is_next_touched
  recommended_trigger:
  - a future backlog item that adds a PlatformTransactionManager bean available across
    all active profiles
  - a production incident or QA finding involving an orphaned Patient record from
    a failed commit
  acceptance_criteria:
  - PatientRegistrationService#commit is wrapped in a transaction boundary that rolls
    back patient creation, representative attach and consent recording together on
    any failure.
  - The full backend test suite (including the default in-memory profile) continues
    to pass with the transaction manager in place.
```
