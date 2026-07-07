# Knowledge Graph Principles

## 1. Every important artifact is a node

A node can represent a business capability, process, rule, user story, API, entity, event, screen, test, ADR, RFC, playbook or deployment artifact.

## 2. Every node has metadata

Minimum metadata:

- `id`
- `name`
- `type`
- `status`
- `version`
- `owner`
- `created_at`
- `updated_at`
- `source_path`

## 3. Every relationship must be explicit

A relationship must describe why two nodes are connected.

Example:

```yaml
from: US-001
to: API-001
type: implemented_by
```

## 4. The graph is the context source for agents

Agents must not scan the full repository unless the playbook requires it. They should start from the node index and load only related context.

## 5. Impact analysis is mandatory

Any change to a node with downstream dependencies must trigger impact analysis before implementation.
