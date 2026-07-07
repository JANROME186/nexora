---
id: SEC-VIEW-001
name: Security Context View
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: reference_view
---

# Security Context View

```mermaid
flowchart LR
  User[User / Patient / Physician / Employee] --> Channel[Web or Mobile Channel]
  Channel --> Gateway[API Gateway]
  Gateway --> Auth[Identity Provider Adapter]
  Gateway --> API[Application APIs]
  API --> Authz[Authorization Policy Engine]
  API --> Audit[Audit Service]
  API --> Data[(Tenant-aware Data Stores)]
  API --> AI[AI Capability Adapter]
  API --> Storage[Object Storage Adapter]
  Audit --> Logs[(Immutable Audit Log)]
```

## Notes

- All user actions pass through authentication and authorization.
- Backend use cases enforce permissions.
- Audit is required for sensitive operations.
- AI access is mediated by privacy and authorization controls.
