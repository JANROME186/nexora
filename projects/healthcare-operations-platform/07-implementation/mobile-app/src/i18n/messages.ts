/**
 * Backward-compatible re-export of the default-locale (es-MX) message catalog.
 *
 * Historically this module was the single, English-only message catalog (HOP-QA-ALIGN-005). It
 * has since been restructured into locale-keyed catalogs under `src/i18n/locales/` plus a
 * `getMessages(locale)` resolver (see `src/i18n/locale.ts`; there is no renderable UI layer yet,
 * so this is a plain function rather than a React context). `src/auth/localAuth.ts` now accepts
 * an optional locale parameter and calls `getMessages` directly; this module remains only for
 * any other existing import of the flat `MESSAGES` catalog.
 */
export { esMX as MESSAGES } from "./locales/es-MX";
