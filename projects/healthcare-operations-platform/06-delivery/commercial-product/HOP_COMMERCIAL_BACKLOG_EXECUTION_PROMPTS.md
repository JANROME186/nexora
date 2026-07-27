---
artifact:
  id: HOP-COM-PROMPTS-001
  type: execution-prompt-index
  status: active
  optimization: atomic_context
---

# HOP Commercial Backlog Execution Prompts

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-PROMPTS-001
  type: backlog-execution-prompts
  name: HOP Commercial Backlog Execution Prompts
  version: 1.0.0
  status: approved
  human_readable: HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  machine_readable: HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  created_date: 2026-07-08
  owner: Nexora Product Architecture Team
rules_ref: 06-delivery/commercial-product/prompt-library/execution-rules.md
rules_summary:
  agent_agnostic: true
  ollama_prompt_optimization_required: true
  manual_execution_flow_default: true
  closure_validator_required_after_commit: true
  load_full_rules_only_on_demand: true
required_context:
  load_first:
  - PROJECT_STATE.md
  - SOURCE_OF_TRUTH.md
  - 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - 06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md
  load_on_demand:
  - 06-delivery/commercial-product/backlog-map/items/<ACTIVE_ITEM>.md
  - 06-delivery/commercial-product/prompt-library/<AUX_PROMPT>.md
prompt_sequence:
- id: HOP-COM-PROMPT-001
  path: 06-delivery/commercial-product/prompt-library/hop-com-prompt-001.md
  purpose: Select next executable backlog item
- id: HOP-COM-PROMPT-002
  path: 06-delivery/commercial-product/prompt-library/hop-com-prompt-002.md
  purpose: Generate capability package models
- id: HOP-COM-PROMPT-003
  path: 06-delivery/commercial-product/prompt-library/hop-com-prompt-003.md
  purpose: Validate capability package models
- id: HOP-COM-PROMPT-004
  path: 06-delivery/commercial-product/prompt-library/hop-com-prompt-004.md
  purpose: Compile and implement selected backlog item
- id: HOP-COM-PROMPT-005
  path: 06-delivery/commercial-product/prompt-library/hop-com-prompt-005.md
  purpose: Close module
validation_commands:
  yaml_parse:
    intent: Validate YAML syntax for source artifacts.
    command_template: Parse all YAML files outside generated dependency folders and fail on syntax errors.
  registry_references:
    intent: Confirm source registries point to existing files or folders.
    command_template: Validate SOURCE_OF_TRUTH.md and PROJECT_MANIFEST.md references.
  agent_agnostic_scan:
    intent: Confirm no artifact requires a specific named agent or vendor runtime.
    command_template: Scan source artifacts for named-agent or vendor-specific requirements and resolve findings.
  security_quality_gate:
    intent: Confirm open-source-first and security quality checks were run or documented for code-changing work.
    command_template: Load 03-architecture/technology-architecture/local-toolchain-inventory.md, verify required tool paths,
      then run applicable open source tests, build, coverage, best-practice and standards checks, duplicate-code checks, complexity
      checks, SAST/static analysis, OWASP/secure-code checks, dependency vulnerability checks across all severities, secrets
      scan, message externalization/i18n review and DAST where applicable. Build a required-validation matrix for every changed
      stack; if a required category lacks an executable HOP script, plugin or tool configuration, create or update technical
      debt with owner, target backlog, acceptance criteria and blocking decision before closure. Do not use 'if configured'
      or 'if scripts exist' as a closure condition. For OWASP Dependency-Check or equivalent local-advisory-database tools,
      run the scanner against the local database available at execution time and record freshness; do not refresh/download
      the database unless explicitly assigned. Address at least one open technical-debt item before feature work unless no
      open debt exists; increase debt burn-down intensity as the project advances. Coverage target is 80 percent; if below
      target, do not allow coverage to drop below the previous measured baseline. If mandatory executable gates cannot run,
      stop as blocked_by_environment or ready_for_external_validation and do not advance next_backlog_item.
  verifiable_closure_audit:
    intent: Prevent false backlog closure and stale handoffs.
    command_template: Before claiming completion, parse HOP YAML, sweep stale backlog pointers, sweep evidence/registries
      for limited or blocked gate states, run git diff --check, confirm evidence metrics match command output, commit when
      allowed, and verify git status --short is clean. If any item fails or is not run, keep next_backlog_item unchanged and
      report incomplete or blocked.
  git_whitespace:
    intent: Confirm no whitespace errors before commit.
    command_template: Run repository whitespace validation before closing the item.
  module_id: COM-MOD-015
  backlog_item_id: COM-MOD-015-CLOSEOUT
  name: Module closeout and registry update
  expected_folder: 01-product-definition/business-capabilities/packages/
  required_debt_first_action: none
  coverage_floor:
    backend_java_maven_line_coverage_percent_if_backend_is_touched: 70.16
    frontend_typescript_web_line_coverage_percent: 91.00
    mobile_typescript_foundation_line_coverage_percent: 99.21
    patient_portal_typescript_web_line_coverage_percent: 94.11
    doctor_portal_typescript_web_line_coverage_percent: 96.28
    public_website_typescript_web_line_coverage_percent: 98.61
    final_target_percent: 80
  mandatory_execution_notes:
  - Resume functional work only from the compact generated prompt and COM-MOD-015-QA-001 handoff; do not preload broad YAML
    registries.
  - Keep execution agent-agnostic and preserve the open-source-first stack and quality gates.
  - Address or reduce at least one applicable technical-debt item before feature work.
  - Preserve backend coverage at or above 70.16% (COM-MOD-015-QA-001 resynced the backend_java_maven baseline from 70.14%
    after fixing TD-BE-022; raising it back toward 80-84% is ordinary gradual coverage debt, not a newly discovered incident)
    and employee-portal coverage at or above 91.00%; keep final project target at 80% or higher.
  - Generate QA/security evidence, update SOURCE_OF_TRUTH, PROJECT_STATE, product backlog and execution prompts, and commit
    only when validation passes.
  previous_backlog_item:
    backlog_item_id: COM-MOD-015-CLOSEOUT
    status: closed
    summary: Closed COM-MOD-015-CLOSEOUT. Formally closed AI Overlay (BCM-AI-001 through BCM-AI-008 marked module_closed in capability-package-index.md, capability-package.md files and traceability.md matrices).
```
