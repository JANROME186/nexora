---
id: doctors.openapi
format: markdown_structured_payload
---

# Doctors.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.0.3
info:
  title: Nexora Doctors API
  version: 0.26.0
  description: API contract for medical staff and referring physicians.
servers:
- url: /api
paths:
  /v1/doctors:
    get:
      operationId: searchDoctors
      summary: Search doctors
      parameters:
      - name: tenantId
        in: header
        required: true
        schema:
          type: string
      - name: branchId
        in: query
        schema:
          type: string
      - name: q
        in: query
        schema:
          type: string
      - name: specialtyCode
        in: query
        schema:
          type: string
      - name: status
        in: query
        schema:
          $ref: '#/components/schemas/DoctorStatus'
      responses:
        '200':
          description: Doctors found
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/Doctor'
                  page:
                    $ref: '#/components/schemas/Page'
    post:
      operationId: createDoctor
      summary: Create doctor profile
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateDoctorRequest'
      responses:
        '201':
          description: Doctor created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Doctor'
  /v1/doctors/{doctorId}:
    get:
      operationId: getDoctorById
      summary: Get doctor by id
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      responses:
        '200':
          description: Doctor found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Doctor'
    patch:
      operationId: updateDoctor
      summary: Update doctor profile
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateDoctorRequest'
      responses:
        '200':
          description: Doctor updated
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Doctor'
  /v1/doctors/{doctorId}/suspend:
    post:
      operationId: suspendDoctor
      summary: Suspend doctor
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
              - reason
              properties:
                reason:
                  type: string
      responses:
        '202':
          description: Doctor suspended
  /v1/doctors/{doctorId}/branch-assignments:
    post:
      operationId: assignDoctorToBranch
      summary: Assign doctor to branch
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
              - branchId
              properties:
                branchId:
                  type: string
                role:
                  type: string
      responses:
        '201':
          description: Assignment created
  /v1/doctors/{doctorId}/orders:
    get:
      operationId: listDoctorOrders
      summary: List orders related to doctor
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      responses:
        '200':
          description: Related orders
  /v1/doctors/{doctorId}/results:
    get:
      operationId: listDoctorResults
      summary: List results related to doctor
      parameters:
      - $ref: '#/components/parameters/DoctorId'
      responses:
        '200':
          description: Related results
components:
  parameters:
    DoctorId:
      name: doctorId
      in: path
      required: true
      schema:
        type: string
  schemas:
    DoctorStatus:
      type: string
      enum:
      - Draft
      - Active
      - Suspended
      - Inactive
    DoctorType:
      type: string
      enum:
      - Internal
      - External
      - Referring
      - Radiologist
      - Chemist
      - Pathologist
      - Specialist
    Doctor:
      type: object
      required:
      - id
      - tenantId
      - displayName
      - type
      - status
      properties:
        id:
          type: string
        tenantId:
          type: string
        displayName:
          type: string
        firstName:
          type: string
        lastName:
          type: string
        type:
          $ref: '#/components/schemas/DoctorType'
        status:
          $ref: '#/components/schemas/DoctorStatus'
        professionalLicense:
          type: string
        countryCode:
          type: string
        specialties:
          type: array
          items:
            type: string
        email:
          type: string
          format: email
        phone:
          type: string
    CreateDoctorRequest:
      type: object
      required:
      - tenantId
      - firstName
      - lastName
      - type
      properties:
        tenantId:
          type: string
        firstName:
          type: string
        lastName:
          type: string
        type:
          $ref: '#/components/schemas/DoctorType'
        professionalLicense:
          type: string
        countryCode:
          type: string
        specialties:
          type: array
          items:
            type: string
        email:
          type: string
          format: email
        phone:
          type: string
    UpdateDoctorRequest:
      type: object
      properties:
        firstName:
          type: string
        lastName:
          type: string
        professionalLicense:
          type: string
        specialties:
          type: array
          items:
            type: string
        email:
          type: string
          format: email
        phone:
          type: string
    Page:
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer
        total:
          type: integer
```
