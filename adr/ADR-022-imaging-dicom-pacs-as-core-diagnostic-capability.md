# ADR-022: Imaging, DICOM and PACS as a Core Diagnostic Capability

## Status

Accepted

## Context

Nexora must support clinical laboratories and imaging centers. Imaging workflows require specialized scheduling, modality management, DICOM ingestion, PACS storage references, radiology reporting, viewer access and strict auditability.

## Decision

Create CAP-011 Imaging Operations & DICOM/PACS Management as a core diagnostic capability. DICOM/PACS will be modeled through provider-agnostic storage and integration abstractions. Advanced viewer and AI features will be progressive capabilities controlled through feature flags and licensing.

## Consequences

- Imaging can evolve independently from laboratory result workflows while sharing patients, orders, physicians, billing, IAM and notifications.
- DICOM binary/object concerns remain abstracted from the domain.
- Viewer access requires dedicated security and audit controls.
