---
id: API-001
format: markdown_structured_payload
type: apiContract
name: Patients API
version: 0.15.0
status: draft
---

# Patients Api

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: API-001
type: apiContract
name: Patients API
status: draft
version: 0.15.0
owner: API Architecture
openapi: 05-contracts/contracts/openapi/patients/openapi.md
style: REST
versioning: semantic-and-url-versioned
relations:
- type: implements
  target: US-001
- type: exposesEntity
  target: ENT-001
- type: emits
  target: EVT-001
- type: verifiedBy
  target: QA-001
```
