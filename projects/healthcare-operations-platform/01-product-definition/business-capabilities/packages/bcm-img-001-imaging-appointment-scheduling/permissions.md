---
id: HOP-PERM-BCM-IMG-001
format: markdown_structured_payload
type: permissions
name: Imaging Appointment Scheduling Permissions
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-001
  type: permissions
  name: Imaging Appointment Scheduling Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
permissions:
  - id: PERM_BCM_IMG_001_READ
    name: Read Imaging Appointment Scheduling
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_001_WRITE
    name: Write Imaging Appointment Scheduling
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
