import type { ReactElement } from "react";
import { useLocale } from "../../i18n/LocaleContext";
import type { FetchStatus } from "../../state/useFetch";
import { EmptyState } from "./EmptyState";
import { ErrorState } from "./ErrorState";
import { LoadingState } from "./LoadingState";
import { resolveErrorMessage } from "./resolveErrorMessage";

interface CatalogListViewProps<T> {
  headingId: string;
  title: string;
  intro: string;
  status: FetchStatus;
  data: T[] | undefined;
  error: unknown;
  onRetry: () => void;
  renderCard: (item: T) => ReactElement;
}

/** Shared loading/empty/error/success chrome for every published-catalog list page (services,
 * tests, panels, preparations), which otherwise differ only in their data shape and card markup. */
export function CatalogListView<T>({
  headingId,
  title,
  intro,
  status,
  data,
  error,
  onRetry,
  renderCard,
}: CatalogListViewProps<T>) {
  const { t } = useLocale();

  return (
    <section aria-labelledby={headingId}>
      <h1 id={headingId}>{title}</h1>
      <p>{intro}</p>
      {status === "loading" && <LoadingState />}
      {status === "error" && (
        <ErrorState message={resolveErrorMessage(error, t.errors)} onRetry={onRetry} />
      )}
      {status === "success" && data && data.length === 0 && <EmptyState />}
      {status === "success" && data && data.length > 0 && (
        <ul className="catalog-grid">{data.map(renderCard)}</ul>
      )}
    </section>
  );
}
