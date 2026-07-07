# ADR-009: Integration and Interoperability Through Adapters

**Status:** Approved  
**Date:** 2026-07-07  
**Version:** 0.20.0  

## Context

Nexora must integrate with laboratory devices, imaging systems, external LIS/RIS/HIS platforms, billing systems, tax services, AI providers and partner applications.

Direct coupling to protocols or vendors would make the platform hard to maintain and difficult to deploy across countries, clouds and infrastructure profiles.

## Decision

Nexora will implement integrations through protocol adapters, an integration gateway and canonical message models. External protocols such as ASTM, HL7, FHIR, DICOM, REST, webhooks, SFTP, CSV, XML and JSON must not leak into the domain model.

## Consequences

- The platform remains vendor and protocol agnostic.
- Integrations can evolve independently.
- Device and country-specific behavior can live in connectors and packs.
- The domain remains stable.
- More upfront design is required for canonical messages and mapping.
