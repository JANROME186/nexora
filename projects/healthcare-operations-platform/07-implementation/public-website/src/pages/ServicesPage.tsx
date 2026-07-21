import { listDiagnosticServices } from "../api/publicCatalogApi";
import { CatalogCard } from "../components/common/CatalogCard";
import { CatalogListView } from "../components/common/CatalogListView";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { serviceDetailPath } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function ServicesPage() {
  const { t, locale } = useLocale();
  usePageMeta("servicesTitle", "servicesDescription");
  const { status, data, error, retry } = useFetch(
    () => listDiagnosticServices(siteConfig.laboratoryId),
    siteConfig.laboratoryId,
  );
  const section = t.catalog.diagnosticServices;

  return (
    <CatalogListView
      headingId="services-heading"
      title={section.title}
      intro={section.intro}
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      renderCard={(service) => (
        <CatalogCard
          key={service.serviceId}
          title={pickLocalized(locale, service.nameEs, service.nameEn)}
          meta={
            <span className="catalog-card__code">
              {section.codeLabel}: {service.code}
            </span>
          }
          href={serviceDetailPath(service.serviceId)}
          cta={section.detailCta}
        />
      )}
    />
  );
}
