# Change Impact Analysis

Change impact analysis is mandatory before modifying any approved node.

## Impact levels

| Level | Description |
|---|---|
| Low | Documentation or metadata only. |
| Medium | Affects a single capability or module. |
| High | Affects APIs, data model, workflows, security or cross-channel behavior. |
| Critical | Affects compliance, clinical safety, billing, authentication or data isolation. |

## Required steps

1. Identify the changed node.
2. Load direct relationships.
3. Load downstream relationships.
4. Classify impact.
5. List affected artifacts.
6. Create RFC if impact is High or Critical.
7. Update tests and contracts before implementation.
