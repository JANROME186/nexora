---
id: HOP-API-BCM-IMG-007
format: markdown_structured_payload
type: openapi-source
name: Radiology Signature OpenAPI Source Specification
version: 1.0.0
status: modeled
---

# Radiology Signature OpenAPI Source Specification

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-IMG-007
  type: openapi-source
  name: Radiology Signature OpenAPI Source Specification
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
openapi:
  openapi: 3.0.3
  info:
    title: Radiology Signature API
    version: 1.0.0
  paths:
    /api/v1/imaging/bcm-img-007:
      get:
        summary: Query Radiology Signature resources
        responses:
          '200':
            description: Success
```
