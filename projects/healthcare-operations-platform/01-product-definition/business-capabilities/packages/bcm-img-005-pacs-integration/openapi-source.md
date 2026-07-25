---
id: HOP-API-BCM-IMG-005
format: markdown_structured_payload
type: openapi-source
name: PACS Integration OpenAPI Source Specification
version: 1.0.0
status: modeled
---

# PACS Integration OpenAPI Source Specification

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-IMG-005
  type: openapi-source
  name: PACS Integration OpenAPI Source Specification
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-005
openapi:
  openapi: 3.0.3
  info:
    title: PACS Integration API
    version: 1.0.0
  paths:
    /api/v1/imaging/bcm-img-005:
      get:
        summary: Query PACS Integration resources
        responses:
          '200':
            description: Success
```
