import { getPreparationSnapshot } from "../api/publicCatalogApi";
import { CatalogDetailView } from "../components/common/CatalogDetailView";
import { DetailField } from "../components/common/DetailField";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function PreparationDetailPage({ preparationId }: { preparationId: string }) {
  const { t, locale } = useLocale();
  usePageMeta("preparationsTitle", "preparationsDescription");
  const { status, data, error, retry } = useFetch(
    () => getPreparationSnapshot(preparationId),
    preparationId,
  );
  const section = t.catalog.preparations;
  const catalog = t.catalog;

  return (
    <CatalogDetailView
      headingId="preparation-detail-heading"
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      backTo={ROUTES.preparations}
      backLabel={section.backToList}
    >
      {(preparation) => (
        <article>
          <h1 id="preparation-detail-heading">
            {pickLocalized(locale, preparation.titleEs, preparation.titleEn)}
          </h1>
          <dl className="detail-list">
            <DetailField label={section.codeLabel} value={preparation.code} />
            <DetailField label={catalog.categoryLabel} value={preparation.category} />
            <DetailField
              label={catalog.durationLabel}
              value={
                preparation.durationHours != null
                  ? `${preparation.durationHours} ${catalog.durationHoursSuffix}`
                  : null
              }
            />
            <DetailField label={section.versionLabel} value={preparation.version} />
          </dl>
          <div className="preparation-instructions">
            <h2>{catalog.instructionsLabel}</h2>
            <p>
              {pickLocalized(locale, preparation.instructionTextEs, preparation.instructionTextEn)}
            </p>
          </div>
        </article>
      )}
    </CatalogDetailView>
  );
}
