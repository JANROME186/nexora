import { getDiagnosticServiceSnapshot } from "../api/publicCatalogApi";
import { CatalogDetailView } from "../components/common/CatalogDetailView";
import { DetailField } from "../components/common/DetailField";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function ServiceDetailPage({ serviceId }: { serviceId: string }) {
  const { t, locale } = useLocale();
  usePageMeta("servicesTitle", "servicesDescription");
  const { status, data, error, retry } = useFetch(
    () => getDiagnosticServiceSnapshot(serviceId),
    serviceId,
  );
  const section = t.catalog.diagnosticServices;
  const catalog = t.catalog;

  return (
    <CatalogDetailView
      headingId="service-detail-heading"
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      backTo={ROUTES.services}
      backLabel={section.backToList}
    >
      {(service) => (
        <article>
          <h1 id="service-detail-heading">
            {pickLocalized(locale, service.nameEs, service.nameEn)}
          </h1>
          <dl className="detail-list">
            <DetailField label={section.codeLabel} value={service.code} />
            <DetailField label={catalog.serviceTypeLabel} value={service.serviceType} />
            <DetailField label={section.versionLabel} value={service.version} />
          </dl>
        </article>
      )}
    </CatalogDetailView>
  );
}
