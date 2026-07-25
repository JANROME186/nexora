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
