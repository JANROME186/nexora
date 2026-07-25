# AI Context Builder

The AI Context Builder defines how agents must load only the required context for a task.

## Context loading pattern

```text
Task → Target Node → Knowledge Index → Related Nodes → Source Files → Execution Playbook
```

## Rules

- Never load the whole repository unless explicitly required.
- Always start from `KNOWLEDGE_INDEX.md` or a capability-specific index.
- Always include source-of-truth files before derived files.
- Always include applicable ADRs, RFCs and quality gates.
