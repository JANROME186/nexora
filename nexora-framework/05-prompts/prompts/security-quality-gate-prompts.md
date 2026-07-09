# Security and Quality Gate Prompts

**Artifact ID:** `NXF-SQ-PROMPTS-001`  
**Status:** Approved  
**Machine-readable source:** `security-quality-gate-prompts.yaml`

Use these prompts with the Open Source First Security and Quality Standard.

## Open-Source-First Assessment

Load `open-source-first-security-quality-standard.yaml`, review the changed technology choices, and confirm that open source, self-hostable and standards-based options are preferred. If a proprietary mandatory dependency appears, require an ADR exception before continuing.

## Backlog Gate

For every code-changing backlog item, run or document applicable checks:

- Tests.
- SAST/static analysis.
- Dependency vulnerability scan.
- Secrets scan.
- Coverage.
- Contract quality.
- DAST when a runnable web/API surface exists.
- Container/IaC scan when deployment assets change.

Write evidence under:

`08-qa/security-quality/<backlog-item-id>/`

Do not close the backlog when critical/high findings, secrets, failing tests or undocumented coverage regressions remain unresolved.

## Module Closeout Gate

Before closing a module, aggregate backlog evidence and confirm that the module meets the required security and quality gates.
