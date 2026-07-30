---
id: TD-FMT-001
format: markdown_structured_payload
type: technical-debt-item
name: Transition monolithic YAML task/state artifacts to Markdown frontmatter handoffs
version: 1.0.0
status: closed
---

# Transition Monolithic Yaml Task/State Artifacts To Markdown Frontmatter Handoffs

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FMT-001
  type: technical-debt-item
  name: Transition monolithic YAML task/state artifacts to Markdown frontmatter handoffs
  version: 1.1.0
  status: closed
  created_date: 2026-07-24
  updated_date: 2026-07-30
source:
  discovered_during_backlog_item: NXF-CTX-001
  module: Nexora Framework Context Efficient Execution update
  standard: ../../../../nexora-framework/02-standards/standards/context-efficient-execution-standard.md
classification:
  category: documentation_format_migration
  affected_area: framework_and_hop_execution_artifacts
  risk_level: high
  blocking: false
current_state:
  issue: HOP and Nexora Framework still contain YAML execution/state artifacts created
    before the context-efficient execution standard. These remain valid, but they
    are expensive for commercial agents to preload and can cause stale-pointer drift
    when agents read broad registries instead of compact handoffs.
  affected_artifact_types:
  - backlog execution prompts
  - project state registries
  - source-of-truth registries
  - capability package indexes
  - QA/security evidence indexes
  compensating_control: Existing authoritative YAML remains supported for automation
    only. Agent-facing backlog execution must start from compact Markdown/frontmatter
    prompts and handoffs, and agents must use lazy loading plus targeted rg/read operations
    for any remaining registries.
target_state:
  preferred_formats:
  - Markdown with minimal YAML frontmatter for task inputs, user stories and handoffs.
  - TOML or compact Markdown tables for inventories and concise configuration lists.
  migration_support:
  - Use local Python scripts and optional Ollama models to summarize and convert legacy
    YAML without consuming commercial model tokens.
  - Preserve machine-readability during migration and never delete authoritative YAML
    until a replacement has validation evidence.
remediation:
  strategy: gradual_migration_during_normal_backlog_execution
  recommended_trigger:
  - next prompt framework update
  - next module closeout
  - next evidence registry refactor
  acceptance_criteria:
  - New tasks produce <TASK_ID>-summary.md handoffs under 08-qa/handoffs/.
  - New prompt handoffs follow the context-efficient prompt contract.
  - A migration plan identifies which legacy YAML artifacts can be replaced, split
    or frontmatter-wrapped.
  - Optional Ollama-assisted conversion is validated locally or explicitly marked
    unavailable without blocking execution.
  latest_evidence:
    backlog_item: COM-MOD-015-CLOSEOUT
    status: materially_reduced
    hop_inventory: ../format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md
    framework_inventory: ../format-migration/frontmatter-migration-report-nexora-framework.md
    migration_plan: ../format-migration/frontmatter-migration-plan.md
    closeout_handoff: ../handoffs/COM-MOD-015-CLOSEOUT-summary.md
    note: COM-MOD-015-CLOSEOUT formally closed AI Overlay using compact Markdown/frontmatter evidence and handoff artifacts, materially reducing TD-FMT-001 across the entire module lifecycle.
```
