# OpenAPI Scope

## Imaging API

Initial contract path:

`05-contracts/contracts/openapi/imaging/imaging.yaml`

## Endpoint Groups

- `/imaging/studies`
- `/imaging/appointments`
- `/imaging/worklists/technician`
- `/imaging/worklists/radiologist`
- `/imaging/dicom/studies`
- `/imaging/dicom/reconciliation-tasks`
- `/imaging/reports`
- `/imaging/viewer-links`
- `/imaging/modalities`
- `/imaging/rooms`

## Contract Rules

- OpenAPI is the source of truth for public and internal HTTP contracts.
- DICOM binary transport details are documented in integration architecture and not fully represented as REST-only workflows.
- Viewer URLs must not expose direct object storage paths.
- All endpoints must include tenant/organization context and authorization scopes.
- Report release/amendment endpoints must expose idempotency and audit metadata.
