import { useLocale, type Locale } from "../../i18n/LocaleContext";

const OPTIONS: { locale: Locale; label: string }[] = [
  { locale: "es-MX", label: "ES" },
  { locale: "en-US", label: "EN" },
];

export function LanguageSwitcher() {
  const { locale, setLocale, t } = useLocale();

  return (
    <div className="language-switcher" role="group" aria-label={t.languageSwitcherLabel}>
      {OPTIONS.map((option) => (
        <button
          key={option.locale}
          type="button"
          className={`language-switcher__option ${locale === option.locale ? "is-active" : ""}`}
          aria-pressed={locale === option.locale}
          onClick={() => setLocale(option.locale)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}
