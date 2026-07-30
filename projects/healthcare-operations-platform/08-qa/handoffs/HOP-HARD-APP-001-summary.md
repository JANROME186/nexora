---
id: HOP-HARD-APP-001-summary
type: backlog-handoff
status: closed
backlog_item: HOP-HARD-APP-001
---

# HOP-HARD-APP-001 Summary

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-APP-001-summary
  type: backlog-handoff
  status: closed
  backlog_item: HOP-HARD-APP-001
  module_id: HOP-FINAL-HARDENING
summary:
  closed_scope:
  - 'COM-MOD-014-PORTAL-001 -- Imaging study delivery views: patient-portal and
    doctor-portal each gained a read-only "Imaging" tab. patient-portal shows the
    patient''s own delivered studies plus the associated signed/draft radiology
    findings/impression inline (new imagingDeliveryApi.ts); doctor-portal reuses
    its existing PatientSelector pattern to list a referred patient''s delivered
    studies. Both follow the same callerRoleCode/callerId self-access query-param
    convention already established by patientResultHistoryApi.getPatientHistoryAsDoctor.'
  materially_reduced_or_reviewed:
  - 'TD-APP-001 -- mobile quality baseline: re-verified (40 tests, 99.21% coverage,
    unchanged). Corrected the stale ~569-line figure both this item and TD-UX-003
    cited -- actual current size is 1,158 non-test lines / 2,172 total. Still not
    closed: native renderer stack selection remains out of a hardening slice''s
    safe blast radius.'
  - 'TD-UX-003 -- mobile layout system: reviewed, remains genuinely blocked on
    TD-APP-001''s (unselected) renderer stack. No status change.'
  backend_hardening_found_and_fixed:
  - 'Found a real, pre-existing authorization gap: ImagingStudyDeliveryController
    (BCM-IMG-008) and RadiologySignatureController (BCM-IMG-007) had zero
    patient/doctor-role-scoped authorization -- only the generic employee
    SCREEN_IMAGING_DELIVERY/SCREEN_IMAGING_REPORTS permission plus a raw X-Tenant-Id
    header. This was a latent risk the moment either endpoint became reachable from
    a patient/doctor-facing UI, which is exactly what this item did. Fixed it before
    wiring the new UI to it: 2 new permission codes (PORTAL_PATIENT_IMAGING_VIEW,
    PORTAL_DOCTOR_IMAGING_VIEW), 2 new HopAuthorizationInterceptor self-access bypass
    blocks (GET-only; mutating verbs remain employee-only), and new
    ImagingStudyDeliveryService/RadiologySignatureService overloads that verify real
    ownership downstream (patient self-match, or a doctor referral check via the
    already-available ReferringDoctorAuthorizationPort -- reused unchanged from
    ResultHistoryService, no new cross-module coupling). A new ImagingAccessDeniedException
    maps mismatches to HTTP 403, mirroring ResultHistoryAccessDeniedException.'
  dependency_remediation:
  - 'patient-portal, doctor-portal and mobile-app each had 1 pre-existing high-severity
    npm audit finding (brace-expansion, transitive devDependency). Ran `npm audit
    fix` in all 3 (non-breaking patch bump); all now report 0 vulnerabilities.'
validation:
  qa_evidence: 08-qa/qa/final-hardening/HOP-HARD-APP-001-validation.md
  security_quality_evidence: 08-qa/security-quality/HOP-HARD-APP-001/security-quality-evidence.md
  backend_gate:
    command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify (plus checkstyle/pmd/spotbugs/dependency-check)
    status: passed
    tests_run: 580
    line_coverage_percent: 84.86
  patient_portal_gate:
    command: npm run quality (patient-portal)
    status: passed
    tests_run: 20
    line_coverage_percent: 94.42
  doctor_portal_gate:
    command: npm run quality (doctor-portal)
    status: passed
    tests_run: 33
    line_coverage_percent: 96.55
  mobile_app_gate:
    command: npm run quality (mobile-app)
    status: passed
    tests_run: 40
    line_coverage_percent: 99.21
  trivy_integrated_filesystem:
    status: passed_zero_findings
    targets: [backend, patient-portal, doctor-portal, employee-portal, public-website]
technical_debt:
  closed:
  - COM-MOD-014-PORTAL-001
  reviewed_unchanged:
  - TD-APP-001
  - TD-UX-003
notable_findings_for_next_owner:
- 'The imaging backend (BCM-IMG-001..008) is fully implemented and was already
  consumed by employee-portal (COM-MOD-014-FE-001); this item was the first to
  expose it to patient/doctor-facing UI, which is why the authorization gap
  described above had never been exercised before. Any future backlog item adding
  more patient/doctor-facing endpoints against an existing employee-only controller
  should check for the same class of gap before wiring new UI to it.'
- 'employee-portal''s existing imagingOperationsApi.ts (COM-MOD-014-FE-001) has
  TypeScript interfaces (RadiologyReport, ImagingDeliveryPackage) whose field names
  do not match the actual backend record field names (e.g. "id"/"signed"/"status"/"deliveredBy"
  vs. the real "reportId"/"reportStatus"/"deliveryStatus"/"updatedBy"). This is a
  pre-existing latent bug in employee-portal, out of scope for this item (employee-portal
  was closed under HOP-HARD-FE-001 and is not part of this item''s surface). The new
  patient-portal/doctor-portal imagingDeliveryApi.ts clients in this item use the
  correct field names -- do not copy employee-portal''s interfaces as a template if
  employee-portal''s imaging screens are ever revisited.'
- 'COM-MOD-014.md''s module record already said `status: module_closed` even though
  COM-MOD-014-PORTAL-001 (one of its own backlog_items) had no status field (i.e.
  was actually still open) -- a pre-existing doc inconsistency. Closing
  COM-MOD-014-PORTAL-001 in this item makes that module_closed status actually
  true/consistent for the first time.'
- 'patient-portal''s permissions.ts/SCREEN_TO_PERMISSION model was dead code before
  this item -- App.tsx rendered all 5 tabs unconditionally with no permission
  filtering, unlike doctor-portal''s already-correct dynamic nav. This item added
  the new "imaging" screen/permission to that model file for consistency but did
  NOT retrofit permission-based nav filtering into patient-portal''s AppContent --
  doing so would change existing tab-visibility behavior for the REFERRING_DOCTOR
  mock login (which patient-portal''s own ROLE_PERMISSION_CATALOG scopes to only 2
  of the 5 tabs), a behavior change beyond this item''s scope. A future UX/IAM
  hardening item should decide whether to wire it in.'
next:
  backlog_item: HOP-HARD-WEB-001
  focus: Public marketplace discovery surface and website hardening.
```
