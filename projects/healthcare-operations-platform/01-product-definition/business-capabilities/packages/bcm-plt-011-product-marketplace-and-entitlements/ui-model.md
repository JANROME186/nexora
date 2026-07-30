---
id: HOP-UI-BCM-PLT-011
format: markdown_structured_payload
type: ui-model
name: Product Marketplace and Entitlements UI Model
version: 1.1.0
status: compiled
backlog_item: COM-MOD-017-DEF
---

# Product Marketplace And Entitlements Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-011
  type: ui-model
  name: Product Marketplace and Entitlements UI Model
  version: 1.1.0
  status: compiled
  backlog_item: COM-MOD-017-DEF
implementation_note: 'COM-MOD-017-QA-001 traceability check: required_permissions
  below list this package''s target-state fine-grained marketplace.<resource>:<action>
  codes (see permissions.md''s implementation_note); the compiled screens actually
  enforce access with the single coarse SCREEN_MARKETPLACE_* code named per screen
  id below, per platform convention.'
employee_portal:
  screens:
  - id: SCREEN_MARKETPLACE_CATALOG_ADMIN
    purpose: Submit, review and publish the package catalog, versions, certification
      state and publication metadata; retire superseded versions.
    enforced_permission_code: SCREEN_MARKETPLACE_PACKAGES
    required_permissions:
    - marketplace.package:submit
    - marketplace.package:certify
    - marketplace.package:publish
    - marketplace.package:retire
  - id: SCREEN_MARKETPLACE_OFFERS
    purpose: Manage commercial offers, bundles, trial policy and package availability;
      accept an offer on behalf of a tenant.
    enforced_permission_code: SCREEN_MARKETPLACE_OFFERS
    required_permissions:
    - marketplace.offer:publish
    - marketplace.offer:accept
  - id: SCREEN_TENANT_ENTITLEMENTS
    purpose: Grant, revoke, inspect and troubleshoot tenant entitlements.
    enforced_permission_code: SCREEN_MARKETPLACE_ENTITLEMENTS
    required_permissions:
    - marketplace.entitlement:grant
    - marketplace.entitlement:revoke
  - id: SCREEN_PACKAGE_INSTALLATIONS
    purpose: Install, activate, upgrade, rollback, suspend and uninstall packages
      per tenant.
    enforced_permission_code: SCREEN_MARKETPLACE_INSTALLATIONS
    required_permissions:
    - marketplace.installation:install
    - marketplace.installation:activate
    - marketplace.installation:upgrade
    - marketplace.installation:rollback
    - marketplace.installation:suspend
    - marketplace.installation:uninstall
public_website:
  surfaces:
  - id: PUBLIC_MARKETPLACE_LISTING
    purpose: Discover published public package listings and request commercial contact.
patient_portal:
  entitlement_behavior: Hide unavailable package features unless tenant entitlement
    and IAM permission allow access.
doctor_portal:
  entitlement_behavior: Hide unavailable package features unless tenant entitlement
    and IAM permission allow access.
i18n:
  locales:
  - es-MX
  - en-US
  namespace: marketplace
  hardcoded_text_allowed: false
```
