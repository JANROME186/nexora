# MVP-MOD-003-BE-002 — Security Quality Evidence

Machine-readable evidence: [security-quality-evidence.md](security-quality-evidence.md)

## Summary

`MVP-MOD-003-BE-002` implements the duplicate-detection and portal-identity custom rules deferred
by `MVP-MOD-003-BE-001` across BCM-PER-001, BCM-PER-002, BCM-PER-003 and BCM-ATT-002. No new
dependency was introduced; identifier hashing uses only JDK-bundled `java.security`/`java.util`
classes. The stack stays on Spring Boot 3.5.14, Spring Modulith 1.4.5 and PostgreSQL JDBC 42.7.12.

## Security review specific to this backlog

- One-way SHA-256 hashing replaces a weak `hashCode()`-based identifier digest for duplicate
  matching.
- Consent revocation is append-only; the original evidence row is never mutated.
- Patient merge never deletes data (soft-merge with a bounded merge-chain lookup).
- Duplicate detection and tenant policy overrides are strictly tenant-scoped.
- The new tenant policy store has no REST surface in this backlog, so it cannot be altered by an
  external caller (flagged as a modeling gap in `FWF-HOP-002`, not a vulnerability).

## Gates executed

| Gate | Result |
|---|---|
| Backend automated tests (`mvn test`) | Passed: 58 tests, 0 failures, 0 errors, 6 skipped |
| Backend database-backed tests | Passed: 58 tests, 0 failures, 0 errors, 0 skipped |
| Spring Modulith module boundary check | Passed |
| OpenAPI contract coverage | Passed |
| Custom-rule functional coverage | Passed |
| Static analysis (compile) | Passed through Maven test execution |
| Trivy fs vuln + secret + misconfig scan | Passed: 0 HIGH/CRITICAL findings |
| Agent-agnostic scan | Passed |
| DAST | Deferred — TD-QA-001 |
| Container or IaC scan | Passed through Trivy filesystem scan |

## Confirmation note

The original delivery reported shell/build unavailability. This follow-up validation executed the
required Maven, YAML and Trivy gates, corrected the issues found, and resolved exception `EX-001`.

## Dependencies

No dependency changes.

## Technical debt

Reuses `TD-QA-001`, `TD-BE-002`, `TD-BE-003`, `TD-BE-004`, `TD-STACK-001`. Newly registered:
`TD-BE-005` (doctor referring-eligibility as a computed query instead of a status-field change) and
`TD-BE-006` (patient registration commit orchestration is not transactionally atomic).

## Result

Security quality gate: **passed**. Ready to continue with `MVP-MOD-003-FE-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-MVP-MOD-003-BE-002-001
  type: security-quality-evidence
  name: MVP-MOD-003-BE-002 Duplicate Detection and Portal Identity Custom Rules Security
    Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-003-BE-002
  module: MVP-MOD-003 People and Clinical Master Data
  release: REL-001
  implementation_root: 07-implementation/
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  objective: Apply open-source-first security and quality gates to the custom-rule
    implementations added for duplicate detection, merge coordination, credential
    lifecycle and patient registration commit orchestration.
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  reviewed_stack:
  - name: Spring Boot
    version: 3.5.14
    license: Apache-2.0
    role: backend application framework (unchanged)
  - name: Spring Modulith
    version: 1.4.5
    license: Apache-2.0
    role: modular backend architecture support (unchanged)
  - name: PostgreSQL JDBC
    version: 42.7.12
    license: BSD-2-Clause
    role: database driver (unchanged)
  - name: JDK java.security.MessageDigest / java.util.HexFormat
    version: JDK-bundled
    license: GPLv2+CE
    role: one-way SHA-256 hashing of national identifiers for duplicate matching (no
      new dependency; JDK standard library)
  - name: Trivy
    version: 0.69.2
    license: Apache-2.0
    role: vulnerability, secret and misconfiguration scan
security_review_specific_to_this_backlog:
- topic: One-way identifier hashing for duplicate detection
  finding: PersonNaturalKey.hashIdentifier() uses SHA-256 (JDK MessageDigest) instead
    of the previous Integer.toHexString(hashCode()) approach, removing a weak/collision-prone
    identifier digest from the natural-key matching path. Hashes are used only for
    equality comparison during duplicate scoring, never reversed or exposed as a claimed
    strong secret-hash (no salting, since this is a matching index, not a credential
    store).
  result: passed
- topic: Consent revocation immutability
  finding: revokePatientConsent inserts a new PatientConsent row rather than mutating
    the original; JdbcPatientRepository.saveConsent() has no ON CONFLICT clause, so
    a mutation attempt would fail loudly rather than silently succeed. Preserves consent
    evidence history for compliance/audit purposes.
  result: passed
- topic: Patient merge never deletes data
  finding: mergePatient sets status=MERGED and mergedIntoPatientId on the source record;
    no DELETE is issued anywhere in the merge path. Historical references remain resolvable
    via the merge chain in findSnapshot (bounded by MAX_MERGE_CHAIN_HOPS=10 to prevent
    an unbounded or cyclic lookup from becoming a denial-of-service vector).
  result: passed
- topic: Tenant isolation in duplicate detection
  finding: PersonDuplicateDetectionEngine, PersonDocumentUniquenessPolicy and TenantPeoplePolicyStore
    all scope every query and every configuration override by tenantId; no cross-tenant
    candidate can be returned or scored.
  result: passed
- topic: No new externally-reachable configuration surface
  finding: TenantPeoplePolicyStore is an in-process component with no REST endpoint
    in this backlog item (see FWF-HOP-002 framework feedback); tenant policy values
    can only be changed by code calling the store directly (e.g. future tests or a
    future config capability), not by an unauthenticated or under-authorized HTTP
    caller.
  result: passed
quality_gates:
- id: SQ-001
  name: Backend automated tests
  command: mvn --settings .mvn/settings.xml test
  working_directory: 07-implementation/backend
  result: passed
  notes: Result 58 tests, 0 failures, 0 errors, 6 skipped. Skipped tests are local
    database tests.
- id: SQ-002
  name: Backend database-backed tests
  command: mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  working_directory: 07-implementation/backend
  result: passed
  notes: Result 58 tests, 0 failures, 0 errors, 0 skipped with hop-local-postgres
    healthy.
- id: SQ-003
  name: Spring Modulith module boundary check
  command: mvn --settings .mvn/settings.xml test "-Dtest=PlatformFoundationModulithTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: PlatformFoundationModulithTest executed in the database-backed Maven test
    run.
- id: SQ-004
  name: OpenAPI contract coverage
  command: mvn --settings .mvn/settings.xml test "-Dtest=PeopleClinicalMasterDataContractTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: PeopleClinicalMasterDataContractTest executed in the database-backed Maven
    test run.
- id: SQ-005
  name: Custom-rule functional coverage
  command: mvn --settings .mvn/settings.xml test "-Dtest=PeopleClinicalMasterDataApiTest,DoctorEligibilityRulesTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: PeopleClinicalMasterDataApiTest and DoctorEligibilityRulesTest executed in
    the database-backed Maven test run.
- id: SQ-006
  name: Static analysis (compiler warnings and Modulith validation)
  command: mvn --settings .mvn/settings.xml compile
  working_directory: 07-implementation/backend
  result: passed
  notes: Compilation and Modulith validation passed as part of Maven test execution.
    Deeper SAST tooling stays tracked in TD-BE-002.
- id: SQ-007
  name: Filesystem vulnerability, secret and misconfiguration scan
  command: trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code
    1 --no-progress .
  working_directory: 07-implementation
  result: passed
  notes: Trivy 0.69.2 reported 0 vulnerabilities for backend/pom.xml and employee-portal/package-lock.json;
    no HIGH/CRITICAL findings.
- id: SQ-008
  name: Agent-agnostic scan
  command: rg scan for named-agent/vendor/runtime references in active BE-002 state
    and evidence files.
  working_directory: 07-implementation/backend
  result: passed
  notes: 0 matches found for named AI agents, assistant vendors or specific AI platform
    runtimes.
- id: SQ-009
  name: DAST
  command: not_executed
  result: deferred_with_technical_debt
  notes: Same as BE-001; TD-QA-001 tracks automated DAST enablement.
- id: SQ-010
  name: Container or IaC scan
  command: trivy fs (same command as SQ-007)
  result: passed
  notes: Same Trivy filesystem scan as SQ-007 completed without HIGH/CRITICAL findings.
dependency_remediation:
  changes_applied: false
  notes: No new dependencies were added. Identifier hashing uses JDK-bundled java.security
    and java.util classes only.
technical_debt:
  registered_reused:
  - TD-QA-001
  - TD-BE-002
  - TD-BE-003
  - TD-BE-004
  - TD-STACK-001
  newly_registered:
  - TD-BE-005
  - TD-BE-006
  blocking: []
exceptions:
- id: EX-001
  gate: SQ-001, SQ-002, SQ-003, SQ-005, SQ-006, SQ-007, SQ-010
  status: resolved
  reason: Original delivery reported shell/build unavailability.
  resolution: 'Follow-up validation executed Maven tests, database-backed tests, YAML
    parsing and Trivy scan; issues found during confirmation were fixed before marking
    this backlog item closed.

    '
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-003-FE-001
  next_required_focus:
  - Continue with automated DAST once TD-QA-001 is scheduled.
  - Consider FWF-HOP-002 (modeled tenant-configurable-parameters surface) in future
    capability package planning.
```
