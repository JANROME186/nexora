import { getPanelSnapshot } from "../api/publicCatalogApi";
import { CatalogDetailView } from "../components/common/CatalogDetailView";
import { DetailField } from "../components/common/DetailField";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function PanelDetailPage({ panelId }: { panelId: string }) {
  const { t, locale } = useLocale();
  usePageMeta("panelsTitle", "panelsDescription");
  const { status, data, error, retry } = useFetch(() => getPanelSnapshot(panelId), panelId);
  const section = t.catalog.panels;

  return (
    <CatalogDetailView
      headingId="panel-detail-heading"
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      backTo={ROUTES.panels}
      backLabel={section.backToList}
    >
      {(panel) => (
        <article>
          <h1 id="panel-detail-heading">{pickLocalized(locale, panel.nameEs, panel.nameEn)}</h1>
          <dl className="detail-list">
            <DetailField label={section.codeLabel} value={panel.code} />
            <DetailField label={section.versionLabel} value={panel.version} />
          </dl>
        </article>
      )}
    </CatalogDetailView>
  );
}
