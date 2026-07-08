# Nexora Auxiliary Development Prompts

## Purpose

These prompts support development after the generic MVP development prompt has selected a project, module and backlog item.

They do not replace the main lifecycle prompts:

- `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.yaml`

If there is a conflict, the project source artifacts and the generic MVP development prompt win.

## Prompt Hierarchy

1. Project `BUSINESS_REQUIREMENT.md` and project source-of-truth files.
2. Target module package files.
3. Generic MVP development prompt.
4. Project-specific backlog execution playbook, when present.
5. Auxiliary development prompts.

## Available Auxiliary Prompts

- `AUX-DEV-001`: Module development kickoff.
- `AUX-DEV-002`: Implement selected backlog slice.
- `AUX-DEV-003`: Backend slice implementation.
- `AUX-DEV-004`: Web slice implementation.
- `AUX-DEV-005`: Mobile slice implementation.
- `AUX-DEV-006`: QA and module closeout.

The machine-readable source is:

```text
nexora-framework/05-prompts/prompts/auxiliary-development-prompts.yaml
```

## Minimal User Prompts

Module kickoff:

```text
Prepare development kickoff for <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```

Backlog slice:

```text
Implement backlog item <backlog-item-id> for <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```

QA and closeout:

```text
Validate and close <module-id> in projects/<project-slug>/ using the Nexora auxiliary development prompts.
```
