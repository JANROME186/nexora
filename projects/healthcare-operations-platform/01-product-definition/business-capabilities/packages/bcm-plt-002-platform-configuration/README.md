# BCM-PLT-002: Platform Configuration Capability Package

## Overview
The Platform Configuration capability package governs global platform parameters, system environment profiles (dev, staging, prod), tenant feature flag toggles, dynamic PII data masking policy overrides (addressing TD-BE-008), and maintenance window orchestration for Healthcare Operations Platform.

## Bounded Context & Primary Aggregate
- **Bounded Context**: `platform-operations`
- **Primary Aggregate**: `PlatformConfiguration`

## Key Specifications
- **Feature Flags**: Dynamic evaluation by tenant, rollout percentage, and targeted rules.
- **PII Data Masking**: Tenant-configurable masking policy for document and credential fields (TD-BE-008).
- **Security**: AES-256 GCM encryption for sensitive configuration keys.
