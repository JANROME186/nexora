---
id: HOP-CAP-PKG-BCM-PLT-004
format: markdown_structured_payload
type: capability-package
name: Integration Management Capability Package
version: 0.1.0
status: modeled
---

# Integration Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-PLT-004
  type: capability-package
  name: Integration Management Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-18
  roadmap_group: MVP-MOD-008
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-PLT-004
  name:
    en: Integration Management
    es: Integraciones
  domain: DOM-10 Platform
  priority: Critical
  roadmap: MVP2
  dependency_profile: platform_extension
  bounded_context: integration-interoperability
  primary_aggregate: IntegrationEndpoint (new platform aggregate owned by this capability)
  process_ref: HRP-001-P08
scope:
  summary: 'Anti-corruption boundary for external system messages (HL7, ASTM, FHIR,
    DICOM and other provider protocols) entering HOP through registered integration
    endpoints. Normalizes external payloads into canonical, domain-safe messages before
    any domain module reads them, mirroring the FiscalAdapterPort/DocumentStoragePort/NotificationProviderPort
    provider-agnostic adapter pattern established in MVP-MOD-005/MVP-MOD-007. Never
    mutates a business aggregate directly; normalized messages reach domains only
    through their own domain commands.

    '
  in_scope:
  - 'IntegrationEndpoint aggregate: registered external system/device connection,
    protocol, status.'
  - 'IntegrationAdapterPort: provider-agnostic inbound/outbound message boundary with
    a local, self-hostable default adapter.'
  - Canonical message envelope (ExternalMessageEnvelope), normalization into NormalizedClinicalMessage,
    and IntegrationAcknowledgement for delivery confirmation.
  - Idempotent message processing keyed by a stable external message identifier.
  - Bounded, auditable retry policy for failed inbound or outbound message delivery.
  - Canonical error codes for normalization and delivery failures.
  out_of_scope:
  - Any business aggregate mutation (DiagnosticOrder, LaboratoryResult, Patient, Invoice)
    — normalized messages are handed to the owning domain's own commands, never written
    directly.
  - API surface classification and partner API contract governance (BCM-PLT-005).
  - Bulk file-based migration ingestion (BCM-PLT-010).
  - Protocol-specific parsing library selection, which is a custom implementation
    and stack decision evaluated by MVP-MOD-008-BE-001 (local deterministic adapter
    remains the default; real HL7v2/FHIR/ASTM/DICOM parser adoption remains open,
    see TD-BE-013's analogous migration scope note) and generation-plan.md.
roadmap:
  module: MVP-MOD-008
  release: REL-001
  package_status: module_closed
  next_backlog_item: COM-MOD-009-BE-001
  paused_functional_backlog_item: null
dependencies:
  required_capabilities:
  - BCM-PLT-001
  - BCM-PLT-006
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-005
  downstream_capabilities:
  - BCM-IMG-004
  - BCM-AI-007
  - BCM-LAB-006
  upstream_contexts:
  - integration-interoperability
  - identity-access
  - audit-compliance
  - observability
product_surfaces:
  backend: required
  employee_portal: admin_required
  patient_portal: not_required
  doctor_portal: not_required
  mobile_app: not_required
required_artifacts:
- capability-package.md
- business-model.md
- business-rules.md
- processes.md
- events.md
- openapi-source.md
- permissions.md
- ui-model.md
- mobile-model.md
- test-model.md
- observability-model.md
- generation-plan.md
- traceability.md
- README.md
```
