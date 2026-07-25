---
id: HOP-API-BCM-IMG-004
format: markdown_structured_payload
type: openapi-source
name: DICOM Integration OpenAPI Source Specification
version: 1.0.0
status: modeled
---

# DICOM Integration OpenAPI Source Specification

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-IMG-004
  type: openapi-source
  name: DICOM Integration OpenAPI Source Specification
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
openapi:
  openapi: 3.0.3
  info:
    title: DICOM Integration API
    version: 1.0.0
  paths:
    /api/v1/imaging/bcm-img-004:
      get:
        summary: Query DICOM Integration resources
        responses:
          '200':
            description: Success
```
