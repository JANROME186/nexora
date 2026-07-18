/**
 * Backward-compatible re-export of the default-locale (es-MX) message catalog.
 *
 * Historically this module was the single, English-only message catalog (HOP-QA-ALIGN-005 /
 * MVP-MOD-007-FE-001). It has since been restructured into locale-keyed catalogs under
 * `src/i18n/locales/` plus a `LocaleContext`/`useLocale()` switching mechanism (see
 * `src/i18n/LocaleContext.tsx`). Screens that call `useLocale()` get the fully locale-aware
 * catalog; the ~13 screens that still `import { MESSAGES } from "../../i18n/messages"` directly
 * (predating the locale switch) keep working unchanged against the es-MX catalog, which is now
 * the product's default locale per the enterprise-product-foundation-standard. Migrating those
 * remaining call sites to `useLocale()` is tracked as remaining TD-I18N-002 scope.
 */
export { esMX as MESSAGES } from "./locales/es-MX";
