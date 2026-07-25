---
id: HOP-PROC-BCM-PLT-005
format: markdown_structured_payload
type: processes
name: API Management Processes
version: 0.1.0
status: modeled
---

# Api Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PLT-005
  type: processes
  name: API Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-005
actors:
- id: platform-administrator
  actor_ref: ACT-001
  name: Platform Super Administrator
  source: ACM-001
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
- id: integration-partner
  actor_ref: ACT-014
  name: Integration Partner System
  source: ACM-001
  note: Consumes partner-classified operations using an issued PartnerApiKey; does
    not perform administrative commands.
processes:
- id: PRC-APIM-005-01
  name: Classify API operation
  actor: platform-administrator
  trigger: A capability publishes a new or changed API operation that needs an external-facing
    classification.
  commands:
  - ClassifyApiOperation
  preconditions:
  - Actor holds api.classification.manage scope.
  steps:
  - Create or update the operation's ApiSurfaceRegistration entry.
  - Publish ApiSurfaceClassified.
  outcome: ApiSurfaceClassified
  rules:
  - RN-001
  - RN-006
- id: PRC-APIM-005-02
  name: Issue partner API key
  actor: tenant-administrator
  trigger: A partner integration needs credentialed access to partner-classified operations.
  commands:
  - IssuePartnerApiKey
  preconditions:
  - Actor holds api.partnerkey.manage scope.
  - Requested scopes are all covered by partner-classified operations.
  steps:
  - Create PartnerApiKey in active status with the granted scopes and a rate-limit
    policy reference.
  - Publish PartnerApiKeyIssued.
  outcome: PartnerApiKeyIssued
  rules:
  - RN-002
  - RN-006
- id: PRC-APIM-005-03
  name: Revoke partner API key
  actor: tenant-administrator
  trigger: A partner integration is decommissioned or a credential is compromised.
  commands:
  - RevokePartnerApiKey
  preconditions:
  - PartnerApiKey exists in active status.
  steps:
  - Transition PartnerApiKey to revoked.
  - Publish PartnerApiKeyRevoked.
  outcome: PartnerApiKeyRevoked
  rules:
  - RN-002
  - RN-005
- id: PRC-APIM-005-04
  name: Schedule API deprecation
  actor: platform-administrator
  trigger: A published public or partner operation requires a breaking change.
  commands:
  - ScheduleApiDeprecation
  preconditions:
  - A deprecation window and migration note are documented.
  steps:
  - Transition ApiSurfaceRegistration to deprecation_scheduled with the window and
    note.
  - Publish ApiDeprecationScheduled.
  outcome: ApiDeprecationScheduled
  rules:
  - RN-003
  - RN-005
commands:
- name: ClassifyApiOperation
  generatable: false
  custom_reason: Classification defaulting and publish-gating logic.
- name: IssuePartnerApiKey
  generatable: false
  custom_reason: Scope-coverage validation against classified partner operations.
- name: RevokePartnerApiKey
  generatable: true
- name: ScheduleApiDeprecation
  generatable: false
  custom_reason: Deprecation-window and migration-note completeness checks.
```
