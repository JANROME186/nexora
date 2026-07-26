---
artifact:
  id: COM-MOD-015-BE-002-SQ
  type: security-quality-evidence
  status: passed
  backlog_item: COM-MOD-015-BE-002
---

# COM-MOD-015-BE-002 Security Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-015-BE-002-SQ
  type: security-quality-evidence
  status: validated
  backlog_item: COM-MOD-015-BE-002
backlog_item: COM-MOD-015-BE-002
module: COM-MOD-015 AI Overlay
status: validated
checks:
  sast_static_analysis:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Pquality spotbugs:spotbugs pmd:pmd pmd:cpd checkstyle:checkstyle cyclonedx:makeAggregateBom org.owasp:dependency-check-maven:check
    result: passed
    notes:
    - 'PMD initially flagged 3 AvoidFieldNameMatchingMethodName findings in the new
      AiOverlayCapability enum (capabilityId()/purposeKey()/allowedSourceContextTypes()
      accessors colliding with their field names); fixed by renaming to getCapabilityId()/
      getPurposeKey()/getAllowedSourceContextTypes(), matching this module''s existing
      AiOverlayErrorCode.getMessageKey() convention. PMD findings attributable to the new
      aioverlay.rules code: 0 after the fix.'
    - CPD duplications in aioverlay.rules: 0.
    - SpotBugs/FindSecBugs findings in aioverlay (assistant + rules): 0.
    - Checkstyle reports 98 pre-existing repo-wide style findings (unchanged from
      COM-MOD-015-BE-001); no blocking Maven failure because quality.failOnViolation is false.
  dependency_vulnerability_scan:
    tool: OWASP Dependency-Check 12.1.3
    result: passed
    dependencies_scanned: 72
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
    source_scan: 08-qa/security-quality/COM-MOD-015-BE-002/trivy-ai-overlay-source.json
    pom_scan: 08-qa/security-quality/COM-MOD-015-BE-002/trivy-backend-pom.json
    command_source: trivy fs --offline-scan --scanners vuln,secret,misconfig src/main/java/com/nexora/hop/platformfoundation/aioverlay --format json --output ...
    command_pom: trivy fs --offline-scan --scanners vuln pom.xml --format json --output ...
    result: passed
    findings: 0
  semgrep:
    result: unavailable
    detail: semgrep executable is not installed in the local CLI environment (unchanged from
      COM-MOD-015-BE-001).
  docker:
    result: passed
    detail: docker ps succeeded; hop-local-postgres, hop-local-redis and hop-local-otel-collector
      containers already up and healthy. Improvement over COM-MOD-015-BE-001's
      blocked_by_host_permission environment note - the local database gate ran against
      real PostgreSQL for this item.
security_controls:
- Custom rule engine enforces attributable, tenant-scoped source context types per
  capability and requires at least one source citation before AI output can be treated as
  reviewable, closing the concrete gap between each capability's business-rules.md guardrail
  statements and the code that enforces them.
- Output for these four capabilities is still forced human-review-required (never
  auto-applied), verified by a defensive check inside the rule engine itself, not only by the
  pre-existing generic assistant flow.
- No new REST surface was added, so no new endpoint permission registry entry was needed;
  /api/ai remains mapped to SCREEN_AI_ASSISTANT.
- Provider-neutral port and deterministic local adapter from COM-MOD-015-BE-001 are unchanged;
  no proprietary or token-billed AI SDK dependency was introduced.
```
