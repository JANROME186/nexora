---
id: TD-BE-002
format: markdown_structured_payload
type: technical-debt-item
name: Configure backend Java/Maven static analysis and SAST toolchain
version: 1.0.0
status: materially_reduced
---

# Configure Backend Java/Maven Static Analysis And Sast Toolchain

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-002
  type: technical-debt-item
  name: Configure backend Java/Maven static analysis and SAST toolchain
  version: 1.0.0
  status: materially_reduced
  created_date: 2026-07-09
source:
  discovered_during_backlog_item: MVP-MOD-002-CLOSEOUT
  module: MVP-MOD-002 Diagnostic Catalog
  evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-CLOSEOUT.md
classification:
  category: quality_tooling
  affected_area: backend_static_analysis_and_sast
  affected_components:
  - 07-implementation/backend
  risk_level: medium
  blocking: false
  reason_non_blocking: 'Trivy dependency, secret and misconfiguration scanning passed
    with 0 HIGH/CRITICAL findings and all backend tests pass. Deep static analysis
    is a defense-in-depth improvement, not a blocker for MVP module closeout.

    '
current_state:
  issue: 'The backend Maven quality profile now runs SpotBugs, Find Security Bugs,
    PMD, PMD CPD, Checkstyle and Spotless. SpotBugs, Checkstyle and CPD pass with
    0 findings. PMD still reports maintainability findings that must be remediated
    gradually as backend code is touched. Semgrep CE remains a future CI defense-in-depth
    addition.

    '
  compensating_control:
  - Trivy filesystem scan (vuln, secret, misconfig) passing across all severities.
  - Spring Modulith structure verification test enforcing module boundaries.
  - ArchUnit is available transitively through spring-modulith-starter-test.
target_state:
  preferred_open_source_tooling:
  - SpotBugs bytecode bug analysis.
  - Find Security Bugs SpotBugs plugin for Java security patterns.
  - PMD bad-practice, dead-code and complexity analysis.
  - PMD CPD copy-paste detection for module or release closeout.
  - Checkstyle team conventions once style rules are agreed.
  - Semgrep CE Java rules for CI defense in depth.
  expected_integration_points:
  - backend Maven verify phase or dedicated quality profile
  - local quality gate script
  - CI security quality workflow
remediation:
  strategy: gradual_when_backend_code_or_quality_toolchain_is_touched
  owner: backend_platform_team
  target_backlog: next_backend_code_changing_backlog_item_or_release_readiness_gate
  priority: P1
  recommended_trigger:
  - next backend code-changing backlog item that touches shared backend infrastructure
  - backend quality hardening iteration
  - CI pipeline implementation
  - release readiness gate
  acceptance_criteria:
  - SpotBugs + Find Security Bugs run in the Maven build and export findings.
  - PMD and PMD CPD run at least during module or release closeout.
  - Checkstyle rules are defined and enforced before commercial GA.
  - All SAST findings receive a disposition; vulnerabilities of any severity require
    remediation or an accepted-risk debt item.
  latest_evidence:
    backlog_item: HOP-QA-ALIGN-002
    evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-002-validation.md
    status: materially_reduced
    residual_findings:
    - PMD reports 124 findings.
    - Semgrep CE is not yet configured.
```
