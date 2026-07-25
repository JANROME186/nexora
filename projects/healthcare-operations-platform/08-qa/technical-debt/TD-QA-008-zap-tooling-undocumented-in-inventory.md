---
id: TD-QA-008
format: markdown_structured_payload
type: technical-debt-item
name: OWASP ZAP local availability is undocumented in the toolchain inventory and
  baseline
version: 1.0.0
status: open
---

# Owasp Zap Local Availability Is Undocumented In The Toolchain Inventory And Baseline

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-QA-008
  type: technical-debt-item
  name: OWASP ZAP local availability is undocumented in the toolchain inventory and
    baseline
  version: 1.0.0
  status: open
  created_date: 2026-07-24
  updated_date: 2026-07-24
source:
  discovered_during_backlog_item: COM-MOD-016-QA-001
  module: COM-MOD-016 Commercial Launch and Customer Enablement
  evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-QA-001-validation.md
classification:
  category: quality_tooling_documentation
  affected_area: local_quality_toolchain_inventory
  risk_level: low
  blocking: false
current_state:
  issue: '03-architecture/technology-architecture/local-toolchain-inventory.md lists
    java, maven, node, npm, git, docker, trivy, ripgrep and python but has no entry
    for OWASP ZAP (zap-baseline.py / zap-api-scan.py), even though ZAP has been executed
    successfully and repeatedly against the local stack (HOP-QA-ALIGN-004, COM-MOD-012-QA-001:
    353 backend URLs, COM-MOD-013-QA-001: 939 backend URLs and 125 portal URLs). Separately,
    03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
    (lines ~188 and ~297) still states "OWASP ZAP unavailable locally" as the reason
    TD-QA-001 was accepted not_applicable_now, but TD-QA-001 in 08-qa/technical-debt/technical-debt-index.md
    has since been closed (remediation_strategy: closed_by_HOP_QA_ALIGN_004_zap_baseline_and_api_scans)
    and is demonstrably available. Both files are stale relative to actual, proven
    local capability.'
  compensating_control: 'DAST has not been skipped in practice: every QA-001 item
    since HOP-QA-ALIGN-004 has run real ZAP scans and recorded the results directly
    in its own QA/security-quality evidence, so the stale inventory/baseline text
    has not caused a validation to be incorrectly marked not_applicable. The risk
    is purely that a future agent could misread the stale text and either skip DAST
    or waste effort re-discovering ZAP''s local availability.

    '
target_state:
  preferred_open_source_tooling:
  - Add a zap entry (tool, version, install method, invocation path) to local-toolchain-inventory.md
    command_templates, mirroring the trivy entry.
  - Update stack-quality-toolchain-baseline.md's not_applicable_now.tools entry
    and the TD-QA-001 gap note to reflect that ZAP is available and routinely used,
    or remove the stale line entirely.
  expected_integration_points:
  - 03-architecture/technology-architecture/local-toolchain-inventory.md
  - 03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
remediation:
  strategy: gradual_when_toolchain_inventory_or_baseline_is_next_touched
  recommended_trigger:
  - next backlog item that updates local-toolchain-inventory.md or stack-quality-toolchain-baseline.md
    for any other reason
  - next release-readiness or GA hardening pass
  acceptance_criteria:
  - local-toolchain-inventory.md documents ZAP's local availability and invocation
    commands.
  - stack-quality-toolchain-baseline.md no longer states ZAP is unavailable locally.
  latest_evidence:
    backlog_item: COM-MOD-016-QA-001
    evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-QA-001-validation.md
    status: open_non_blocking
```
