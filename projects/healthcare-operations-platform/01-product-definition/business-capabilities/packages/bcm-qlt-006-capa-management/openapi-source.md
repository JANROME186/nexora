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
  title: CAPA Management API
  version: 0.1.0
  description: API for managing Corrective Action and Preventive Action (CAPA) investigations.
paths:
  /api/quality/capa:
    get:
      summary: List CAPA investigations
      operationId: listCapaInvestigations
      parameters:
      - name: status
        in: query
        required: false
        schema:
          type: string
      - name: sourceCategory
        in: query
        required: false
        schema:
          type: string
      responses:
        '200':
          description: List of CAPA investigations.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/CapaInvestigationResponse'
    post:
      summary: Initiate a new CAPA investigation
      operationId: createCapaInvestigation
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateCapaInvestigationRequest'
      responses:
        '201':
          description: CAPA investigation created.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CapaInvestigationResponse'
  /api/quality/capa/{id}:
    get:
      summary: Get CAPA investigation details
      operationId: getCapaInvestigation
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      responses:
        '200':
          description: CAPA details.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CapaInvestigationResponse'
  /api/quality/capa/{id}/rca:
    put:
      summary: Record Root Cause Analysis
      operationId: recordRootCauseAnalysis
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
              $ref: '#/components/schemas/RecordRcaRequest'
      responses:
        '200':
          description: RCA recorded.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CapaInvestigationResponse'
  /api/quality/capa/{id}/approve:
    post:
      summary: Approve CAPA action plan
      operationId: approveCapaActionPlan
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      responses:
        '200':
          description: Plan approved.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CapaInvestigationResponse'
  /api/quality/capa/{id}/verify:
    post:
      summary: Verify CAPA effectiveness and close
      operationId: verifyCapaEffectiveness
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
              $ref: '#/components/schemas/VerifyEffectivenessRequest'
      responses:
        '200':
          description: Effectiveness verified.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CapaInvestigationResponse'
components:
  schemas:
    CreateCapaInvestigationRequest:
      type: object
      required:
      - title
      - sourceCategory
      - assignedInvestigatorId
      - targetCompletionDate
      properties:
        title:
          type: string
        sourceCategory:
          type: string
        sourceReferenceId:
          type: string
        assignedInvestigatorId:
          type: string
          format: uuid
        targetCompletionDate:
          type: string
          format: date
    RecordRcaRequest:
      type: object
      required:
      - rootCauseMethodology
      - rootCauseSummary
      properties:
        rootCauseMethodology:
          type: string
        rootCauseSummary:
          type: string
    VerifyEffectivenessRequest:
      type: object
      required:
      - effectivenessRating
      - closureNotes
      properties:
        effectivenessRating:
          type: string
          enum:
          - effective
          - partially_effective
          - ineffective
        closureNotes:
          type: string
    CapaInvestigationResponse:
      type: object
      properties:
        capaId:
          type: string
          format: uuid
        capaNumber:
          type: string
        title:
          type: string
        sourceCategory:
          type: string
        status:
          type: string
        assignedInvestigatorId:
          type: string
          format: uuid
        targetCompletionDate:
          type: string
          format: date
        effectivenessRating:
          type: string
```
