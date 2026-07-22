# BCM-PLT-007: Audit Trail Capability Package

## Overview
The Audit Trail capability package governs append-only security audit event recording, tamper-evident hash chaining, HIPAA/GDPR compliance data access logging, audit search & export APIs, and operational retention policies for Healthcare Operations Platform.

## Bounded Context & Primary Aggregate
- **Bounded Context**: `audit-compliance`
- **Primary Aggregate**: `AGG-018 AuditEvent` (`AuditEventRoot`)

## Key Specifications
- **Immutability**: Database UPDATE and DELETE operations are forbidden.
- **Hash Chaining**: SHA-256 tamper-evident linked event verification.
- **Compliance Export**: Encrypted audit exports for HIPAA and GDPR regulatory reviews.
