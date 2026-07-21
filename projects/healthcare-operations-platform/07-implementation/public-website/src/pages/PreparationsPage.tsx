import { listPreparations } from "../api/publicCatalogApi";
import { CatalogCard } from "../components/common/CatalogCard";
import { CatalogListView } from "../components/common/CatalogListView";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { preparationDetailPath } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function PreparationsPage() {
  const { t, locale } = useLocale();
  usePageMeta("preparationsTitle", "preparationsDescription");
  const { status, data, error, retry } = useFetch(
    () => listPreparations(siteConfig.laboratoryId),
    siteConfig.laboratoryId,
  );
  const section = t.catalog.preparations;

  return (
    <CatalogListView
      headingId="preparations-heading"
      title={section.title}
      intro={section.intro}
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      renderCard={(preparation) => (
        <CatalogCard
          key={preparation.preparationId}
          title={pickLocalized(locale, preparation.titleEs, preparation.titleEn)}
          meta={
            <span className="catalog-card__code">
              {section.codeLabel}: {preparation.code}
            </span>
          }
          href={preparationDetailPath(preparation.preparationId)}
          cta={section.detailCta}
        />
      )}
    />
  );
}
