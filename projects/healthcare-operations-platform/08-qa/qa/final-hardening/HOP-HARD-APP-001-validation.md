---
id: HOP-HARD-APP-001-validation
type: qa-validation-evidence
status: validated
backlog_item: HOP-HARD-APP-001
---

# HOP-HARD-APP-001 Validation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-APP-001-validation
  type: qa-validation-evidence
  status: validated
  backlog_item: HOP-HARD-APP-001
  module_id: HOP-FINAL-HARDENING
summary:
  decision: validated_related_item_closed_both_mapped_items_reviewed_with_no_regression
  implemented:
  - 'COM-MOD-014-PORTAL-001 (closed): patient-portal and doctor-portal each gained a
    read-only "Imaging" tab. patient-portal''s ImagingTab lists the patient''s own
    delivered imaging studies (new imagingDeliveryApi.ts: getMyImagingDeliveryPackages,
    getMyImagingReportsForStudy) and shows the associated signed/draft radiology
    findings/impression inline. doctor-portal''s ImagingTab reuses the existing
    PatientSelector pattern (same as its Results/Notifications tabs) to list a
    referred patient''s delivered studies (new imagingDeliveryApi.ts:
    getPatientImagingDeliveryPackagesAsDoctor). Both clients follow the established
    patientResultHistoryApi.getPatientHistoryAsDoctor convention: a callerRoleCode/callerId
    query-param pair the backend uses to enforce self-access.'
  - 'Backend imaging delivery hardening (new work, not previously tracked as its own
    debt item but squarely inside "imaging delivery hardening" per this item''s
    own title/objective): ImagingStudyDeliveryController (BCM-IMG-008) and
    RadiologySignatureController (BCM-IMG-007) previously had zero patient/doctor-role-scoped
    authorization beyond the generic X-Tenant-Id header -- any authenticated PATIENT
    or REFERRING_DOCTOR session could, in principle, read another patient''s delivery
    package or radiology report by guessing/enumerating an ID. Added: 2 new permission
    codes (PORTAL_PATIENT_IMAGING_VIEW, PORTAL_DOCTOR_IMAGING_VIEW) granted to PATIENT/REFERRING_DOCTOR
    in RolePermissionCatalog; 2 new HopAuthorizationInterceptor self-access bypass
    blocks (list-by-patientId query-param match for PATIENT, permission-gated pass-through
    for REFERRING_DOCTOR, mirroring the existing /api/results/history/patient/ blocks);
    new ImagingStudyDeliveryService/RadiologySignatureService overloads that verify
    real ownership after loading the record (patient self-match on package.patientId(),
    or a ReferringDoctorAuthorizationPort referral check reused unchanged from
    ResultHistoryService -- both modules already declared frontdeskcaredelivery as
    an allowed Spring Modulith dependency); a new ImagingAccessDeniedException (403)
    mirroring ResultHistoryAccessDeniedException.'
  - 'TD-APP-001 (reviewed, materially_reduced_unchanged): mobile TypeScript foundation
    quality gate re-run clean (40 tests, 12 files, 99.21% line coverage, unchanged
    from the documented floor). Corrected a stale figure the debt record and TD-UX-003
    both cited (~569 lines): the foundation has grown to 1,158 non-test lines /
    2,172 total across prior mobile backlog items (COM-MOD-009-APP-001 and others);
    the corrected figure is now recorded in TD-APP-001''s evidence rather than left
    stale. Still not closed: selecting a native renderer stack (React Native/Expo/Flutter)
    is a standalone architecture decision outside a hardening slice''s safe blast
    radius, and remains the trigger this item has always named.'
  - 'TD-UX-003 (reviewed, unchanged, genuinely blocked): re-confirmed it depends on
    TD-APP-001''s renderer selection, which did not happen this iteration for the
    reason above. Risk level and compensating control unchanged (low, non-blocking).'
  audit_dependency_remediation:
  - 'patient-portal, doctor-portal and mobile-app each had 1 high-severity npm audit
    finding (brace-expansion, transitive devDependency, DoS via unbounded expansion).
    Ran `npm audit fix` in all three (non-breaking devDependency patch bump only,
    package-lock.json updated); all three now report 0 vulnerabilities. Re-ran each
    portal''s full `npm run quality` after the fix to confirm no regression.'
technical_debt_result:
  closed:
  - COM-MOD-014-PORTAL-001
  reviewed_unchanged:
  - TD-APP-001
  - TD-UX-003
tests:
  patient_portal_quality:
    command: npm run quality (typecheck && lint && test:coverage && build && duplication
      && format:check && license:check)
    working_directory: 07-implementation/patient-portal
    status: passed
    lint_errors: 0
    lint_warnings: 12
    lint_warnings_note: Identical count and rule categories to the pre-change baseline
      (verified by running eslint against the pre-change git HEAD copy of every
      touched file); the new imagingDeliveryApi.ts, ImagingTab and its tests introduce
      0 new warnings.
    tests_run: 20
    test_files: 6
    failures: 0
    line_coverage_percent: 94.42
    previous_floor_percent: 94.11
    coverage_result: improved_above_floor
    build: passed
    duplication: passed_zero_duplicates
    format_check: passed
    license_check: passed_3_mit_1_unlicensed
    npm_audit: passed_0_vulnerabilities_after_audit_fix
  doctor_portal_quality:
    command: npm run quality
    working_directory: 07-implementation/doctor-portal
    status: passed
    lint_errors: 0
    lint_warnings: 11
    lint_warnings_note: Same verification method as patient-portal. One pre-existing
      warning category (AppContent exceeding the 120-line function threshold) would
      have been newly introduced by the extra imaging case/tab-label entries; extracted
      a standalone TabContent component (same hook/JSX-extraction pattern documented
      in the HOP-HARD-FE-001 handoff) to keep AppContent under the threshold, so the
      net new-warning count is 0.
    tests_run: 33
    test_files: 10
    failures: 0
    line_coverage_percent: 96.55
    previous_floor_percent: 96.28
    coverage_result: improved_above_floor
    build: passed
    duplication: passed_zero_duplicates
    format_check: passed
    license_check: passed_3_mit_1_unlicensed
    npm_audit: passed_0_vulnerabilities_after_audit_fix
  mobile_app_quality:
    command: npm run quality (typecheck && lint && test:coverage && duplication &&
      format:check)
    working_directory: 07-implementation/mobile-app
    status: passed
    tests_run: 40
    test_files: 12
    failures: 0
    line_coverage_percent: 99.21
    previous_floor_percent: 99.21
    coverage_result: unchanged_no_regression
    npm_audit: passed_0_vulnerabilities_after_audit_fix
  backend:
    command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify checkstyle:checkstyle
      pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check
    working_directory: 07-implementation/backend
    status: passed
    tests_run: 580
    failures: 0
    errors: 0
    skipped: 0
    line_coverage_percent: 84.86
    previous_floor_percent: 84.77
    coverage_result: improved_above_floor
    checkstyle: passed_report_only_103_pre_existing_violations_0_new
    checkstyle_note: 103 pre-existing violations (98 at HOP-HARD-BE-001, grown with
      intervening backlog items' new files), all report-only (checkstyle:checkstyle,
      not checkstyle:check) and non-blocking per established policy. Verified with
      a per-file length scan that the 2 touched imaging service files' only
      >140-character lines are the pre-existing, unmodified single-statement
      orElseThrow() calls this item did not edit; every new method/class added by
      this item is within the 140-character limit.
    pmd: passed_report_only_pre_existing_baseline
    pmd_note: New findings limited to (1) 1 MissingSerialVersionUID on the new
      ImagingAccessDeniedException, consistent with all 5 sibling exception classes
      in the codebase (ImagingDomainException, ImagingNotFoundException,
      ResultHistoryAccessDeniedException, etc.) that already carry the same
      pre-existing debt pattern -- not a new category; (2) HopAuthorizationInterceptor.preHandle's
      already-flagged CognitiveComplexity/CyclomaticComplexity/NPathComplexity/AvoidDeeplyNestedIfStmts
      findings grew quantitatively (2 more self-access bypass branches added to an
      already-large, already-branchy method) but are the same pre-existing rule
      categories that method already violated before this item.
    spotbugs: passed_0_new_findings
    spotbugs_note: 0 SpotBugs findings in any file touched by this item (1 unrelated
      pre-existing finding elsewhere in the repository).
    duplicate_finder: passed
  backend_dependency_scan:
    command: mvn -Pquality org.owasp:dependency-check-maven:check
    status: passed
    dependencies_scanned: 72
    vulnerabilities: 0
  new_or_extended_backend_tests:
  - "RolePermissionCatalogTest - updated referringDoctorHoldsItsExpectedDoctorPortalScreens
    to include the new PORTAL_DOCTOR_IMAGING_VIEW grant"
  - "ImagingOperationsUnitTest - constructors updated for the 2 new service dependencies
    (ReferringDoctorAuthorizationPort, ImagingStudyRepository); 6 new tests:
    patientMayViewTheirOwnDeliveryPackageAndReport,
    patientCannotViewAnotherPatientsDeliveryPackageOrList,
    referringDoctorWithConfirmedReferralMayViewDeliveryPackage,
    referringDoctorWithoutReferralCannotViewDeliveryPackage,
    patientMayViewReportForTheirOwnStudyButNotAnothersStudy,
    referringDoctorReferralCheckAppliesToReportAccess; testExceptionHandlerHandling
    extended for the new ImagingAccessDeniedException/403 mapping"
  - "HopAuthorizationInterceptorTest - 5 new tests covering the PATIENT list/get-by-id/mutation-denied
    paths and the REFERRING_DOCTOR reports-permission path for the 2 new imaging
    endpoint bypass blocks"
  new_or_extended_frontend_tests:
  - "patient-portal httpClient.test.ts - 2 new tests for imagingDeliveryApi's self-access
    query-param construction"
  - "patient-portal App.test.tsx - Imaging tab added to the full-navigation test
    (findings/impression rendering, correct patientId/studyId args passed through)"
  - "doctor-portal imagingDeliveryApi.test.ts (new file) - 1 test for the referring-doctor
    self-access query-param construction"
  - "doctor-portal App.test.tsx - Imaging tab added to the full-navigation test (patient-selector
    flow, correct doctorId arg) plus a new 403/permission-denied scenario test"
  regression_check:
  - Ran each of the 4 touched stacks' full test suite before and after every change;
    0 regressions in any pre-existing test.
security_quality_evidence: 08-qa/security-quality/HOP-HARD-APP-001/security-quality-evidence.md
next_backlog_item: HOP-HARD-WEB-001
```
