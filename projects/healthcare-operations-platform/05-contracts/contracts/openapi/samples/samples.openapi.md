---
id: samples.openapi
format: markdown_structured_payload
---

# Samples.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Samples API
  version: 0.28.0
  description: API contract for sample collection and traceability.
servers:
- url: /api/v1
security:
- bearerAuth: []
paths:
  /samples/{sampleId}/collect:
    post:
      operationId: registerSampleCollection
      summary: Register sample collection
      tags:
      - Samples
      parameters:
      - $ref: '#/components/parameters/SampleId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterSampleCollectionRequest'
      responses:
        '200':
          description: Sample collected
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Sample'
  /samples/{sampleId}/reject:
    post:
      operationId: rejectSample
      summary: Reject sample
      tags:
      - Samples
      parameters:
      - $ref: '#/components/parameters/SampleId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RejectSampleRequest'
      responses:
        '200':
          description: Sample rejected
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Sample'
  /samples/{sampleId}/traceability:
    get:
      operationId: getSampleTraceability
      summary: Get sample traceability
      tags:
      - Samples
      parameters:
      - $ref: '#/components/parameters/SampleId'
      responses:
        '200':
          description: Traceability events
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/SampleTraceabilityEvent'
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  parameters:
    SampleId:
      in: path
      name: sampleId
      required: true
      schema:
        type: string
  schemas:
    RegisterSampleCollectionRequest:
      type: object
      required:
      - collectedAt
      - containerType
      properties:
        collectedAt:
          type: string
          format: date-time
        containerType:
          type: string
        collectorUserId:
          type: string
        notes:
          type: string
    RejectSampleRequest:
      type: object
      required:
      - reasonCode
      properties:
        reasonCode:
          type: string
        notes:
          type: string
        recollectionRequired:
          type: boolean
    Sample:
      type: object
      properties:
        id:
          type: string
        orderId:
          type: string
        sampleCode:
          type: string
        status:
          type: string
          enum:
          - pending_collection
          - collected
          - labeled
          - received_by_lab
          - accepted
          - rejected
          - recollection_required
          - in_analysis
          - processed
        collectedAt:
          type: string
          format: date-time
          nullable: true
    SampleTraceabilityEvent:
      type: object
      properties:
        id:
          type: string
        eventType:
          type: string
        occurredAt:
          type: string
          format: date-time
        actorUserId:
          type: string
        locationId:
          type: string
```
