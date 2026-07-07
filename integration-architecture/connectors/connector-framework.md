# Connector Framework

**Artifact ID:** IIA-CONN-001  
**Version:** 0.20.0  

## Purpose

The connector framework defines how Nexora integrates with devices, external systems, tax providers, messaging systems, AI providers and country-specific services.

## Connector Types

- Device connector.
- Healthcare messaging connector.
- Public API connector.
- Webhook connector.
- File exchange connector.
- Billing/tax connector.
- Notification connector.
- AI provider connector.
- Identity provider connector.

## Required Connector Metadata

Every connector must define:

```yaml
id: CONN-XXX
name: Connector Name
type: device|healthcare-messaging|public-api|webhook|file-exchange|billing|notification|ai|identity
version: 1.0.0
status: Draft|Approved|Deprecated
owner: Integration Architecture
protocols: []
inputMessages: []
outputMessages: []
canonicalMessages: []
security:
  authentication: none|api-key|oauth2|mutual-tls|basic|custom
  encryptionRequired: true
observability:
  correlationIdRequired: true
  auditRequired: true
failureHandling:
  retry: true
  deadLetter: true
  reconciliation: true
```

## Connector Lifecycle

```text
Proposed → Designed → Implemented → Certified → Active → Deprecated → Removed
```

## Connector Isolation

Connectors must not contain domain business rules. Their responsibility is protocol communication, validation, transformation and delivery.
