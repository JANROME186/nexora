# Nexora Agent-Agnostic Standard

## Purpose

Every Nexora project must be understandable, plannable and implementable by any capable AI agent, deterministic automation or human engineering team.

No project may require a named assistant, coding agent, model provider, runtime, cloud service or hosted platform to interpret requirements or start implementation.

## Rules

- Repository artifacts are the source of truth.
- Prompts, tool wrappers and local automation are adapters.
- Project folders must include enough definition to start without chat history.
- Vendor, model, cloud and runtime choices must remain replaceable behind ports or adapters.
- Agent-specific files may not become mandatory loading context.

## Validation

Before marking a project ready for development, run a repository text audit for known agent, assistant, model-vendor and platform-runtime names.

The expected result is no source-artifact matches, unless an approved ADR documents a temporary migration exception.
