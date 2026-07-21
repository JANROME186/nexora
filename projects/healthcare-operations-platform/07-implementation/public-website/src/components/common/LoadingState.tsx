import { useLocale } from "../../i18n/LocaleContext";

export function LoadingState() {
  const { t } = useLocale();
  return (
    <div className="state-panel state-panel--loading" role="status" aria-live="polite">
      <span className="state-panel__spinner" aria-hidden="true" />
      <p>{t.states.loading}</p>
    </div>
  );
}
