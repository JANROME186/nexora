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
  title: Audit Management API
  version: 0.1.0
  description: API for managing internal, regulatory, and supplier quality audits.
paths:
  /api/quality/audits:
    get:
      summary: List audit schedules
      operationId: listAuditSchedules
      parameters:
      - name: category
        in: query
        required: false
        schema:
          type: string
      - name: status
        in: query
        required: false
        schema:
          type: string
      responses:
        '200':
          description: List of audits.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/AuditScheduleResponse'
    post:
      summary: Create an audit schedule
      operationId: createAuditSchedule
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateAuditScheduleRequest'
      responses:
        '201':
          description: Audit created.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuditScheduleResponse'
  /api/quality/audits/{id}:
    get:
      summary: Get audit details
      operationId: getAuditSchedule
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      responses:
        '200':
          description: Audit details.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuditScheduleResponse'
  /api/quality/audits/{id}/findings:
    post:
      summary: Log a finding for an audit
      operationId: recordAuditFinding
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
              $ref: '#/components/schemas/RecordAuditFindingRequest'
      responses:
        '201':
          description: Finding logged.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuditScheduleResponse'
  /api/quality/audits/{id}/close:
    post:
      summary: Close an audit cycle
      operationId: closeAuditSchedule
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
          format: uuid
      responses:
        '200':
          description: Audit closed.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuditScheduleResponse'
components:
  schemas:
    CreateAuditScheduleRequest:
      type: object
      required:
      - title
      - category
      - leadAuditorId
      - plannedStartDate
      - plannedEndDate
      properties:
        title:
          type: string
        category:
          type: string
        standardReference:
          type: string
        leadAuditorId:
          type: string
          format: uuid
        plannedStartDate:
          type: string
          format: date
        plannedEndDate:
          type: string
          format: date
    RecordAuditFindingRequest:
      type: object
      required:
      - severity
      - observation
      properties:
        clauseReference:
          type: string
        severity:
          type: string
          enum:
          - critical
          - major
          - minor
          - opportunity_for_improvement
        observation:
          type: string
        evidenceReference:
          type: string
    AuditScheduleResponse:
      type: object
      properties:
        auditId:
          type: string
          format: uuid
        auditCode:
          type: string
        title:
          type: string
        category:
          type: string
        status:
          type: string
        leadAuditorId:
          type: string
          format: uuid
        plannedStartDate:
          type: string
          format: date
        plannedEndDate:
          type: string
          format: date
```
