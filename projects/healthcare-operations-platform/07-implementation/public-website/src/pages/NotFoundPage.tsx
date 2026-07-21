import { useLocale } from "../i18n/LocaleContext";
import { Link } from "../router/Router";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";

export function NotFoundPage() {
  const { t } = useLocale();
  usePageMeta("notFoundTitle", "defaultDescription");

  return (
    <section aria-labelledby="not-found-heading" className="not-found-page">
      <h1 id="not-found-heading">{t.notFound.title}</h1>
      <p>{t.notFound.body}</p>
      <Link to={ROUTES.home} className="btn btn--primary">
        {t.notFound.backHome}
      </Link>
    </section>
  );
}
