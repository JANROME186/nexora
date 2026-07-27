---
artifact:
  id: HOP-SOT-INDEX
  type: source-registry-index
  status: active
  optimization: atomic_context
---

# Source Registry Index

Load a source shard only when a task explicitly touches that domain.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SOT-001
  type: source-of-truth-registry
  version: 1.0.0
  status: approved
groups:
  core:
    path: 08-qa/project-tracking/source-registry/source-registry-core.md
    entries: 87
    payload_hash: fbe76b97d8ca
  framework:
    path: 08-qa/project-tracking/source-registry/source-registry-framework.md
    entries: 32
    payload_hash: 3a0201cf1027
  qa:
    path: 08-qa/project-tracking/source-registry/source-registry-qa.md
    entries: 432
    payload_hash: d98f15eb46b7
  definition:
    path: 08-qa/project-tracking/source-registry/source-registry-definition.md
    entries: 84
    payload_hash: 9c6c695c092c
  implementation:
    path: 08-qa/project-tracking/source-registry/source-registry-implementation.md
    entries: 56
    payload_hash: 89629ee0980f
  delivery:
    path: 08-qa/project-tracking/source-registry/source-registry-delivery.md
    entries: 33
    payload_hash: 4894d720d311
```
