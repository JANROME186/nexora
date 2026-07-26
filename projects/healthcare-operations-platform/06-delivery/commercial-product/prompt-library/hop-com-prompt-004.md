---
artifact:
  id: HOP-PROMPT-HOP-COM-PROMPT-004
  type: auxiliary-prompt
  status: active
  optimization: atomic_context
---

# HOP-COM-PROMPT-004 Auxiliary Prompt

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-COM-PROMPT-004
name: Compile and implement selected backlog item
intent: Compile generated outputs and implement only custom rule points for one backlog item at a time.
input:
- selected_backlog_item
- selected_capability_package_folders
- ../../07-implementation
- ../../08-qa
expected_output:
- code_changes
- tests
- security_quality_evidence_yaml
- security_quality_evidence_md
- qa_evidence_yaml
- qa_evidence_md
- registry_updates
prompt: "Implement only the selected backlog item.\nIf preparing work for another execution agent, first generate a compact\
  \ prompt using the mandatory local Python/Ollama context orchestrator.\nLoad the selected Capability Package models first\
  \ and follow existing project implementation patterns.\nBefore feature work, load ../../08-qa/technical-debt/technical-debt-index.md\
  \ and resolve or materially reduce at least one open debt item using the framework selection order. If no open debt exists,\
  \ record that explicitly in evidence.\nDetermine whether the current HOP phase requires higher debt burn-down intensity.\
  \ Early MVP work reduces at least one relevant debt item; module closeout, release preparation and late commercial work\
  \ must reduce multiple relevant items when open debt remains.\nGenerate repetitive platform artifacts from the models before\
  \ writing custom code.\nKeep changes scoped to the selected backlog item.\nExternalize new or changed user-visible text,\
  \ validation copy, error prose, status labels, error codes and repeated magic values through backend message bundles, frontend/mobile\
  \ localization resources, constants, configuration or policy providers as appropriate.\nAdd or update only generated tests\
  \ or custom rule tests appropriate to the backend, web, mobile, portal, integration or operations scope.\nRun or document\
  \ applicable open source security quality gates for the changed stack, including best practices, coding standards, duplicate\
  \ code, complexity, SAST/static analysis, OWASP or equivalent secure-code rules, dependency vulnerability checks across\
  \ all severities, secrets scan, coverage, message externalization/i18n review and DAST when a runnable surface exists.\n\
  Load 03-architecture/technology-architecture/local-toolchain-inventory.md before executing commands. Use its executable\
  \ paths, versions, working directories and generic command templates. If a listed required tool is stale or missing, update\
  \ the inventory when the correct value is known; otherwise create or update technical debt before closure.\nBuild a required-validation\
  \ matrix for every changed stack. If a required category lacks an executable HOP script, plugin or tool configuration, create\
  \ or update a technical-debt item under 08-qa/technical-debt/ with owner, target backlog, acceptance criteria and blocking\
  \ decision before closure.\nDo not record missing duplicate-code, complexity, SAST/static analysis, OWASP/secure-code, dependency,\
  \ secrets, coverage, i18n or DAST tooling as merely not applicable when the stack or runnable surface exists.\nFor OWASP\
  \ Dependency-Check, use the configured local advisory database. Do not refresh/download the NVD database during ordinary\
  \ backlog execution; that is a manual once-per-day responsibility of the project operator or security reviewer. Record the\
  \ database path and freshness timestamp/date in evidence.\nRecord current line coverage, previous iteration line coverage\
  \ baseline and the 80 percent target for every changed stack. If current coverage is below 80 percent, it must not decrease\
  \ below the previous baseline and the target gap must remain tracked as technical debt.\nMandatory executable gates must\
  \ actually run before closure. If Maven, Java, Node, npm,\nnative packages, Docker, database services, network access or\
  \ audit endpoints are missing or\nblocked, first attempt documented remediation or request approval. If still unavailable,\
  \ mark\nthe backlog blocked_by_environment or ready_for_external_validation, keep next_backlog_item on\nthe current backlog\
  \ item, write exact remediation commands, and stop. Do not use manual source\nreview to convert an unexecuted mandatory\
  \ gate into a pass.\nDo not close the backlog item with unresolved vulnerabilities of any severity, missing duplicate/complexity/OWASP\
  \ analysis, missing required validation tooling without registered technical debt, coverage below the previous iteration\
  \ baseline, missing debt-first action, or unexternalized user-facing messages/magic strings unless an immediate accepted-risk/debt\
  \ disposition exists with owner and target backlog.\nWrite security quality evidence under 08-qa/security-quality/<selected-backlog-item-id>/.\n\
  Write QA evidence under 08-qa and update PROJECT_STATE.md and SOURCE_OF_TRUTH.md.\nWrite 08-qa/handoffs/<selected-backlog-item-id>-summary.md\
  \ with Status, Cambios Clave, Deuda T\xC3\xA9cnica Creada y Siguiente Paso. Keep it compact and do not duplicate the full\
  \ execution log.\nBefore closure, reconcile every backlog pointer and status registry that references the\ncurrent or next\
  \ backlog item, including PROJECT_STATE.md, SOURCE_OF_TRUTH.md,\nHOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, affected capability\
  \ traceability.md files,\nlocal-solution-runbook.md/.md and any QA/security indexes. The current backlog item must\nbe consistently\
  \ closed, and the next_backlog_item pointer must match PROJECT_STATE.md.\nRun the verifiable HOP backlog closure audit before\
  \ marking the item closed:\n- parse all HOP YAML outside dependency/build folders;\n- search stale active/current/next backlog\
  \ ids, previous next-backlog ids and ready_for_next_backlog_item mismatches;\n- search changed evidence and registries for\
  \ not_executed, failed, passed_with_execution_limitation, closed_with_execution_limitation, blocked_by_missing_toolchain,\
  \ blocked_by_network and blocked_by_unsupported_runtime;\n- run git diff --check;\n- confirm tests, coverage, vulnerability\
  \ and static-analysis numbers recorded in evidence match actual command output;\n- commit when all gates pass and commits\
  \ are allowed;\n- confirm git status --short is clean after commit.\nIf any audit item fails or is not run, do not claim\
  \ completion, do not advance next_backlog_item, and report the item as incomplete or blocked with exact remediation steps.\n\
  Stop before starting the next backlog item.\n"
```
