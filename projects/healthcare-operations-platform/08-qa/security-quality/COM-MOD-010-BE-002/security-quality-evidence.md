# COM-MOD-010-BE-002 Security Quality Evidence

Status: passed.

Security and quality gates completed for the backend QLT compilation:

- `mvn -q test`: 312 tests, 0 failures, 0 errors, 16 skipped.
- `mvn -q verify`: passed; JaCoCo line coverage stayed at 82.94%.
- `mvn -q -Pquality org.owasp:dependency-check-maven:check`: passed; 65 dependencies and 0 vulnerabilities.
- `trivy fs --scanners vuln,misconfig ...`: passed; 0 vulnerabilities.
- Secrets pattern scan: no hardcoded secrets.
- Agent-agnostic scan: no named-agent dependency.

Dependency-Check used the local advisory database available on July 20, 2026 at 13:56:20 -06:00.
Daily refresh of that database remains an operator/manual responsibility.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SEC-COM-MOD-010-BE-002
  type: security-quality-evidence
  name: COM-MOD-010-BE-002 Equipment, Maintenance and Internal QC Security Quality
    Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-20
  owner: Nexora Backend Engineering Team
scope:
  backlog_item: COM-MOD-010-BE-002
  module: COM-MOD-010 Inventory and Internal Quality
  release: REL-002
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/backend
checks:
  tests:
    status: passed
    command: mvn -q test
    tests_run: 312
    failures: 0
    errors: 0
    skipped: 16
    coverage_line_percent: 82.94
    coverage_previous_baseline_percent: 82.94
    coverage_regression: false
  sast_or_static_analysis:
    status: passed
    command: mvn -q verify
    tools:
    - JaCoCo
    - Surefire
    - Checkstyle
    - PMD
    - CPD
    - SpotBugs
    - Find Security Bugs
    - Maven Enforcer
    - Duplicate Finder
    - CycloneDX SBOM
    notes: The backend quality profile completed successfully after adding the QLT
      domain, application, web and persistence packages.
  dependency_vulnerability_scan:
    status: passed
    tools:
    - OWASP Dependency-Check 12.1.3
    - Trivy 0.72.0
    dependency_check_command: mvn -q -Pquality org.owasp:dependency-check-maven:check
    dependency_check_dependencies_scanned: 65
    dependency_check_vulnerabilities: 0
    dependency_check_local_nvd_last_checked: 2026-07-20 13:56:20-06:00
    dependency_check_database_refresh_policy: manual_daily_refresh_outside_agent_responsibility
    trivy_command: trivy fs --scanners vuln,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --exit-code 1 --skip-dirs target .
    trivy_vulnerabilities: 0
    trivy_misconfigurations: 0
  secrets_scan:
    status: passed
    command: rg -n '(api[_-]?key|secret|password|token)\s*[:=]\s*[''\"]?[A-Za-z0-9_\-]{16,}'
      projects/healthcare-operations-platform nexora-framework
    findings: 0
    note: A lexical variable named token was reviewed and is not a hardcoded secret.
  dast:
    status: scheduled_for_COM-MOD-010-QA-001
    rationale: This backlog added backend JSON endpoints. Integrated DAST is scheduled
      for the module QA item after the frontend workflows are compiled.
  container_or_iac_scan:
    status: not_applicable
    rationale: No Docker, Terraform, Compose or infrastructure asset changed.
closure:
  status: passed
  next_backlog_item: COM-MOD-010-FE-001
```
