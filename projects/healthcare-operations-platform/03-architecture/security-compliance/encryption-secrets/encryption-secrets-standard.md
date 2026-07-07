---
id: ENC-ARCH-001
name: Encryption and Secrets Standard
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: security_standard
---

# Encryption and Secrets Standard

## Objective

Define minimum requirements for encryption, key management, and secrets handling.

## Encryption

- TLS for data in transit.
- Encryption at rest for databases and object storage where supported.
- Field-level encryption for selected highly sensitive fields when required by risk or regulation.
- Hashing for integrity where immutable audit evidence is needed.

## Secrets

Secrets must never be stored in source code, Markdown examples, Docker images, frontend bundles, or mobile app code.

Supported secret provider options:

- Local `.env` only for development.
- Docker secrets.
- Kubernetes secrets or sealed secrets.
- Vault-compatible providers.
- Cloud secret managers through adapters.

## Key management

Key management must be provider-agnostic and support rotation.
