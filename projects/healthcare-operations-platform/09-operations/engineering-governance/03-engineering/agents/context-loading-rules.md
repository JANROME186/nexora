---
id: AGENT-CONTEXT-LOADING-RULES-001
format: markdown_structured_payload
type: agent-context-policy
version: 0.34.0
status: approved
---

# Agent Context Loading Rules 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: AGENT-CONTEXT-LOADING-RULES-001
type: agent-context-policy
owner: Engineering Governance
status: approved
version: 0.34.0
rules:
- Load PROJECT_MANIFEST first.
- Load SOURCE_OF_TRUTH second.
- Load bounded-contexts.md before any capability artifact.
- Do not load deprecated artifacts unless explicitly performing cleanup.
- Prefer YAML source artifacts over generated Markdown.
- For implementation tasks, load only the target context, related shared kernel, contracts,
  and direct dependencies.
- For migration tasks, load UIM, CDM, mapping templates, validation rules, and target
  aggregates.
```
