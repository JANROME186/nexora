import { listTests } from "../api/publicCatalogApi";
import { CatalogCard } from "../components/common/CatalogCard";
import { CatalogListView } from "../components/common/CatalogListView";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { testDetailPath } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function TestsPage() {
  const { t, locale } = useLocale();
  usePageMeta("testsTitle", "testsDescription");
  const { status, data, error, retry } = useFetch(
    () => listTests(siteConfig.laboratoryId),
    siteConfig.laboratoryId,
  );
  const section = t.catalog.tests;

  return (
    <CatalogListView
      headingId="tests-heading"
      title={section.title}
      intro={section.intro}
      status={status}
      data={data}
      error={error}
      onRetry={retry}
      renderCard={(test) => (
        <CatalogCard
          key={test.testDefinitionId}
          title={pickLocalized(locale, test.nameEs, test.nameEn)}
          meta={
            <span className="catalog-card__code">
              {section.codeLabel}: {test.code}
            </span>
          }
          href={testDetailPath(test.testDefinitionId)}
          cta={section.detailCta}
        />
      )}
    />
  );
}
