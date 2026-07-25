---
id: TD-QA-008
format: markdown_structured_payload
type: technical-debt-item
name: OWASP ZAP local availability is undocumented in the toolchain inventory and
  baseline
version: 2.0.0
status: closed
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
  version: 2.0.0
  status: closed
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
  issue: 'Closed by COM-MOD-017-BE-002 (opportunistic non-blocking debt reduction,
    unrelated to this item''s own marketplace scope). local-toolchain-inventory.md
    now documents an owasp-zap entry (Docker image ghcr.io/zaproxy/zaproxy:stable,
    zap-baseline.py/zap-api-scan.py, no local install) with detection/version/invocation
    commands and proven_usage pointers to HOP-QA-ALIGN-004, COM-MOD-012-QA-001 and
    COM-MOD-013-QA-001, mirroring the trivy entry format. stack-quality-toolchain-baseline.md''s
    not_applicable_now.tools no longer claims "OWASP ZAP unavailable locally"; the
    DAST line was moved to already_covered with a pointer to the toolchain inventory.'
  compensating_control: null
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - 03-architecture/technology-architecture/local-toolchain-inventory.md
  - 03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
remediation:
  strategy: closed_by_COM_MOD_017_BE_002_inventory_and_baseline_doc_correction
  owner: qa_team
  estimated_effort: none_remaining
  estimated_cost_impact: none
  target_backlog: COM-MOD-017-BE-002
  dependencies_or_prerequisites: []
  acceptance_criteria:
  - id: local-toolchain-inventory.md documents ZAP's local availability and invocation
      commands.
    status: closed
  - id: stack-quality-toolchain-baseline.md no longer states ZAP is unavailable locally.
    status: closed
  latest_evidence:
    backlog_item: COM-MOD-017-BE-002
    evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md
    status: closed
```
