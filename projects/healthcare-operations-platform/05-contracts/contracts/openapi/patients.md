---
id: patients
format: markdown_structured_payload
---

# Patients

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Patients API
  version: 0.1.0
  description: Initial contract for patient management.
servers:
- url: https://api.nexora.local/v1
paths:
  /patients:
    get:
      summary: List patients
      operationId: listPatients
      tags:
      - Patients
      responses:
        '200':
          description: Patient list
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/Patient'
    post:
      summary: Create patient
      operationId: createPatient
      tags:
      - Patients
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreatePatientRequest'
      responses:
        '201':
          description: Patient created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Patient'
components:
  schemas:
    Patient:
      type: object
      required:
      - id
      - firstName
      - lastName
      properties:
        id:
          type: string
          format: uuid
        firstName:
          type: string
        lastName:
          type: string
        birthDate:
          type: string
          format: date
    CreatePatientRequest:
      type: object
      required:
      - firstName
      - lastName
      properties:
        firstName:
          type: string
        lastName:
          type: string
        birthDate:
          type: string
          format: date
```
