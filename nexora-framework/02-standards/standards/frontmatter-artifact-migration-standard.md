# Frontmatter Artifact Migration and Optimization Standard

Nexora uses Markdown with compact YAML frontmatter as the target format for task, backlog,
evidence, runbook and handoff artifacts. The goal is to reduce token cost and stale-context risk
without relying on commercial models for mass refactoring.

## Target Format

Frontmatter stores only minimal metadata: IDs, status, dates, owner, priority and next pointers.
The Markdown body stores readable scope, evidence, tables, audit notes and technical debt.

New artifacts must not create parallel "machine YAML plus human MD" pairs unless automation has a
specific approved need.

## Migration Strategy

Structured artifacts are migrated with Python and PyYAML only. This covers backlog files, QA and
security evidence, technical debt and runbooks.

Narrative or very large artifacts may use Python plus local Ollama, never a commercial model.
Ollama is only a local restructuring assistant; deterministic parsing remains the default.

## Required Flow

1. Inventory and classify candidate files.
2. Run a pilot conversion with a small limit.
3. Review the generated Markdown/frontmatter manually.
4. Run deterministic conversion for structured files.
5. Enable Ollama only for narrative files that cannot be safely converted with PyYAML.
6. Validate references and update pointers from `.yaml` to `.md` only after replacement files exist.
7. Archive or remove source YAML only after validation passes.

## Tool

Install/validate local Python requirements before running SLLM migration:

```powershell
python -m pip install -r nexora-framework/08-engineering/agents/context-orchestrator/requirements.txt
```

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --scope projects/healthcare-operations-platform `
  --limit 20
```

Use `--apply` to write converted Markdown files. Use `--use-ollama` only when local SLLM processing
is required. Use `--combine-existing` when a YAML file already has an existing Markdown companion
and both contents must be preserved. Use `--archive-source` only after validation confirms
references are safe.

The tool writes compact Markdown/frontmatter reports to scope-specific files, for example:

`projects/healthcare-operations-platform/08-qa/format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md`

Migration reports must not include full per-file inventories by default. They must include only
counts, strategy/type breakdowns, bounded samples, errors and reference issues. Detailed inventories
may be generated only as temporary local diagnostics and must not become official project evidence.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-STD-015
  type: artifact-format-migration-standard
  name: Frontmatter Artifact Migration and Optimization Standard
  version: 1.0.0
  status: approved
  human_readable: frontmatter-artifact-migration-standard.md
  machine_readable: frontmatter-artifact-migration-standard.md
  owner: Nexora Engineering
purpose: Define the mandatory local, open-source-first process for migrating heavy
  YAML/Markdown artifacts into compact Markdown with YAML frontmatter using deterministic
  Python parsing and Ollama local summarization only when necessary.
target_format:
  primary: markdown_with_yaml_frontmatter
  frontmatter: minimal_metadata_only
  body: concise_markdown_tables_and_bullets
  no_dual_human_machine_files: true
  new_artifacts_must_not_generate_parallel_yaml_and_md_pairs: true
migration_strategy:
  structured_artifacts:
    examples:
    - backlog
    - qa_evidence
    - security_quality_evidence
    - technical_debt
    - runbook
    processor: python_pyyaml_deterministic
    commercial_tokens_allowed: false
    ollama_allowed: false
  narrative_or_large_artifacts:
    examples:
    - long_free_text_markdown
    - long_yaml_with_nested_logs
    - disordered_evidence_narrative
    processor: python_plus_ollama_local
    commercial_tokens_allowed: false
    ollama_required_when_sllm_processing_is_used: true
execution_phases:
- inventory_and_classification
- pilot_conversion
- deterministic_conversion
- ollama_narrative_conversion_when_enabled
- reference_validation_and_pointer_cleanup
- source_archive_or_removal_only_after_validation
guardrails:
- Do not delete or archive source YAML during inventory or pilot dry-runs.
- Do not remove authoritative automation YAML without a validated Markdown/frontmatter
  replacement.
- Do not use commercial models for migration.
- Preserve IDs, statuses, dates, routes, numeric metrics and evidence names exactly.
- If references cannot be updated safely, keep the source and report reference_issues.
- SOURCE_OF_TRUTH and PROJECT_STATE files are excluded unless an explicit future backlog
  covers them.
tooling:
  migration_tool: nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py
  required_python_libraries:
  - PyYAML
  - official_python_ollama_package
  default_ollama_model: qwen2.5-coder:0.5b
  preferred_large_local_models:
  - qwen2.5-coder:7b
  - qwen2.5-coder:3b
  - llama3.2:3b
  validation:
    required:
    - migration_report
    - yaml_parse
    - reference_sweep
    - git_diff_check
    - pilot_manual_review_before_mass_apply
  report_policy:
    official_report_format: markdown_with_yaml_frontmatter
    official_report_path_pattern: projects/healthcare-operations-platform/08-qa/format-migration/frontmatter-migration-report-<scope>.md
    full_inventory_in_official_report_allowed: false
    max_sample_rows_per_section: 20
    detailed_inventory_allowed_only_as_temporary_local_diagnostic: true
```
