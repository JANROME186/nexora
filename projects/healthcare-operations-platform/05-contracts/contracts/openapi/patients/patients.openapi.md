---
id: patients.openapi
format: markdown_structured_payload
---

# Patients.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Patients API
  version: 1.0.0
  description: Contract-first API for CAP-001 Patient Management.
servers:
- url: https://api.nexora.example.com
  description: Production placeholder
- url: http://localhost:3000
  description: Local development
security:
- bearerAuth: []
tags:
- name: Patients
paths:
  /v1/patients:
    post:
      tags:
      - Patients
      summary: Register a patient
      operationId: registerPatient
      security:
      - bearerAuth:
        - patients:create
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterPatientRequest'
      responses:
        '201':
          description: Patient registered
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PatientResponse'
        '409':
          description: Possible duplicate patient detected
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DuplicatePatientResponse'
    get:
      tags:
      - Patients
      summary: Search patients
      operationId: searchPatients
      security:
      - bearerAuth:
        - patients:read
      parameters:
      - name: query
        in: query
        schema:
          type: string
      - name: page
        in: query
        schema:
          type: integer
          minimum: 1
          default: 1
      - name: pageSize
        in: query
        schema:
          type: integer
          minimum: 1
          maximum: 100
          default: 20
      responses:
        '200':
          description: Paginated patients
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PatientPage'
  /v1/patients/{patientId}:
    get:
      tags:
      - Patients
      summary: Get patient profile
      operationId: getPatientById
      security:
      - bearerAuth:
        - patients:read
      parameters:
      - $ref: '#/components/parameters/PatientId'
      responses:
        '200':
          description: Patient profile
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PatientResponse'
    patch:
      tags:
      - Patients
      summary: Update patient profile
      operationId: updatePatient
      security:
      - bearerAuth:
        - patients:update
      parameters:
      - $ref: '#/components/parameters/PatientId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdatePatientRequest'
      responses:
        '200':
          description: Patient updated
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PatientResponse'
  /v1/patients/{patientId}/consents:
    post:
      tags:
      - Patients
      summary: Record patient consent
      operationId: recordPatientConsent
      security:
      - bearerAuth:
        - patients:manage-consent
      parameters:
      - $ref: '#/components/parameters/PatientId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RecordConsentRequest'
      responses:
        '201':
          description: Consent recorded
  /v1/patients/{patientId}/guardians:
    post:
      tags:
      - Patients
      summary: Add patient guardian
      operationId: addPatientGuardian
      security:
      - bearerAuth:
        - patients:manage-guardian
      parameters:
      - $ref: '#/components/parameters/PatientId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AddGuardianRequest'
      responses:
        '201':
          description: Guardian added
  /v1/patients/{patientId}/deactivate:
    post:
      tags:
      - Patients
      summary: Deactivate patient
      operationId: deactivatePatient
      security:
      - bearerAuth:
        - patients:deactivate
      parameters:
      - $ref: '#/components/parameters/PatientId'
      responses:
        '200':
          description: Patient deactivated
  /v1/patients/{patientId}/reactivate:
    post:
      tags:
      - Patients
      summary: Reactivate patient
      operationId: reactivatePatient
      security:
      - bearerAuth:
        - patients:reactivate
      parameters:
      - $ref: '#/components/parameters/PatientId'
      responses:
        '200':
          description: Patient reactivated
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  parameters:
    PatientId:
      name: patientId
      in: path
      required: true
      schema:
        type: string
        format: uuid
  schemas:
    RegisterPatientRequest:
      type: object
      required:
      - fullName
      properties:
        fullName:
          type: string
          minLength: 1
        birthDate:
          type: string
          format: date
        sexAtBirth:
          type: string
          enum:
          - female
          - male
          - intersex
          - unknown
        identifiers:
          type: array
          items:
            $ref: '#/components/schemas/PatientIdentifier'
        contactPoints:
          type: array
          items:
            $ref: '#/components/schemas/ContactPoint'
        guardians:
          type: array
          items:
            $ref: '#/components/schemas/Guardian'
    UpdatePatientRequest:
      type: object
      properties:
        fullName:
          type: string
        birthDate:
          type: string
          format: date
        sexAtBirth:
          type: string
          enum:
          - female
          - male
          - intersex
          - unknown
        contactPoints:
          type: array
          items:
            $ref: '#/components/schemas/ContactPoint'
    PatientResponse:
      type: object
      required:
      - id
      - fullName
      - status
      properties:
        id:
          type: string
          format: uuid
        fullName:
          type: string
        birthDate:
          type: string
          format: date
        sexAtBirth:
          type: string
        status:
          type: string
          enum:
          - draft
          - active
          - inactive
          - blocked
          - merged
        contactPoints:
          type: array
          items:
            $ref: '#/components/schemas/ContactPoint'
    PatientPage:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/PatientResponse'
        page:
          type: integer
        pageSize:
          type: integer
        total:
          type: integer
    PatientIdentifier:
      type: object
      properties:
        type:
          type: string
        value:
          type: string
        country:
          type: string
    ContactPoint:
      type: object
      properties:
        type:
          type: string
          enum:
          - email
          - phone
          - whatsapp
        value:
          type: string
        primary:
          type: boolean
    Guardian:
      type: object
      required:
      - fullName
      - relationship
      properties:
        fullName:
          type: string
        relationship:
          type: string
        contactPoint:
          $ref: '#/components/schemas/ContactPoint'
    RecordConsentRequest:
      type: object
      required:
      - scope
      - version
      - accepted
      properties:
        scope:
          type: string
        version:
          type: string
        accepted:
          type: boolean
    AddGuardianRequest:
      $ref: '#/components/schemas/Guardian'
    DuplicatePatientResponse:
      type: object
      properties:
        message:
          type: string
        candidates:
          type: array
          items:
            type: object
            properties:
              patientId:
                type: string
                format: uuid
              matchScore:
                type: number
              reason:
                type: string
```
