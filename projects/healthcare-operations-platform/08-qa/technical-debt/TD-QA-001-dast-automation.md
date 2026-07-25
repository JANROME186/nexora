---
id: TD-QA-001
format: markdown_structured_payload
type: technical-debt-item
name: Automate DAST baseline scans for runnable web and API surfaces
version: 2.0.0
status: closed
---

# Automate Dast Baseline Scans For Runnable Web And Api Surfaces

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-QA-001
  type: technical-debt-item
  name: Automate DAST baseline scans for runnable web and API surfaces
  version: 2.0.0
  status: closed
  created_date: 2026-07-09
  updated_date: 2026-07-16
source:
  discovered_during_backlog_item: MVP-MOD-002-QA-001
  module: MVP-MOD-002 Diagnostic Catalog
  evidence: 08-qa/security-quality/MVP-MOD-002-QA-001/security-quality-evidence.md
classification:
  category: quality_tooling
  affected_area: integrated_web_and_api_runtime
  risk_level: medium
  blocking: false
current_state:
  issue: OWASP ZAP DAST has not yet been executed against the current local API/UI
    runtime.
  compensating_control: 'All-severity dependency, filesystem, secret and misconfiguration
    scans pass with 0 findings, but this does not close DAST.

    '
target_state:
  preferred_open_source_tooling:
  - OWASP ZAP baseline scan for employee portal routes.
  - OWASP ZAP API scan for OpenAPI-backed backend surfaces.
  expected_integration_points:
  - local quality gate script
  - CI security quality workflow
  - module closeout evidence
remediation:
  strategy: immediate_quality_alignment_before_functional_development_resumes
  recommended_trigger:
  - next integrated runtime quality gate
  - release hardening
  - CI pipeline implementation
  - security-quality standard expansion
  acceptance_criteria:
  - DAST can run repeatably against local backend and employee portal.
  - Findings are exported to YAML/Markdown evidence.
  - Findings of any severity are remediated or registered with accepted-risk disposition
    and owner.
  latest_evidence:
    backlog_item: HOP-QA-ALIGN-004
    evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-004-validation.md
    status: closed
closure:
  closed_by_backlog_item: HOP-QA-ALIGN-004
  closure_note: OWASP ZAP baseline scan (employee portal) and API scan (backend) both
    executed successfully with 0 FAIL-NEW against the running local stack. DAST is
    now repeatable via the commands documented in 09-operations/runbooks/local-solution-runbook.md.
    See 08-qa/qa/quality-alignment/HOP-QA-ALIGN-004-validation.md for full evidence.
```
