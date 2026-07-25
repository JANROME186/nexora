# Regional Localization & Currency Configuration Guide

## Overview

This guide describes regional localization configuration, locale switching (`es-MX`, `en-US`), ISO currency formatting, date/time standards, and country-specific rules across **Healthcare Operations Platform (HOP)**.

## Supported Locales & Default Configurations

| Region / Language | Locale Identifier | Currency Code | Date Format | Default Timezone | Primary Target |
|---|---|---|---|---|---|
| **Mexico (Spanish)** | `es-MX` | `MXN` (`$`) | `DD/MM/YYYY` | `America/Mexico_City` | Primary Commercial Target |
| **United States (English)**| `en-US` | `USD` (`$`) | `MM/DD/YYYY` | `America/New_York` | International / Cross-Border |

## Locale Context & Switching

Locale switching is available across all user-facing surfaces:
1. **Employee Portal**: Dynamic locale selector in header (`LocaleContext`), updating UI strings instantly without full page reload.
2. **Patient & Doctor Portals**: Localized i18n context switching.
3. **Public Website**: Localized static content and anonymous form labels.
4. **Mobile App**: Localized label catalog (`PATIENT_MOBILE_LABEL_CATALOG`).

### Backend i18n Error Response Envelope
Every backend error response delivers structured, localized error details:

```json
{
  "code": "ORDER_ALREADY_CANCELLED",
  "messageKey": "care_delivery.error.order_already_cancelled",
  "message": "La orden de diagnóstico ya ha sido cancelada.",
  "timestamp": "2026-07-24T14:00:00Z"
}
```

The `messageKey` allows frontends to display custom localized messages from their local catalogs when needed, while `message` provides default localized text in the tenant's primary locale.

## Currency & Monetary Value Standard

All financial transactions, sales line items, and diagnostic price lists enforce strict `Money` representation:
- **`amount`**: Exact decimal value (avoiding binary floating-point rounding errors).
- **`currency`**: ISO 4217 3-letter currency code (e.g., `MXN`, `USD`).

```http
POST /api/platform/configuration
Content-Type: application/json
X-Tenant-ID: CLINICA-SAN-JOSE

{
  "defaultLocale": "es-MX",
  "supportedLocales": ["es-MX", "en-US"],
  "defaultCurrency": "MXN",
  "timezone": "America/Mexico_City",
  "dateFormat": "DD/MM/YYYY"
}
```

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-ONB-GUIDE-004
  type: regional-localization-config-specification
  name: HOP Regional Localization and Currency Configuration Specification
  version: 1.0.0
  status: approved
  human_readable: regional-localization-and-currency-config-guide.md
  machine_readable: regional-localization-and-currency-config-guide.md
  backlog_item: COM-MOD-016-DOC-001
supported_locales:
- id: es-MX
  name: Spanish (Mexico)
  default_currency: MXN
  date_format: DD/MM/YYYY
  timezone: America/Mexico_City
- id: en-US
  name: English (United States)
  default_currency: USD
  date_format: MM/DD/YYYY
  timezone: America/New_York
currency_standard:
  iso_standard: ISO 4217
  supported_codes:
  - MXN
  - USD
  precision: 2_decimal_places
  data_type: Money_object_amount_and_currency
i18n_envelope:
  required_fields:
  - code
  - messageKey
  - message
  - timestamp
traceability:
  capabilities:
  - BCM-PLT-002
  open_source_first: true
  agent_agnostic: true
```
