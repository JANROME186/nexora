# HOP Localization and Internationalization Strategy

Machine-readable source: `localization-strategy.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Required base locales

- **es-MX** — default locale.
- **en-US** — fallback locale.

Locale is configurable at three axes: tenant default locale, user-selected locale, and a hard
fallback to en-US.

## Hardcoded text inventory summary

| Stack | Estimated hardcoded strings (pre-iteration) | Remediated this iteration | Remaining |
|---|---|---|---|
| Backend | ~30 domain error/validation messages | `identityaccess` module fully migrated to `MessageSource` + resource bundles | 11 other bounded-context modules — tracked by TD-I18N-002 |
| Employee portal | ~130 (5 centralized, ~125 single-occurrence inline) | AppShell header + 27 nav tab labels via locale-keyed catalogs + language switch | ~125 per-screen inline strings — tracked by TD-I18N-002 |
| Mobile app | 9 validation strings | Locale-keyed catalog split (no UI layer yet) | Full localization-resource validation once a renderer stack is selected (TD-APP-001) |

Full detail: see `hardcoded_text_inventory` in the YAML companion.

## Backend mechanism

Spring `MessageSource` backed by `classpath:i18n/messages{,_es_MX,_en_US}.properties`, configured
in `sharedkernel/LocalizationConfig.java` and resolved through the `sharedkernel/HopMessages.java`
helper. Applied end-to-end to the `identityaccess` module (validation and not-found messages, plus
the new IAM "permission denied" message) as the reference implementation for the remaining modules
to follow when they are next touched.

**Current limitation**: locale is supplied explicitly by callers or defaults to es-MX. The backend
now has a local-development authenticated request context for mapped API paths; the next hardening
step is `Accept-Language` resolution and, later, authenticated user-preference resolution once
production OIDC/IdP login replaces local fixtures.

## Frontend mechanism (employee portal)

A typed, dependency-free TypeScript catalog split into `src/i18n/locales/es-MX.ts` and
`en-US.ts`, exposed through a `LocaleContext`/`useLocale()` React context with `localStorage`
persistence (`hop.locale`). A visible ES/EN switch in the `AppShell` header drives the portal
title/subtitle and all 27 navigation tab labels in both locales today.

`react-i18next` and FormatJS were evaluated and **not adopted this iteration** — both remain the
recommended target for full-catalog, ICU-aware adoption once the remaining ~125 per-screen strings
are migrated (tracked by TD-I18N-002); introducing either now was judged unnecessary for the
scope of this foundation-alignment slice.

## Mobile mechanism

Same locale-keyed split pattern (`src/i18n/locales/es-MX.ts` / `en-US.ts`) behind a plain
`getMessages(locale)` function — there is no UI/renderer layer yet, so no visible switch exists.
Revisited when TD-APP-001 selects a renderer stack.

## Closure gate compliance

New or changed user-visible text introduced by this backlog item is externalized in both locales.
Pre-existing text not touched by this iteration remains inventoried above and tracked by
**TD-I18N-002, materially reduced (not closed)** by this backlog item.
