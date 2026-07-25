---
id: imaging
format: markdown_structured_payload
---

# Imaging

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.0.3
info:
  title: Nexora Imaging API
  version: 0.1.0
  description: Initial contract for imaging operations, DICOM metadata, radiology
    reports and viewer access.
servers:
- url: https://api.nexora.example.com/v1
tags:
- name: Imaging Studies
- name: Imaging Appointments
- name: DICOM
- name: Reports
- name: Viewer Access
- name: Modalities
paths:
  /imaging/studies:
    get:
      tags:
      - Imaging Studies
      summary: Search imaging studies
      parameters:
      - name: patientId
        in: query
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
      - name: branchId
        in: query
        schema:
          type: string
      responses:
        '200':
          description: Imaging study list
    post:
      tags:
      - Imaging Studies
      summary: Create imaging study from diagnostic order
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateImagingStudyRequest'
      responses:
        '201':
          description: Imaging study created
  /imaging/studies/{imagingStudyId}:
    get:
      tags:
      - Imaging Studies
      summary: Get imaging study by id
      parameters:
      - name: imagingStudyId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Imaging study detail
  /imaging/appointments:
    post:
      tags:
      - Imaging Appointments
      summary: Schedule imaging appointment
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScheduleImagingAppointmentRequest'
      responses:
        '201':
          description: Appointment scheduled
  /imaging/worklists/technician:
    get:
      tags:
      - Imaging Studies
      summary: Get technician worklist
      responses:
        '200':
          description: Technician worklist
  /imaging/worklists/radiologist:
    get:
      tags:
      - Reports
      summary: Get radiologist interpretation worklist
      responses:
        '200':
          description: Radiologist worklist
  /imaging/dicom/studies:
    post:
      tags:
      - DICOM
      summary: Register received DICOM study metadata
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterDicomStudyRequest'
      responses:
        '202':
          description: DICOM study accepted for matching
    get:
      tags:
      - DICOM
      summary: Search DICOM studies
      responses:
        '200':
          description: DICOM studies
  /imaging/dicom/reconciliation-tasks:
    get:
      tags:
      - DICOM
      summary: Get DICOM reconciliation worklist
      responses:
        '200':
          description: Reconciliation tasks
  /imaging/dicom/reconciliation-tasks/{taskId}/reconcile:
    post:
      tags:
      - DICOM
      summary: Reconcile DICOM study to order
      parameters:
      - name: taskId
        in: path
        required: true
        schema:
          type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReconcileDicomStudyRequest'
      responses:
        '200':
          description: DICOM study reconciled
  /imaging/reports:
    post:
      tags:
      - Reports
      summary: Create imaging report draft
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateImagingReportRequest'
      responses:
        '201':
          description: Report draft created
  /imaging/reports/{reportId}/sign:
    post:
      tags:
      - Reports
      summary: Sign imaging report
      parameters:
      - name: reportId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Report signed
  /imaging/reports/{reportId}/release:
    post:
      tags:
      - Reports
      summary: Release imaging report
      parameters:
      - name: reportId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Report released
  /imaging/viewer-links:
    post:
      tags:
      - Viewer Access
      summary: Create secure viewer access link
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateViewerLinkRequest'
      responses:
        '201':
          description: Viewer link created
  /imaging/modalities:
    get:
      tags:
      - Modalities
      summary: Search modalities
      responses:
        '200':
          description: Modalities
    post:
      tags:
      - Modalities
      summary: Register modality
      responses:
        '201':
          description: Modality registered
components:
  schemas:
    CreateImagingStudyRequest:
      type: object
      required:
      - organizationId
      - branchId
      - orderId
      - patientId
      - serviceId
      properties:
        organizationId:
          type: string
        branchId:
          type: string
        orderId:
          type: string
        patientId:
          type: string
        serviceId:
          type: string
        priority:
          type: string
          enum:
          - routine
          - stat
          - urgent
    ScheduleImagingAppointmentRequest:
      type: object
      required:
      - imagingStudyId
      - roomId
      - modalityId
      - scheduledStart
      - scheduledEnd
      properties:
        imagingStudyId:
          type: string
        roomId:
          type: string
        modalityId:
          type: string
        scheduledStart:
          type: string
          format: date-time
        scheduledEnd:
          type: string
          format: date-time
    RegisterDicomStudyRequest:
      type: object
      required:
      - studyInstanceUid
      - storageProvider
      - objectReferences
      properties:
        studyInstanceUid:
          type: string
        accessionNumber:
          type: string
        patientExternalId:
          type: string
        storageProvider:
          type: string
        objectReferences:
          type: array
          items:
            type: string
    ReconcileDicomStudyRequest:
      type: object
      required:
      - orderId
      - patientId
      - reason
      properties:
        orderId:
          type: string
        patientId:
          type: string
        reason:
          type: string
    CreateImagingReportRequest:
      type: object
      required:
      - imagingStudyId
      - templateId
      - content
      properties:
        imagingStudyId:
          type: string
        templateId:
          type: string
        content:
          type: string
    CreateViewerLinkRequest:
      type: object
      required:
      - imagingStudyId
      - audience
      - expiresAt
      properties:
        imagingStudyId:
          type: string
        audience:
          type: string
          enum:
          - patient
          - physician
          - internal
        expiresAt:
          type: string
          format: date-time
```
