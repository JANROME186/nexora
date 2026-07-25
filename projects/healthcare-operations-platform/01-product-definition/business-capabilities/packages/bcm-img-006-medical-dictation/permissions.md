---
id: HOP-PERM-BCM-IMG-006
format: markdown_structured_payload
type: permissions
name: Medical Dictation Permissions
version: 1.0.0
status: modeled
---

# Medical Dictation Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-006
  type: permissions
  name: Medical Dictation Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
permissions:
  - id: PERM_BCM_IMG_006_READ
    name: Read Medical Dictation
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_006_WRITE
    name: Write Medical Dictation
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
