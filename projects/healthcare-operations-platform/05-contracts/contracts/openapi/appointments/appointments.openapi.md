---
id: appointments.openapi
format: markdown_structured_payload
---

# Appointments.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Appointments API
  version: 0.28.0
  description: API contract for appointments and scheduling.
servers:
- url: /api/v1
security:
- bearerAuth: []
paths:
  /appointments:
    post:
      operationId: scheduleAppointment
      summary: Schedule appointment
      tags:
      - Appointments
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScheduleAppointmentRequest'
      responses:
        '201':
          description: Appointment scheduled
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Appointment'
  /appointments/availability:
    get:
      operationId: getAppointmentAvailability
      summary: Get availability
      tags:
      - Appointments
      parameters:
      - in: query
        name: branchId
        required: true
        schema:
          type: string
      - in: query
        name: date
        required: true
        schema:
          type: string
          format: date
      - in: query
        name: testIds
        schema:
          type: array
          items:
            type: string
      responses:
        '200':
          description: Availability slots
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/AvailabilitySlot'
  /appointments/{appointmentId}/check-in:
    post:
      operationId: checkInPatient
      summary: Check-in patient
      tags:
      - Appointments
      parameters:
      - in: path
        name: appointmentId
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Patient checked in
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Appointment'
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  schemas:
    ScheduleAppointmentRequest:
      type: object
      required:
      - laboratoryId
      - branchId
      - patientId
      - startsAt
      properties:
        laboratoryId:
          type: string
        branchId:
          type: string
        patientId:
          type: string
        startsAt:
          type: string
          format: date-time
        testIds:
          type: array
          items:
            type: string
    Appointment:
      type: object
      properties:
        id:
          type: string
        branchId:
          type: string
        patientId:
          type: string
        startsAt:
          type: string
          format: date-time
        status:
          type: string
          enum:
          - requested
          - scheduled
          - confirmed
          - checked_in
          - completed
          - rescheduled
          - cancelled
          - no_show
    AvailabilitySlot:
      type: object
      properties:
        startsAt:
          type: string
          format: date-time
        endsAt:
          type: string
          format: date-time
        resourceId:
          type: string
        available:
          type: boolean
```
