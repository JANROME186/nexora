---
id: HOP-PERM-BCM-IMG-005
format: markdown_structured_payload
type: permissions
name: PACS Integration Permissions
version: 1.0.0
status: modeled
---

# PACS Integration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-IMG-005
  type: permissions
  name: PACS Integration Permissions
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-005
permissions:
  - id: PERM_BCM_IMG_005_READ
    name: Read PACS Integration
    roles: [RadiologyTechnician, Radiologist, MedicalDirector, SystemAdmin]
  - id: PERM_BCM_IMG_005_WRITE
    name: Write PACS Integration
    roles: [RadiologyTechnician, Radiologist, SystemAdmin]
```
