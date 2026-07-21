import { useLocale } from "../../i18n/LocaleContext";

export function ErrorState({ message, onRetry }: { message: string; onRetry?: () => void }) {
  const { t } = useLocale();
  return (
    <div className="state-panel state-panel--error" role="alert">
      <p>{message}</p>
      {onRetry && (
        <button type="button" className="btn btn--secondary" onClick={onRetry}>
          {t.states.retry}
        </button>
      )}
    </div>
  );
}
