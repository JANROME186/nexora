import { listPanels } from "../api/publicCatalogApi";
import { CatalogCard } from "../components/common/CatalogCard";
import { CatalogListView } from "../components/common/CatalogListView";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { panelDetailPath } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function PanelsPage() {
  const { t, locale } = useLocale();
  usePageMeta("panelsTitle", "panelsDescription");
  const { status, data, error, retry } = useFetch(
    () => listPanels(siteConfig.laboratoryId),
    siteConfig.laboratoryId,
  );
  const section = t.catalog.panels;

  return (
    <CatalogListView
      headingId="panels-heading"
      title={section.title}
      intro={section.intro}
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      renderCard={(panel) => (
        <CatalogCard
          key={panel.panelId}
          title={pickLocalized(locale, panel.nameEs, panel.nameEn)}
          meta={
            <span className="catalog-card__code">
              {section.codeLabel}: {panel.code}
            </span>
          }
          href={panelDetailPath(panel.panelId)}
          cta={section.detailCta}
        />
      )}
    />
  );
}
