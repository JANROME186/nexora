# BCM-ORG-001: Tenant Management Capability Package

## Overview
The Tenant Management capability package governs multi-tenant lifecycle management, tenant provisioning, configuration overrides, resource quota enforcement, database/schema isolation policies (including PostgreSQL native Row-Level Security parameters per TD-DB-004), and SaaS subscription status binding for the Healthcare Operations Platform.

## Bounded Context & Primary Aggregate
- **Bounded Context**: `organization-management`
- **Primary Aggregate**: `AGG-017 Tenant` (`TenantRoot`)

## Key Specifications
- **Multi-Tenancy**: Supports schema-per-tenant or single-schema with Row-Level Security (RLS).
- **Tenant Lifecycle**: `PENDING_PROVISIONING`, `ACTIVE`, `SUSPENDED`, `ARCHIVED`.
- **Quota Enforcement**: Controls max branches, active users, monthly orders, storage, and API rate limits.
- **Traceability & Governance**: Fully traceable to platform requirements and security policies.
