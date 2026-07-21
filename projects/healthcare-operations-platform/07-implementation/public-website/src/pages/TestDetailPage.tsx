import { getTestSnapshot } from "../api/publicCatalogApi";
import { CatalogDetailView } from "../components/common/CatalogDetailView";
import { DetailField } from "../components/common/DetailField";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function TestDetailPage({ testId }: { testId: string }) {
  const { t, locale } = useLocale();
  usePageMeta("testsTitle", "testsDescription");
  const { status, data, error, retry } = useFetch(() => getTestSnapshot(testId), testId);
  const section = t.catalog.tests;
  const catalog = t.catalog;

  return (
    <CatalogDetailView
      headingId="test-detail-heading"
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      backTo={ROUTES.tests}
      backLabel={section.backToList}
    >
      {(test) => (
        <article>
          <h1 id="test-detail-heading">{pickLocalized(locale, test.nameEs, test.nameEn)}</h1>
          <dl className="detail-list">
            <DetailField label={section.codeLabel} value={test.code} />
            <DetailField label={catalog.methodologyLabel} value={test.methodology} />
            <DetailField label={catalog.measurementUnitLabel} value={test.measurementUnit} />
            <DetailField label={catalog.resultTypeLabel} value={test.resultType} />
            <DetailField
              label={catalog.turnaroundTimeLabel}
              value={`${test.turnaroundTimeHours} ${catalog.turnaroundTimeHoursSuffix}`}
            />
            <DetailField label={section.versionLabel} value={test.version} />
          </dl>
        </article>
      )}
    </CatalogDetailView>
  );
}
