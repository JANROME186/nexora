import type { ReactNode } from "react";
import { useLocale } from "../../i18n/LocaleContext";
import type { FetchStatus } from "../../state/useFetch";
import { Link } from "../../router/Router";
import { ErrorState } from "./ErrorState";
import { LoadingState } from "./LoadingState";
import { resolveErrorMessage } from "./resolveErrorMessage";

interface CatalogDetailViewProps<T> {
  headingId: string;
  status: FetchStatus;
  data: T | undefined;
  error: unknown;
  onRetry: () => void;
  backTo: string;
  backLabel: string;
  children: (data: T) => ReactNode;
}

/** Shared loading/error/success chrome and back-link for every published-catalog detail page
 * (service, test, panel, preparation), which otherwise differ only in which fields they show. */
export function CatalogDetailView<T>({
  headingId,
  status,
  data,
  error,
  onRetry,
  backTo,
  backLabel,
  children,
}: CatalogDetailViewProps<T>) {
  const { t } = useLocale();

  return (
    <section aria-labelledby={headingId}>
      <Link to={backTo} className="back-link">
        ← {backLabel}
      </Link>
      {status === "loading" && <LoadingState />}
      {status === "error" && (
        <ErrorState message={resolveErrorMessage(error, t.errors)} onRetry={onRetry} />
      )}
      {status === "success" && data && children(data)}
    </section>
  );
}
