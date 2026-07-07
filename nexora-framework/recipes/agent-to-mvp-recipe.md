# Nexora Agent-to-MVP Recipe

## Purpose

This recipe tells an agent how to take a project description and prepare a development-ready MVP.

It is project-agnostic. Healthcare Operations Platform is the first project using it.

## Required Input

Every project starts with:

`projects/<project-slug>/PROJECT_BRIEF.md`

If this file is missing, the agent must create it or ask for the missing user needs.

## Phases

1. Intake normalization.
2. Product definition.
3. Domain foundation.
4. Architecture baseline.
5. MVP delivery framework.
6. Development readiness gate.

## Development Readiness Gate

Development can start when:

- The project brief exists.
- Source of truth exists.
- Project state exists.
- Capability map and dependency map exist.
- Actor catalog exists.
- Reference processes exist.
- Business rules exist.
- MVP framework exists.
- First module definition package exists.
- `blocking_definition_gaps` is empty.

## Output

At the end of the recipe, an agent should know exactly which module to implement first and which files to load before coding.
