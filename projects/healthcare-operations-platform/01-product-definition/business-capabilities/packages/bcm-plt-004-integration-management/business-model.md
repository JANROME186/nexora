---
id: HOP-BM-BCM-PLT-004
format: markdown_structured_payload
type: business-model
name: Integration Management Business Model
version: 0.1.0
status: modeled
---

# Integration Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-PLT-004
  type: business-model
  name: Integration Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
  bounded_context: integration-interoperability
  primary_aggregate: IntegrationEndpoint
  model_kind: aggregate_owner
entities:
- id: ENT-INT-001
  name: IntegrationEndpoint
  is_aggregate_root: true
  description: 'A registered external connection point (an HL7/ASTM interface, a FHIR
    client, a DICOM modality, or another provider-agnostic external system) through
    which messages enter or leave HOP. Owns only connection, protocol and message-log
    metadata; the business meaning of a message belongs to the domain module that
    ultimately consumes it.

    '
  fields:
  - name: endpointId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: endpointName
    type: string
    required: true
  - name: protocol
    type: enum
    values:
    - hl7v2
    - astm
    - fhir
    - dicom
    - generic_rest_webhook
    required: true
  - name: direction
    type: enum
    values:
    - inbound
    - outbound
    - bidirectional
    required: true
  - name: status
    type: enum
    values:
    - registered
    - active
    - suspended
    - retired
    required: true
  - name: audit
    type: AuditMetadata
    required: true
- id: ENT-INT-002
  name: IntegrationMessageRecord
  is_aggregate_root: false
  parent: IntegrationEndpoint
  description: 'One inbound or outbound message instance processed through an endpoint,
    kept for audit, idempotency and retry purposes. Never stores interpreted business
    content beyond the canonical envelope and normalization outcome.

    '
  fields:
  - name: messageId
    type: uuid
    required: true
    identifier: true
  - name: endpointId
    type: uuid
    required: true
  - name: externalMessageId
    type: string
    required: true
    description: Stable identifier supplied by or derived from the external system,
      used for idempotency.
  - name: envelope
    type: ExternalMessageEnvelope
    required: true
  - name: normalizationStatus
    type: enum
    values:
    - received
    - normalized
    - normalization_failed
    - acknowledged
    - retrying
    - dead_lettered
    required: true
  - name: retryCount
    type: integer
    required: true
    default: 0
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-INT-001
  name: ExternalMessageEnvelope
  description: 'Canonical, protocol-agnostic wrapper around a raw external payload
    before domain-safe normalization. Published-language type shared with all-core-contexts
    per context-map.md REL-CTX-011.

    '
  fields:
  - name: sourceProtocol
    type: enum
    values:
    - hl7v2
    - astm
    - fhir
    - dicom
    - generic_rest_webhook
    required: true
  - name: rawPayloadReference
    type: string
    required: true
    description: Opaque reference to the stored raw payload; large payloads are not
      inlined.
  - name: receivedAt
    type: datetime
    required: true
- id: VO-INT-002
  name: NormalizedClinicalMessage
  description: 'Canonical, domain-safe record produced after normalization; the only
    form a domain module may read. Published-language type shared with all-core-contexts
    per context-map.md REL-CTX-011.

    '
  fields:
  - name: messageType
    type: string
    required: true
  - name: canonicalFields
    type: map
    required: true
  - name: targetBoundedContext
    type: string
    required: true
- id: VO-INT-003
  name: IntegrationAcknowledgement
  description: 'Delivery/processing confirmation returned to the external system.
    Published-language type shared with all-core-contexts per context-map.md REL-CTX-011.

    '
  fields:
  - name: externalMessageId
    type: string
    required: true
  - name: status
    type: enum
    values:
    - accepted
    - rejected
    - retrying
    required: true
  - name: canonicalErrorCode
    type: string
    required: false
ports:
- id: PORT-INT-001
  name: IntegrationAdapterPort
  description: 'Provider-agnostic inbound/outbound message boundary, mirroring the
    FiscalAdapterPort (MVP-MOD-005), NotificationProviderPort and DocumentStoragePort
    (MVP-MOD-007) pattern. Callers depend only on this interface; protocol-specific
    parsing is a replaceable adapter.

    '
  operations:
  - receiveMessage(rawPayload, protocolHint) -> ExternalMessageEnvelope
  - normalizeMessage(ExternalMessageEnvelope) -> NormalizedClinicalMessage
  - acknowledgeMessage(externalMessageId, status) -> IntegrationAcknowledgement
  default_adapter: local_deterministic_passthrough_adapter
  adapter_reference: 'Local, self-hostable adapter suitable for on-premises deployment
    and contract testing; open-source protocol-specific adapters (e.g. HAPI FHIR,
    an open-source HL7v2 parser) may be added later without changing this port. Concrete
    parser-library selection is a custom implementation decision deferred to MVP-MOD-008-BE-001.

    '
invariants:
- id: INV-INT-001
  statement: No domain module may parse a raw external protocol payload directly;
    every inbound message reaches a domain only as a NormalizedClinicalMessage produced
    by IntegrationAdapterPort.
- id: INV-INT-002
  statement: An IntegrationMessageRecord's externalMessageId must be unique per endpoint;
    reprocessing the same externalMessageId must not create a duplicate domain effect.
- id: INV-INT-003
  statement: This capability never issues a command against a business aggregate (DiagnosticOrder,
    LaboratoryResult, Patient, Invoice); normalized messages are handed to the owning
    domain's own commands.
- id: INV-INT-004
  statement: A retired IntegrationEndpoint cannot receive or send new messages; only
    historical IntegrationMessageRecord entries remain queryable.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-PLT-005 API Management (governs the outbound partner/public API surface that
    may reuse IntegrationAdapterPort acknowledgements)
  - BCM-PLT-010 Open Data Ingestion and Migration (bulk file-based migration; distinct
    from this capability's message-oriented integration)
- context_map_published_language:
  - ExternalMessageEnvelope
  - NormalizedClinicalMessage
  - IntegrationAcknowledgement
- related_future_consumers:
  - BCM-IMG-004 (imaging DICOM/PACS integration, MVP-MOD-014)
  - BCM-AI-007 (AI platform integration, COM-MOD-015)
  - BCM-LAB-006 (laboratory device result messages, read-only consumer via normalized
    messages)
```
