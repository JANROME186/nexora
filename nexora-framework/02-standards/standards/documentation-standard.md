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
