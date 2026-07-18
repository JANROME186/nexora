import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { esMX, type MessageCatalog } from "./locales/es-MX";
import { enUS } from "./locales/en-US";

export type Locale = "es-MX" | "en-US";

const CATALOGS: Record<Locale, MessageCatalog> = {
  "es-MX": esMX,
  "en-US": enUS,
};

/** Default locale per enterprise-product-foundation-standard `localization_and_i18n.default_locale`. */
const DEFAULT_LOCALE: Locale = "es-MX";
/** Fallback locale per enterprise-product-foundation-standard `localization_and_i18n.fallback_locale`. */
const FALLBACK_LOCALE: Locale = "en-US";

const STORAGE_KEY = "hop.locale";

function isLocale(value: string | null): value is Locale {
  return value === "es-MX" || value === "en-US";
}

function readStoredLocale(): Locale {
  try {
    const stored = window.localStorage.getItem(STORAGE_KEY);
    if (isLocale(stored)) {
      return stored;
    }
    return stored === null ? DEFAULT_LOCALE : FALLBACK_LOCALE;
  } catch {
    return DEFAULT_LOCALE;
  }
}

interface LocaleContextValue {
  locale: Locale;
  setLocale: (locale: Locale) => void;
  t: MessageCatalog;
}

const LocaleContext = createContext<LocaleContextValue | undefined>(undefined);

/**
 * Provides the active locale, a setter that persists the user's choice, and the resolved message
 * catalog for that locale. Defaults to es-MX; falls back to en-US if a stored value is invalid.
 */
export function LocaleProvider({ children }: { children: ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>(() => readStoredLocale());

  const setLocale = useCallback((next: Locale) => {
    setLocaleState(next);
    try {
      window.localStorage.setItem(STORAGE_KEY, next);
    } catch {
      // Local storage may be unavailable (e.g. private browsing); the in-memory locale still
      // applies for the current session.
    }
  }, []);

  const value = useMemo<LocaleContextValue>(
    () => ({ locale, setLocale, t: CATALOGS[locale] }),
    [locale, setLocale],
  );

  return <LocaleContext.Provider value={value}>{children}</LocaleContext.Provider>;
}

export function useLocale(): LocaleContextValue {
  const context = useContext(LocaleContext);
  if (!context) {
    throw new Error("useLocale must be used within a LocaleProvider.");
  }
  return context;
}
