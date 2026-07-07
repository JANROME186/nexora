---
id: JRN-001
name: Patient Journey
version: 0.1.0
status: Draft
owner: Business Architecture
related_capabilities:
  - CAP-001
  - CAP-007
  - CAP-008
  - CAP-009
  - CAP-010
  - CAP-012
---

# Patient Journey

The Patient Journey describes the end-to-end experience from discovering Nexora-enabled diagnostic services to receiving results and follow-up.

```mermaid
journey
    title Patient Journey
    section Discovery
      Search laboratory or service: 3: Patient
      Review services and prices: 3: Patient
      Request quotation: 4: Patient
    section Scheduling
      Select branch and date: 4: Patient
      Receive preparation instructions: 5: Patient
    section Reception
      Confirm identity: 4: Patient, Receptionist
      Sign consent if required: 4: Patient, Receptionist
      Confirm requested studies: 4: Receptionist
    section Payment and Order
      Pay services: 3: Patient, Cashier
      Create diagnostic order: 5: Receptionist
      Generate labels: 5: Receptionist
    section Sample and Imaging
      Take sample or perform imaging: 4: Technician
      Track status: 5: Patient
    section Results
      Validate results: 5: Chemist, Radiologist
      Receive notification: 5: Patient
      View or download results: 5: Patient
    section Follow-up
      Share results with doctor: 5: Patient, Doctor
      Compare historical results: 4: Patient, Doctor
```

## User Goals

- Understand what service is needed.
- Know price, location, preparation instructions and expected delivery time.
- Avoid duplicate data entry.
- Receive clear and timely notifications.
- Access results securely from web or mobile, including low-resource devices.
- Share results with doctors when authorized.

## Business Goals

- Increase conversion from quotation to order.
- Reduce reception time.
- Reduce sample and result traceability errors.
- Improve patient satisfaction.
- Enable self-service through patient portal and mobile app.
- Preserve auditability and consent records.

## Experience Levels

| Level | Description |
|---|---|
| Core | Registration, order, payment, sample, result access. |
| Enhanced | Online scheduling, preparation instructions, notifications, historical comparison. |
| Intelligent | AI-assisted preparation guidance, result explanation and proactive follow-up, always with clinical safety controls. |
