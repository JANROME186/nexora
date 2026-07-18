import { esMX, type MessageCatalog } from "./locales/es-MX";
import { enUS } from "./locales/en-US";

export type Locale = "es-MX" | "en-US";

const CATALOGS: Record<Locale, MessageCatalog> = {
  "es-MX": esMX,
  "en-US": enUS,
};

/** Default locale per enterprise-product-foundation-standard `localization_and_i18n.default_locale`. */
export const DEFAULT_LOCALE: Locale = "es-MX";

/**
 * Resolves the message catalog for the given locale. There is no renderable UI layer yet (see
 * TD-I18N-002), so this is a plain function rather than a React context/hook; a future renderer
 * stack can wrap this the same way the employee-portal's `LocaleContext` does.
 */
export function getMessages(locale: Locale = DEFAULT_LOCALE): MessageCatalog {
  return CATALOGS[locale];
}
