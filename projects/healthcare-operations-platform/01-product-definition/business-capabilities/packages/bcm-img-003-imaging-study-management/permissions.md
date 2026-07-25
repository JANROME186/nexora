---
id: HOP-PERM-BCM-IMG-003
format: markdown_structured_payload
type: permissions
name: Imaging Study Management Permissions
version: 1.0.0
status: modeled
---

# Imaging Study Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-003
  type: permissions
  name: Imaging Study Management Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-003
permissions:
  - id: PERM_BCM_IMG_003_READ
    name: Read Imaging Study Management
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_003_WRITE
    name: Write Imaging Study Management
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
