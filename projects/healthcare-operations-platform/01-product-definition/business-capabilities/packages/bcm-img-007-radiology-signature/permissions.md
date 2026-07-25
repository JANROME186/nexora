---
id: HOP-PERM-BCM-IMG-007
format: markdown_structured_payload
type: permissions
name: Radiology Signature Permissions
version: 1.0.0
status: modeled
---

# Radiology Signature Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-007
  type: permissions
  name: Radiology Signature Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
permissions:
  - id: PERM_BCM_IMG_007_READ
    name: Read Radiology Signature
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_007_WRITE
    name: Write Radiology Signature
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
