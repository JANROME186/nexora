import type { Locale } from "./LocaleContext";

/** Picks the es-MX or en-US variant of a bilingual catalog field returned by the backend. */
export function pickLocalized(locale: Locale, nameEs: string, nameEn: string): string {
  return locale === "es-MX" ? nameEs : nameEn;
}
