---
id: openapi-source
format: markdown_structured_payload
---

# Openapi Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.0.3
info:
  title: External Quality Controls API
  version: 0.1.0
  description: API for managing External Quality Assessment (EQA) and Proficiency
    Testing (PT) evaluation records.
paths:
  /api/quality/external-controls:
    get:
      summary: List external quality control evaluations
      operationId: listExternalQualityEvaluations
      parameters:
      - name: programCode
        in: query
        required: false
        schema:
          type: string
      - name: rating
        in: query
        required: false
        schema:
          type: string
          enum:
          - pending_evaluation
          - acceptable
          - warning
          - unacceptable
      responses:
        '200':
          description: List of EQA evaluations.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ExternalQualityEvaluationResponse'
    post:
      summary: Register a new survey round evaluation entry
      operationId: createExternalQualityEvaluation
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateExternalQualityEvaluationRequest'
      responses:
        '201':
          description: Evaluation entry created.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExternalQualityEvaluationResponse'
  /api/quality/external-controls/{id}/score:
    put:
      summary: Record provider evaluation scoring, peer statistics, and rating
      operationId: scoreExternalQualityEvaluation
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScoreExternalQualityEvaluationRequest'
      responses:
        '200':
          description: Evaluation scored successfully.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExternalQualityEvaluationResponse'
components:
  schemas:
    CreateExternalQualityEvaluationRequest:
      type: object
      required:
      - providerName
      - programCode
      - surveyCycle
      - testDefinitionId
      - sampleCode
      - measuredValue
      properties:
        providerName:
          type: string
        programCode:
          type: string
        surveyCycle:
          type: string
        testDefinitionId:
          type: string
          format: uuid
        sampleCode:
          type: string
        measuredValue:
          type: number
          format: double
    ScoreExternalQualityEvaluationRequest:
      type: object
      required:
      - peerGroupMean
      - peerGroupSd
      properties:
        peerGroupMean:
          type: number
          format: double
        peerGroupSd:
          type: number
          format: double
        peerGroupCount:
          type: integer
        storedDocumentId:
          type: string
          format: uuid
    ExternalQualityEvaluationResponse:
      type: object
      properties:
        evaluationId:
          type: string
          format: uuid
        providerName:
          type: string
        programCode:
          type: string
        surveyCycle:
          type: string
        sampleCode:
          type: string
        measuredValue:
          type: number
        zScore:
          type: number
        performanceRating:
          type: string
        capaInvestigationId:
          type: string
          format: uuid
        evaluatedAt:
          type: string
          format: date-time
```
