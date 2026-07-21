import type { ReactNode } from "react";
import { useLocale } from "../../i18n/LocaleContext";

export function EmptyState({ children }: { children?: ReactNode }) {
  const { t } = useLocale();
  return (
    <div className="state-panel state-panel--empty" role="status">
      <p>{children ?? t.states.empty}</p>
    </div>
  );
}
