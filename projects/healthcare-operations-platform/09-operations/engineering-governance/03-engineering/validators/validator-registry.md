---
id: VALIDATOR-REGISTRY-001
format: markdown_structured_payload
type: validator-registry
version: 0.34.0
status: draft
---

# Validator Registry 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: VALIDATOR-REGISTRY-001
type: validator-registry
owner: Engineering Governance
status: draft
version: 0.34.0
validators:
- id: VAL-METADATA
  name: Metadata Validator
  checks:
  - id
  - type
  - owner
  - status
  - version
- id: VAL-DEPRECATED
  name: Deprecated Concept Validator
  checks:
  - no deprecated concepts in SOURCE artifacts
- id: VAL-DOMAIN-OWNERSHIP
  name: Domain Ownership Validator
  checks:
  - single owner per aggregate
  - external mutation not allowed
- id: VAL-UIM
  name: Migration Model Validator
  checks:
  - supported source declared
  - mapping template required
  - reconciliation required
```
