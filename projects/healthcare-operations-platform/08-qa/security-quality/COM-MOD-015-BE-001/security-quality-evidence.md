---
artifact:
  id: COM-MOD-015-BE-001-SQ
  type: security-quality-evidence
  status: passed_with_documented_environment_limitations
  backlog_item: COM-MOD-015-BE-001
---

# COM-MOD-015-BE-001 Security Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-015-BE-001-SQ
  type: security-quality-evidence
  status: validated
  backlog_item: COM-MOD-015-BE-001
backlog_item: COM-MOD-015-BE-001
module: COM-MOD-015 AI Overlay
status: validated
checks:
  sast_static_analysis:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Pquality spotbugs:spotbugs pmd:pmd pmd:cpd checkstyle:checkstyle cyclonedx:makeAggregateBom org.owasp:dependency-check-maven:check
    result: passed
    notes:
    - PMD findings attributable to new AI overlay code were corrected.
    - CPD duplications: 0.
    - SpotBugs/FindSecBugs AI overlay bugs: 0 after fixing improper Unicode handling.
    - Checkstyle reports 98 pre-existing repo-wide style findings; no blocking Maven failure because quality.failOnViolation is false.
  dependency_vulnerability_scan:
    tool: OWASP Dependency-Check 12.1.3
    result: passed
    vulnerabilities: 0
    report_json: 07-implementation/backend/target/dependency-check-report.json
  sbom:
    tool: CycloneDX Maven
    components: 110
    reports:
    - 07-implementation/backend/target/bom.json
    - 07-implementation/backend/target/bom.xml
  trivy:
    version: 0.72.0
    source_scan: 08-qa/security-quality/COM-MOD-015-BE-001/trivy-ai-overlay-source.json
    pom_scan: 08-qa/security-quality/COM-MOD-015-BE-001/trivy-backend-pom.json
    result: passed_offline_scope
    findings: 0
    note: Full backend Trivy scan attempted but exceeded time due secret scan over generated artifacts; scoped offline source and pom scans completed with local DB and no findings. Normal DB refresh was blocked by restricted network.
  semgrep:
    result: unavailable
    detail: semgrep executable is not installed in the local CLI environment.
  docker:
    result: blocked_by_host_permission
    detail: docker ps could not access Windows Docker API npipe. Local PostgreSQL profile tests passed as database evidence.
security_controls:
- Provider-neutral port isolates model runtime.
- Deterministic local adapter avoids API-key or token-billed dependency.
- Safety policy blocks autonomous clinical validation and skip-human-review prompts.
- All outputs carry policy version, source context, citations, confidence band and reviewer/audit state.
- Endpoint permission registry maps /api/ai to SCREEN_AI_ASSISTANT.
```
