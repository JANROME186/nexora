---
id: PB-001
name: Change Impact Analysis
status: draft
version: 0.13.0
---

# PB-001 Change Impact Analysis

Use this playbook before changing any approved or review-status node.

## Steps

1. Identify the target node ID.
2. Open `KNOWLEDGE_INDEX.yaml`.
3. Find capability-specific index if available.
4. Load direct relations from `knowledge/relations/`.
5. Load downstream artifacts.
6. Classify impact: Low, Medium, High or Critical.
7. If High or Critical, create an RFC.
8. Update affected contracts, stories, rules, tests and documentation.
9. Record the change in `CHANGELOG.md`.

## Output

The agent or contributor must produce an impact report with:

- Target node.
- Affected nodes.
- Required updates.
- Risk level.
- Recommended validation steps.
