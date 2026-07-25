# Licensing Engine Architecture

**Artifact ID:** LIC-001
**Status:** Draft
**Version:** 0.22.0

## Purpose

The Licensing Engine defines what each tenant is allowed to use based on commercial plans, purchased modules, country packs, healthcare packs, usage limits and add-ons.

The licensing model must be configurable and independent from the application code.

## Concepts

| Concept | Description |
|---|---|
| Plan | Commercial package assigned to a tenant. |
| Entitlement | Permission to use a product capability. |
| Limit | Quantitative restriction such as users, branches, storage or AI credits. |
| Add-on | Optional paid capability. |
| Trial | Temporary entitlement with expiration. |
| Country Pack | Country-specific regulation, billing or localization package. |
| Healthcare Pack | Vertical package such as Lab, Imaging, Pathology or Blood Bank. |

## Initial Plans

### Starter

For small laboratories or early adopters.

- Limited branches.
- Limited users.
- Core patient, order, sample, result and cashier capabilities.
- No advanced AI.
- No PACS.
- Basic reports.

### Professional

For growing laboratories.

- Multi-branch support.
- Portal for doctors and patients.
- Inventory.
- Billing.
- Basic AI features.
- Integrations with selected devices.

### Enterprise

For large diagnostic organizations.

- Unlimited or negotiated limits.
- Advanced AI.
- HL7/FHIR/DICOM.
- Marketplace access.
- Country packs.
- Advanced analytics.
- Dedicated support.

## Runtime Evaluation

Every protected capability must be evaluated through an entitlement check:

```text
User Request
  -> Authentication
  -> Tenant Resolution
  -> License Resolution
  -> Feature Flag Resolution
  -> Authorization
  -> Capability Execution
```

## Anti-Pattern

Do not hard-code plan checks inside controllers, UI components or domain logic.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: LIC-001
name: Licensing Engine Architecture
type: licensing-architecture
version: 0.22.0
status: draft
plans:
- id: plan-starter
  name: Starter
  target: Small laboratories
  capabilities:
  - patient-management.core
  - order-management.core
  - sample-management.core
  - results.core
  - cashier.core
  excluded:
  - ai.advanced
  - pacs.core
  - hl7.integration
- id: plan-professional
  name: Professional
  target: Growing laboratories
  capabilities:
  - patient-management.core
  - order-management.core
  - doctor-portal.core
  - patient-portal.core
  - inventory.core
  - billing.core
  - ai.basic
  - device-integrations.basic
- id: plan-enterprise
  name: Enterprise
  target: Large diagnostic organizations
  capabilities:
  - '*'
runtime_flow:
- authentication
- tenant-resolution
- license-resolution
- feature-flag-resolution
- authorization
- capability-execution
rules:
- Do not hard-code plan checks inside controllers, UI components or domain logic.
- Entitlements must be resolved through a dedicated policy service.
```
