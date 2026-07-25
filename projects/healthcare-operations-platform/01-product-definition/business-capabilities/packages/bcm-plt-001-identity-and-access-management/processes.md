---
id: HOP-PROC-BCM-PLT-001
format: markdown_structured_payload
type: processes
name: Identity and Access Management Business Processes
version: 0.1.0
status: modeled
---

# Identity And Access Management Business Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PLT-001
  type: processes
  name: Identity and Access Management Business Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-001
processes:
- id: PROC-IAM-001
  name: User Authentication (Login)
  description: Verifies user credentials and generates a secure session token.
  trigger: User enters credentials on login page.
  actors:
  - Patient
  - Doctor
  - Employee
  steps:
  - step: 1
    name: Resolve Tenant and Username
    description: Ensure the user belongs to an active tenant.
  - step: 2
    name: Check Account Lock Status
    description: Ensure user is not locked (failed attempts < 5 or lockedUntil has
      elapsed).
  - step: 3
    name: Verify Password Hash
    description: Match the password against passwordHash in database.
  - step: 4
    name: Generate Session Context
    description: Resolve assigned roles, scopes, permissions, and localization preference.
  outcomes:
  - success: SessionStarted event published, returns JWT/session token.
  - failure: UserAuthenticationFailed event published, increments failedLoginAttempts,
      returns 401.
- id: PROC-IAM-002
  name: Support-Assisted Access
  description: Allows authorized support staff to troubleshoot account issues within
    a sandboxed context.
  trigger: Support user initiates assistance session.
  actors:
  - SupportStaff
  steps:
  - step: 1
    name: Validate Consent
    description: Check that the patient or doctor has active consent allowing support
      assistance.
  - step: 2
    name: Restrict Scope
    description: Filter active permissions to exclude high-risk clinical and financial
      commands (no validation or signature allowed).
  - step: 3
    name: Write Audit Trail
    description: Publish SupportSessionAssisted event with support user's identifier,
      target user's identifier, and ticket reference.
  outcomes:
  - success: Assisted session token issued.
  - failure: Request denied with 403 Forbidden.
```
