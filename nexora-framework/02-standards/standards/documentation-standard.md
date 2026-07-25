# Nexora Documentation Standard

Status: approved, version 1.1.0.

Nexora still supports existing YAML automation artifacts, but new task inputs, user stories and
handoffs should prefer Markdown with minimal YAML frontmatter. Large monolithic YAML state/task
files are now considered legacy-supported until a controlled migration is completed.

When a `<TASK_ID>-summary.md` exists, agents must read it before historical logs or large evidence
files. The summary is the continuation memory handoff and should stay below 200 tokens.

Agents must use lazy loading: point to files, run targeted `rg` or line-range reads, and avoid
pasting complete files into prompts unless explicitly required.

## Purpose

Nexora documentation must be both human-readable and machine-usable.

The standard pattern is:

- YAML for source-of-truth structure.
- Markdown for explanation and collaboration.
- Mermaid or generated Markdown for derived views.

## Minimum MVP Documentation

A project is ready for MVP development only when it has:

- Project brief.
- Source of truth.
- Project state.
- Capability map.
- Capability dependency map.
- Actor catalog.
- Reference processes.
- Business rules catalog.
- MVP framework.
- First Business Capability Package or roadmap group package.

## Agent Rule

An agent must load the root framework, then the target project. The project folder is the working boundary unless the user explicitly asks to change Nexora framework standards.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-STD-002
  type: documentation-standard
  name: Nexora Documentation Standard
  version: 1.1.0
  status: approved
  owner: Nexora Engineering
artifact_rules:
  dual_format:
    required: true
    machine_readable: yaml_or_markdown_frontmatter
    human_readable: markdown
    transition_policy: Existing YAML automation artifacts remain supported. New task,
      backlog handoff and execution prompt artifacts should prefer Markdown with minimal
      YAML frontmatter unless full structured YAML is required by automation.
  source_first:
  - Existing YAML source artifacts are authoritative when automation is expected until
    migrated.
  - Markdown with minimal frontmatter is the preferred lightweight format for new
    task inputs, user stories and handoffs.
  - Markdown explains intent, tradeoffs and usage.
  - Generated artifacts must reference their source.
  agent_execution:
  - Agents must prefer YAML files when both YAML and Markdown versions exist for legacy
    structured automation artifacts.
  - Agents must prefer Markdown summary handoffs for continuation context when a <TASK_ID>-summary.md
    file exists.
  - Agents must not preload complete source files into prompts; they must inspect
    only relevant lines or sections.
  - Markdown files remain the human-readable companion for understanding, review and
    explanation.
  - BUSINESS_REQUIREMENT.md is requester-supplied; any BUSINESS_REQUIREMENT.md is
    a structured index derived from the requester input, not a replacement.
  - Agents must verify BUSINESS_REQUIREMENT.md is derived from the latest resolved
    BUSINESS_REQUIREMENT.md version.
  lightweight_formats:
    task_inputs: markdown_with_yaml_frontmatter
    handoffs: markdown_with_yaml_frontmatter
    compact_inventory_or_configuration: toml_or_markdown_table
    monolithic_yaml_policy: discouraged_for_new_task_state_artifacts
  traceability:
    required_links:
    - project_brief
    - capability
    - actor
    - process
    - business_rule
    - contract
    - test
minimum_artifacts_to_reach_mvp_development:
- business_requirement
- project_brief
- source_of_truth
- project_state
- capability_map
- dependency_map
- actor_catalog
- reference_processes
- business_rules_catalog
- mvp_framework
- first_business_capability_package_or_roadmap_group_package
agent_context_rules:
- Agents load root Nexora framework first.
- Agents scan projects/ using nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md
  before implementation.
- Agents load the target project SOURCE_OF_TRUTH second.
- Agents resolve the latest business requirement version before consuming derived
  YAML or generated artifacts.
- Agents must not infer requirements from chat if PROJECT_BRIEF is missing.
- Agents must follow nexora-framework/02-standards/standards/agent-agnostic-standard.md.
- Agents must update project state after meaningful iterations.
- Agents must preserve project boundaries.
- Agents must follow nexora-framework/02-standards/standards/context-efficient-execution-standard.md
  for token-optimized task execution and handoffs.
```
