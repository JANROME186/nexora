/**
 * es-MX message catalog (default locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `default_locale: es-MX`, `fallback_locale: en-US`).
 *
 * These are Spanish translations of the original HOP-QA-ALIGN-005 mobile-foundation validation
 * strings (previously the sole content of `src/i18n/messages.ts`, English-only). This file is
 * the source of truth for `MessageCatalog` — `en-US.ts` is type-checked against it so the two
 * locales can never drift out of key parity.
 */
export const esMX = {
  tenantIdRequired: "El id del tenant es obligatorio.",
  userIdRequired: "El id del usuario es obligatorio.",
  displayNameRequired: "El nombre para mostrar es obligatorio.",
  emailRequired: "El correo electrónico es obligatorio.",
  emailInvalid: "El correo electrónico debe ser válido.",
  sessionRequired: "Se requiere una sesión autenticada.",
  resultsTitle: "Resultados",
  resultDetailTitle: "Detalle del Resultado",
  resultHistoryTitle: "Historial de Resultados",
} as const;

/** Recursively widens the `esMX` literal string types to `string` so other locales (en-US) can
 * hold different text while TypeScript still enforces identical key structure. */
type Widen<T> = T extends string ? string : { [K in keyof T]: Widen<T[K]> };

export type MessageCatalog = Widen<typeof esMX>;
