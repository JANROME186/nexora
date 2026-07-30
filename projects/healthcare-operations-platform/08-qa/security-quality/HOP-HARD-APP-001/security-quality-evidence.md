---
id: HOP-HARD-APP-001-security-quality-evidence
type: security-quality-evidence
status: validated
backlog_item: HOP-HARD-APP-001
---

# HOP-HARD-APP-001 Security Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-APP-001-security-quality-evidence
  type: security-quality-evidence
  status: validated
  backlog_item: HOP-HARD-APP-001
  module_id: HOP-FINAL-HARDENING
tools:
  backend_maven_quality_profile:
    command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify checkstyle:checkstyle
      pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check
    status: passed
    tests_run: 580
    failures: 0
    coverage_line_percent: 84.86
  backend_owasp_dependency_check:
    command: mvn -Pquality org.owasp:dependency-check-maven:check
    status: passed
    dependencies_scanned: 72
    vulnerabilities: 0
    database_path: C:\Documents\Proyectos\Laboratorio\NEXORA\dependency-check-data
    database_freshness_note: Used the local advisory database available at scan
      time (2026-07-30), per the runbook's shared-database policy; not refreshed
      by this backlog item.
  patient_portal_npm_quality:
    command: npm run quality
    status: passed
    tests_run: 20
    coverage_line_percent: 94.42
  doctor_portal_npm_quality:
    command: npm run quality
    status: passed
    tests_run: 33
    coverage_line_percent: 96.55
  mobile_app_npm_quality:
    command: npm run quality
    status: passed
    tests_run: 40
    coverage_line_percent: 99.21
  npm_audit_remediation:
    portals: [patient-portal, doctor-portal, mobile-app]
    before: 1 high-severity finding each (brace-expansion, transitive devDependency,
      GHSA-mh99-v99m-4gvg, DoS via unbounded expansion length)
    remediation: npm audit fix (non-breaking devDependency patch bump; package-lock.json
      updated only)
    after: 0 vulnerabilities in all 3
  trivy:
    command: trivy fs --scanners vuln,secret,misconfig --exit-code 0 --no-progress
      --skip-dirs "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules,patient-portal/node_modules,patient-portal/dist,doctor-portal/node_modules,doctor-portal/dist,public-website/node_modules"
      .
    working_directory: 07-implementation
    status: passed
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
    targets_scanned:
    - backend/pom.xml
    - doctor-portal/package-lock.json
    - employee-portal/package-lock.json
    - patient-portal/package-lock.json
    - public-website/package-lock.json
  checkstyle:
    status: passed_report_only_pre_existing_baseline
    finding_summary: 103 pre-existing violations repo-wide (98 recorded at HOP-HARD-BE-001,
      grown by intervening backlog items' new files); 0 new violations attributable
      to this item's changes, confirmed by an explicit per-touched-file >140-character
      line scan.
  pmd:
    status: passed_report_only_pre_existing_baseline_plus_1_consistent_new_instance
    finding_summary: 1 new MissingSerialVersionUID instance on the new
      ImagingAccessDeniedException, matching the identical pre-existing pattern already
      present on its 4 sibling exception classes (ImagingDomainException,
      ImagingNotFoundException, ResultHistoryAccessDeniedException, and others; 50
      total repo-wide) -- a consistent instance of existing debt, not a new category.
      HopAuthorizationInterceptor.preHandle's pre-existing complexity findings grew
      quantitatively (2 new self-access branches) but remain the same rule categories
      that method already violated.
  spotbugs:
    status: passed_0_new_findings
    finding_summary: 0 findings in any file touched by this item.
  license_checker:
    patient_portal: passed_3_mit_1_unlicensed
    doctor_portal: passed_3_mit_1_unlicensed
  jscpd:
    patient_portal: passed_zero_duplicates
    doctor_portal: passed_zero_duplicates
    mobile_app: passed_zero_duplicates
  prettier:
    patient_portal: passed
    doctor_portal: passed
  git_diff_check:
    status: passed
authorization_hardening:
  intent: COM-MOD-014-PORTAL-001 exposes ImagingStudyDeliveryController (BCM-IMG-008)
    and RadiologySignatureController (BCM-IMG-007) to patient-portal/doctor-portal
    for the first time. Both controllers previously enforced only the generic
    SCREEN_IMAGING_DELIVERY/SCREEN_IMAGING_REPORTS employee permission plus a raw
    X-Tenant-Id header -- no patient/doctor-role-scoped ownership check existed,
    so any authenticated PATIENT/REFERRING_DOCTOR session could in principle read
    another patient's delivery package or radiology report by supplying an arbitrary
    packageId/reportId/studyId.
  new_permission_codes:
  - PORTAL_PATIENT_IMAGING_VIEW (granted to PATIENT)
  - PORTAL_DOCTOR_IMAGING_VIEW (granted to REFERRING_DOCTOR)
  interceptor_enforcement: 'HopAuthorizationInterceptor gained 2 new self-access bypass
    blocks (1 per role) covering GET /api/v1/imaging/delivery-packages[/{id}] and
    GET /api/v1/imaging/reports[/{id}]. The list-by-patientId endpoint is checked
    directly at the interceptor (patientId query param must equal the PATIENT caller''s
    userId, mirroring the existing /api/results/history/patient/ pattern); mutating
    verbs (POST/PUT) are excluded from the bypass, so patients/doctors still cannot
    create, sign or mark-delivered imaging records. Get-by-id and reports endpoints
    cannot be checked at the interceptor (no patientId in the path/query), so those
    are permission-gated at the interceptor and ownership-enforced downstream.'
  service_layer_enforcement: 'ImagingStudyDeliveryService and RadiologySignatureService
    gained callerRoleCode/callerId-aware overloads that load the record first, then
    verify: PATIENT caller''s id equals the record''s (resolved) patientId, or
    REFERRING_DOCTOR caller is confirmed via ReferringDoctorAuthorizationPort.isPatientReferredByDoctor
    -- the same port ResultHistoryService already uses for the identical
    results-history self-access boundary, reused unchanged (both modules already
    declared frontdeskcaredelivery::referring-doctor-authorization-port as an allowed
    Spring Modulith dependency target, so no new cross-module coupling was introduced).
    RadiologySignatureService additionally resolves the report''s owning patient
    through ImagingStudyRepository, since RadiologyReport does not carry patientId
    directly -- reports do not leak an unowned study''s findings/impression text.
    Mismatch throws a new ImagingAccessDeniedException, mapped to HTTP 403 by
    ImagingExceptionHandler.'
  test_coverage: 6 new ImagingOperationsUnitTest cases (patient self-match/mismatch,
    doctor referral confirmed/denied, both for delivery packages and reports) plus
    5 new HopAuthorizationInterceptorTest cases exercise every branch of the new
    authorization logic, not just the wiring.
  frontend_header_convention: patient-portal and doctor-portal's readSessionHeaders()
    gained the raw X-Tenant-Id/X-User-Id headers these imaging endpoints require
    (previously only employee-portal sent them), matching employee-portal's
    established convention exactly; no new header scheme was invented.
security_decision:
  no_new_critical_or_high_dependency_vulnerabilities_detected: true
  trivy_all_severities_clean_across_all_5_scanned_targets: true
  new_patient_doctor_facing_imaging_endpoints_have_real_server_enforced_ownership_checks: true
  no_dom_xss_sink_introduced: true
  residual_static_analysis_debt_blocks_final_project_closure: true
  residual_static_analysis_debt_blocks_this_slice_closure: false
  rationale: HOP-HARD-APP-001 closed COM-MOD-014-PORTAL-001 by adding patient/doctor-facing
    imaging delivery views, and closed a real pre-existing authorization gap along
    the way (BCM-IMG-007/008 had no patient/doctor self-access boundary before this
    item, unlike every other patient/doctor-facing endpoint in the platform). All
    4 touched stacks (backend, patient-portal, doctor-portal, mobile-app) preserved
    or improved their coverage floor, and a pre-existing devDependency vulnerability
    was remediated in all 3 touched frontend stacks. TD-APP-001 and TD-UX-003 remain
    honestly reviewed as unchanged -- no renderer stack was selected, since that is a
    standalone architecture decision outside this slice's safe blast radius.
next_backlog_item: HOP-HARD-WEB-001
```
