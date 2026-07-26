---
artifact:
  id: COM-MOD-015-BE-001-QA
  type: qa-validation-evidence
  status: passed
  backlog_item: COM-MOD-015-BE-001
---

# COM-MOD-015-BE-001 QA Validation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-015-BE-001-QA
  type: qa-validation-evidence
  status: validated
  backlog_item: COM-MOD-015-BE-001
backlog_item: COM-MOD-015-BE-001
module: COM-MOD-015 AI Overlay
status: validated
scope:
- compiled backend AI assistant orchestration output for BCM-AI-001
- added provider-neutral draft generator port and deterministic replaceable adapter
- added mandatory human-review policy enforcement and audit events
- added local PostgreSQL schema ai_overlay.ai_interactions
- registered IAM endpoint permission SCREEN_AI_ASSISTANT
technical_debt_reduced:
- TD-BE-017 materially reduced by introducing the first real orchestration target and persisted audit state for AI assistant workflows.
- LocalFilesystemDocumentAdapterTest no longer depends on inaccessible OS temp paths in the Windows sandbox; it uses target/test-documents.
agent_runtime:
  router: commercial_agent_router
  selected_provider: codex_cli
  execution_flow: cli
  channel: local_subscription
gates:
  router_preflight: passed
  maven_test:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents test
    result: passed
    tests: 505
    failures: 0
    errors: 0
    skipped: 31
  local_database:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Dhop.local-db-tests=true -Dtest=AiOverlayLocalDatabaseTest test
    result: passed
    tests: 2
    failures: 0
    errors: 0
    skipped: 0
    evidence: Hikari connected to local PostgreSQL; ai_overlay schema/table initialized and draft persisted.
  full_local_database_regression:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Dhop.local-db-tests=true test
    result: passed
    tests: 505
    failures: 0
    errors: 0
    skipped: 0
  maven_quality_verify:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Pquality clean verify
    result: passed
    tests: 505
    failures: 0
    errors: 0
    skipped: 31
  coverage:
    tool: JaCoCo
    line_coverage_percent: 70.29
    baseline_policy_note: The current generated backend JaCoCo aggregate is below the historical 84.65 pointer; this item added focused tests and did not skip the evidence. The discrepancy is documented as an open baseline drift for later quality alignment rather than hidden.
  docker_local:
    docker_cli_result: blocked_by_host_permission
    detail: Docker daemon access failed on Windows npipe due permission denied while reading user Docker config and connecting to the API.
    compensating_evidence: local PostgreSQL database gate passed with Spring local profile.
acceptance:
- Assistant drafts are advisory and require human review before operational use.
- Prohibited autonomous validation prompts are blocked.
- Review acceptance/rejection requires reviewer identity and reason.
- Audit records are available per tenant.
- No proprietary model provider or agent runtime dependency was introduced.
```
