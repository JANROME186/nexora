---
id: HOP-PERM-BCM-IMG-002
format: markdown_structured_payload
type: permissions
name: Imaging Reception Permissions
version: 1.0.0
status: modeled
---

# Imaging Reception Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-002
  type: permissions
  name: Imaging Reception Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
permissions:
  - id: PERM_BCM_IMG_002_READ
    name: Read Imaging Reception
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_002_WRITE
    name: Write Imaging Reception
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
