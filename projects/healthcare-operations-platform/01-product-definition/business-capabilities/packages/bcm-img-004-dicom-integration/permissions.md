---
id: HOP-PERM-BCM-IMG-004
format: markdown_structured_payload
type: permissions
name: DICOM Integration Permissions
version: 1.0.0
status: modeled
---

# DICOM Integration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-004
  type: permissions
  name: DICOM Integration Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
permissions:
  - id: PERM_BCM_IMG_004_READ
    name: Read DICOM Integration
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_004_WRITE
    name: Write DICOM Integration
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
