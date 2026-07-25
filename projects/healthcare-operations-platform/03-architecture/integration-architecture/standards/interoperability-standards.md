# Interoperability Standards

**Artifact ID:** IIA-STD-001
**Version:** 0.20.0

## Standards Baseline

Nexora will use open standards whenever possible.

| Standard | Use in Nexora | MVP Priority |
|---|---|---|
| OpenAPI | REST API contracts | High |
| OAuth2/OIDC | Identity and API security | High |
| ASTM | Lab equipment interfaces | Medium |
| HL7 v2 | Healthcare system integration | Medium |
| FHIR | Modern healthcare interoperability | Medium |
| DICOM | Imaging and PACS | MVP 3 |
| OpenTelemetry | Observability | High |
| JSON Schema | Structured artifact and payload validation | High |
| Webhooks | Event delivery to external systems | Medium |
| SFTP | Batch import/export | Medium |

## Standard Adoption Policy

- Standards must be adopted through adapter layers.
- Standards must be versioned explicitly.
- Standards must be validated with sample payloads.
- Standards must include testing fixtures.
- Standards must include documentation for implementers.
