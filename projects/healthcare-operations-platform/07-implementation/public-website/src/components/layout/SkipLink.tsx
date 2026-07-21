import { useLocale } from "../../i18n/LocaleContext";

export function SkipLink() {
  const { t } = useLocale();
  return (
    <a className="skip-link" href="#main-content">
      {t.skipToContent}
    </a>
  );
}
