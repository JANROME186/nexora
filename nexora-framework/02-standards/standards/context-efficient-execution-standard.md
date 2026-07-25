# Context Efficient Execution and Local Orchestrator Standard

This standard defines the mandatory Nexora Framework execution stack for prompt generation and
agent orchestration.

The base stack is Python 3.11+, Ollama local service, at least one approved open source Ollama
model, `ripgrep` and `git`. The bootstrap default model is `qwen2.5-coder:0.5b`;
`qwen2.5-coder:3b`, `llama3.2:3b` and `qwen2.5-coder:7b` are also approved.

Ollama is now the primary local orchestrator. The final prompt is rendered by Python from a
canonical context so repeated runs remain stable. If the canonical context hash has not changed,
the cached prompt is reused. If Ollama or the required model is missing, framework bootstrap is
incomplete unless the operator explicitly runs a diagnostic fallback.

## Required Flow

1. Read the active backlog pointer.
2. Inspect only relevant lines or sections.
3. Build and hash the canonical context.
4. Ask Ollama for deterministic orchestration metadata.
5. Render a compact prompt with `ROOT` defined once.
6. Persist the prompt to `08-qa/generated-prompts/<TASK_ID>-prompt.md`.
7. Reuse the cached prompt while the context hash is unchanged.
8. Send the compact prompt to the execution agent.
9. At closure, write `<TASK_ID>-summary.md` with `Status`, `Cambios Clave`, `Deuda Técnica Creada` and `Siguiente Paso`.

The renderer must deduplicate grep/search output before writing the final prompt. Repeated lines
that only confirm the same task id, pointer or state must collapse into one file reference. The
renderer must also filter context by workstream: a backend task includes backend coverage and
backend gates, not unrelated mobile or portal coverage floors unless those components are directly
changed. Generated prompts must use one language consistently; HOP commercial prompts are rendered
in Spanish.

When a task belongs to a project folder, the generated prompt must declare `PROJECT` once after
`ROOT`. All pointers and deliverables inside that project must then use project-relative paths
instead of repeating the full project prefix.

## Format Policy

New task and handoff artifacts should use Markdown with minimal YAML frontmatter. Compact inventories or configuration may use TOML or Markdown tables.

Existing YAML artifacts remain supported until a controlled migration is completed. New monolithic YAML state/task artifacts should not be introduced unless automation truly requires them. When large YAML artifacts remain, projects must register migration debt instead of ignoring the issue.

## Prompt Contract

```text
# TASK: [ID_TAREA] - [TITULO]
ROOT: [RUTA_BASE]
PROJECT: [RUTA_PROYECTO]

## 1. Alcance / Objetivos Directos
- [Instrucciones concisas]

## 2. Contexto Inmediato (Punteros)
- Ref: [handoff previo o archivo puntual]

## 3. Entregables
- [Archivos a crear/modificar]
- Crear [TASK_ID]-summary.md con Status, Cambios Clave, Deuda Técnica Creada y Siguiente Paso.

## 4. Criterios de Cierre
- [Status esperado]
- [Pruebas obligatorias]
- [Conventional Commit sugerido]
```

Quality gates, security checks, coverage floors, stale-pointer sweeps and clean git status remain mandatory. Token optimization never justifies skipping validation.

## Backlog Closure Validation

After an execution agent claims completion, the framework must run the local closure validator
through the compact tool reference `tool: backlog_closure_validator`.

Generated prompts must reference only:

```text
tool: backlog_closure_validator
task_id: <TASK_ID>
prompt_ref: projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md
```

The full executable invocation is owned by
`nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md` and must not be
repeated in every generated prompt.

The validator first applies deterministic repository checks: expected evidence files, evidence
status, project state, product backlog status, execution prompt transition, source-of-truth
references and clean git status. Ollama is then used as the mandatory local summarizer only. It
cannot override deterministic P0/P1 findings, and it cannot keep a backlog incomplete when the
deterministic layer has zero findings.

If the backlog is incomplete, the validator writes
`08-qa/generated-prompts/<TASK_ID>-closure-fix-prompt.md` with only the missing work required to
close the item.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-STD-014
  type: execution-standard
  name: Context Efficient Execution and Local Orchestrator Standard
  version: 1.1.0
  status: approved
  human_readable: context-efficient-execution-standard.md
  machine_readable: context-efficient-execution-standard.md
  owner: Nexora Engineering
purpose: Define the mandatory Nexora Framework execution stack for low-cost local
  orchestration, deterministic prompt generation and agent-agnostic commercial-agent
  handoff.
principles:
  agent_agnostic: true
  local_orchestration_required: true
  ollama_primary_orchestrator_required: true
  commercial_prompt_budget_target_tokens: 300_to_600
  no_quality_degradation_allowed: true
  no_named_agent_dependency_allowed: true
  lazy_loading_required: true
  memory_handoff_required: true
local_orchestrator:
  preferred_runtime: python
  required_model_runtime: ollama
  required_default_model: qwen2.5-coder:0.5b
  approved_models:
  - qwen2.5-coder:0.5b
  - qwen2.5-coder:3b
  - llama3.2:3b
  - qwen2.5-coder:7b
  role: prompt_orchestrator_and_context_compressor
  responsibilities:
  - read the active backlog pointer
  - inspect only relevant local file slices through ripgrep or targeted reads
  - select the minimum relevant context files
  - generate a synthetic prompt for the commercial execution agent
  - produce a compact memory handoff after task closure
  - persist generated prompts in a deterministic file path
  - reuse prompt cache when the canonical context hash has not changed
  prohibited_responsibilities:
  - replace mandatory quality gates
  - skip executable validation
  - require a specific cloud agent or proprietary runtime
  - hide blockers or unresolved debt
mandatory_framework_stack:
  runtime:
  - python_3_11_or_newer
  - ollama_local_service
  - approved_open_source_ollama_model
  inspection_tools:
  - ripgrep
  - git
  deterministic_generation:
    required: true
    rules:
    - Build a canonical context object from active backlog pointers and compact handoffs.
    - Hash the canonical context.
    - Reuse cached prompt output while the hash is unchanged.
    - Use Ollama with temperature 0, fixed seed and JSON output for orchestration
      metadata.
    - Render the final prompt with Python from canonical fields to prevent model wording
      drift.
    - Treat missing Ollama model as a prerequisite failure unless an explicit diagnostic
      fallback flag is used.
format_policy:
  new_task_inputs:
    preferred_format: markdown_with_yaml_frontmatter
    frontmatter_scope: minimal_metadata_only
    body_scope: concise_human_readable_task_instructions
  new_handoffs:
    required: true
    filename_pattern: <TASK_ID>-summary.md
    max_tokens: 200
    required_sections:
    - Status
    - Cambios Clave
    - Deuda Técnica Creada
    - Siguiente Paso
  compact_configuration:
    preferred_formats:
    - TOML
    - compact_markdown_tables
  existing_yaml:
    status: legacy_supported_until_migrated
    rule: Existing YAML source artifacts remain valid and must not be deleted without
      a controlled migration. New monolithic YAML task/state artifacts are discouraged;
      use Markdown with minimal frontmatter unless automation requires structured
      YAML.
    migration_required: true
    debt_required_when_monolithic_yaml_remains: true
lazy_loading:
  root_path:
    rule: Define ROOT_PATH once per generated prompt.
    project_path_rule: Define PROJECT_PATH once when the task targets a project folder.
    repetition_policy: Avoid repeating long absolute or project-prefixed paths.
    project_relative_policy: After PROJECT_PATH is declared, use paths relative to
      that project folder in pointers and deliverables.
  file_loading:
    preload_full_files: prohibited_unless_explicitly_required
    required_method: targeted_inspection
    allowed_tools:
    - rg
    - rg --files
    - targeted read_file
    - line-ranged file reads
    - local python filters
    - local semantic search when available
  prompt_instructions:
  - Ask the commercial agent to run targeted commands for the lines or sections it
    needs.
  - Include pointers to files and handoffs, not pasted full file contents.
  - Prefer the latest task summary over historical logs.
  - Deduplicate grep or search output before prompt rendering; never paste repeated
    lines that only confirm the same task id, pointer or state.
  - Keep only one reference per relevant source file when multiple matches point to
    the same source.
  - Filter context by workstream; backend prompts must not include frontend, mobile
    or portal coverage floors unless those stacks are directly changed by the backlog.
  - Render each generated prompt in one language consistently. HOP commercial backlog
    prompts use Spanish.
local_context_processing:
  search:
    primary: ripgrep
    optional_semantic:
      vector_store: ChromaDB
      embedding_model: all-MiniLM-L6-v2
      status: optional
  prompt_compression:
    optional_tool: llmlingua
    rule: Always apply deterministic compression before persisting generated prompts.
    required_cleanup:
    - remove duplicate pointers
    - remove repeated task-id confirmations
    - remove repeated closure rules
    - remove mixed-language bullets
    - remove irrelevant cross-stack coverage details
  ollama_routing:
    required: true
    rule: Use a local Ollama model for routing, preliminary summaries and orchestration
      metadata. The final prompt must still be rendered from canonical deterministic
      fields. If Ollama or the required model is unavailable, the framework bootstrap
      is incomplete.
backlog_closure_validation:
  required_after_agent_claims_completion: true
  compact_tool_reference: backlog_closure_validator
  tool_registry: nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md
  local_validator: nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  model_runtime: ollama
  required_model: qwen2.5-coder:0.5b
  deterministic_checks_are_authoritative: true
  ollama_decision_role: informational_summary_only
  required_inputs:
  - generated backlog prompt
  - project state
  - product backlog
  - execution prompts
  - source of truth
  - QA evidence
  - security-quality evidence
  - handoff summary
  - git status
  closure_policy:
  - If P0/P1 deterministic findings exist, the backlog is incomplete regardless of
    the local model summary.
  - If no deterministic findings exist, the backlog is closed even if the local model
    produces ambiguous wording.
  - If the backlog is incomplete, generate a compact closure-fix prompt with only
    the missing work.
  - Do not advance the next backlog pointer until the validator reports closed.
  - Validation reports must be persisted under the project 08-qa/backlog-validations
    folder.
  - Closure-fix prompts must be persisted under the project 08-qa/generated-prompts
    folder.
synthetic_prompt_contract:
  template: '# TASK: [ID_TAREA] - [TITULO]

    ROOT: [RUTA_BASE]

    PROJECT: [RUTA_PROYECTO]


    ## 1. Alcance / Objetivos Directos

    - [Instrucciones concisas]


    ## 2. Contexto Inmediato (Punteros)

    - Ref: [handoff previo o archivo puntual]


    ## 3. Entregables

    - [Archivos a crear/modificar]

    - Crear [TASK_ID]-summary.md con Status, Cambios Clave, Deuda Técnica Creada y
    Siguiente Paso.


    ## 4. Criterios de Cierre

    - [Status esperado]

    - [Pruebas obligatorias]

    - [Conventional Commit sugerido]

    '
closure_rules:
- Every task must produce a compact summary handoff.
- Responses must not consolidate the full historical execution log.
- Quality gates, security checks, coverage floors, stale-pointer sweeps and clean
  git status remain mandatory.
- If format migration is outside the selected backlog scope, register or update technical
  debt instead of silently ignoring it.
- Generated prompt files must be stable across repeated executions while the canonical
  context hash is unchanged.
- After an execution agent claims completion, run `tool: backlog_closure_validator`
  before accepting the item as closed.
```
