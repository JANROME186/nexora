---
id: HOP-PERM-BCM-IMG-008
format: markdown_structured_payload
type: permissions
name: Imaging Study Delivery Permissions
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-008
  type: permissions
  name: Imaging Study Delivery Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
permissions:
  - id: PERM_BCM_IMG_008_READ
    name: Read Imaging Study Delivery
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_008_WRITE
    name: Write Imaging Study Delivery
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
