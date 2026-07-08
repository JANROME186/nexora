# Nexora Documentation Standard

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
- First module definition package.

## Agent Rule

An agent must load the root framework, then the target project. The project folder is the working boundary unless the user explicitly asks to change Nexora framework standards.
