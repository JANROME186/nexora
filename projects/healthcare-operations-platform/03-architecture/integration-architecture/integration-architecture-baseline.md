# Integration & Interoperability Architecture Baseline

**Artifact ID:** IIA-BASE-001
**Status:** Approved
**Version:** 0.20.0
**Owner:** Enterprise Architecture

## Principle

Nexora must be integration-ready from the beginning, but the core domain must never depend directly on an external protocol, device, vendor, healthcare standard or cloud service.

The official principle is:

> **Interoperability through adapters, contracts and events.**

## Core Integration Model

Nexora uses a layered integration model:

```text
External System / Device / Partner
        ↓
Protocol Adapter
        ↓
Integration Gateway
        ↓
Canonical Message Model
        ↓
Application Service
        ↓
Domain Model
        ↓
Domain Event
```

The protocol adapter translates external formats into Nexora canonical messages. The domain only receives validated business commands or application DTOs.

## Supported Integration Families

| Family | Examples | Strategy |
|---|---|---|
| Healthcare messaging | HL7 v2, FHIR | Adapter + canonical model |
| Laboratory devices | ASTM, serial, TCP/IP, CSV | Device connector framework |
| Imaging | DICOM, PACS, RIS | Imaging gateway and DICOM services |
| Public APIs | REST/OpenAPI | Contract First APIs |
| Webhooks | Order/result/payment events | Signed and versioned webhooks |
| File exchange | SFTP, CSV, XML, JSON | Managed import/export pipelines |
| Billing/tax | SAT, DIAN, SUNAT, etc. | Country Pack connectors |
| AI providers | LLM/OCR/Vision providers | Provider abstraction |

## Non-Negotiable Rules

1. No external protocol leaks into the domain layer.
2. Every public API must have an OpenAPI contract before implementation.
3. Every webhook must have an event contract and signature validation.
4. Every device connector must support traceability, retries and reconciliation.
5. Every integration must emit observability telemetry.
6. Every connector must declare ownership, version, protocol, supported events and failure behavior.
7. Every country-specific connector must live outside the core platform.

## Canonical Message Model

External payloads must be transformed into canonical messages such as:

- `PatientExternalReferenceReceived`
- `OrderExternalRequestReceived`
- `SampleDeviceResultReceived`
- `ResultExternalDeliveryRequested`
- `InvoiceExternalSubmissionRequested`
- `DICOMStudyReceived`

## Integration Gateway Responsibilities

- Authentication and authorization of external systems.
- Protocol negotiation.
- Payload validation.
- Mapping and transformation.
- Idempotency.
- Rate limiting.
- Retry and dead-letter management.
- Observability.
- Audit logging.
- Version compatibility.

## Deployment Considerations

The integration layer must support multiple deployment modes:

- Local connector simulation for development.
- Docker Compose connectors for labs with basic infrastructure.
- Docker Swarm or Kubernetes for on-premise deployments.
- Serverless functions for event-driven imports, exports and webhooks when beneficial.
- Dedicated long-running services for protocols requiring persistent connections.

## Integration Observability

Every integration must expose:

- Correlation ID.
- External reference ID.
- Protocol name and version.
- Connector version.
- Payload validation status.
- Retry count.
- Processing duration.
- Error category.
- Final disposition.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: IIA-BASE-001
name: Integration & Interoperability Architecture Baseline
version: 0.20.0
status: Approved
owner: Enterprise Architecture
principle: Interoperability through adapters, contracts and events
layers:
- external_system
- protocol_adapter
- integration_gateway
- canonical_message_model
- application_service
- domain_model
- domain_event
supportedFamilies:
  healthcareMessaging:
    standards:
    - HL7v2
    - FHIR
    strategy: adapter_canonical_model
  laboratoryDevices:
    standards:
    - ASTM
    - Serial
    - TCPIP
    - CSV
    strategy: device_connector_framework
  imaging:
    standards:
    - DICOM
    - PACS
    - RIS
    strategy: imaging_gateway
  publicApis:
    standards:
    - REST
    - OpenAPI
    strategy: contract_first
  webhooks:
    standards:
    - HTTPS
    - JSON
    strategy: signed_versioned_events
  fileExchange:
    standards:
    - SFTP
    - CSV
    - XML
    - JSON
    strategy: managed_import_export
rules:
- no_external_protocol_in_domain
- openapi_before_public_api_implementation
- signed_versioned_webhooks
- device_traceability_retry_reconciliation
- telemetry_required
- connector_metadata_required
- country_connectors_outside_core
```
