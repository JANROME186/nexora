# Release Notes — v0.42.0

## Summary

This release restructures the repository into a reusable Nexora framework plus self-contained project folders.

## Added

- `nexora-framework/` for company standards, recipes, templates and governance.
- `projects/` for solution-specific work.
- Nexora project folder standard.
- Nexora documentation standard.
- Nexora Agent-to-MVP recipe.
- New project template.
- Healthcare Operations Platform project brief.
- Healthcare Operations Platform project-local source of truth and state.
- Canonical project-stage folders for Healthcare Operations Platform.
- ADR-024 for the multi-project repository structure.

## Changed

- Healthcare Operations Platform artifacts moved under `projects/healthcare-operations-platform/`.
- Reusable framework assets moved under `nexora-framework/`.
- Root bootstraps and registries now describe repository-level orchestration.

## Result

New solutions can now be added by creating a folder under `projects/`, adding a project brief, and applying the Nexora Agent-to-MVP recipe.
