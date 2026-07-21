import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import { Link } from "../router/Router";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";

export function HomePage() {
  const { t, locale } = useLocale();
  usePageMeta("homeTitle", "homeDescription");

  return (
    <div className="home-page">
      <section className="hero">
        <h1>{t.home.heroTitle}</h1>
        <p>{t.home.heroSubtitle}</p>
        <div className="hero__actions">
          <Link to={ROUTES.appointmentRequest} className="btn btn--primary">
            {t.home.ctaAppointment}
          </Link>
          <Link to={ROUTES.quotationRequest} className="btn btn--secondary">
            {t.home.ctaQuotation}
          </Link>
        </div>
      </section>

      <section aria-labelledby="catalog-overview-heading" className="catalog-overview">
        <h2 id="catalog-overview-heading" className="sr-only">
          {t.nav.services}
        </h2>
        <ul className="overview-grid">
          <li className="overview-card">
            <h3>{t.home.servicesCardTitle}</h3>
            <p>{t.home.servicesCardBody}</p>
            <Link to={ROUTES.services}>{t.catalog.diagnosticServices.detailCta}</Link>
          </li>
          <li className="overview-card">
            <h3>{t.home.testsCardTitle}</h3>
            <p>{t.home.testsCardBody}</p>
            <Link to={ROUTES.tests}>{t.catalog.tests.detailCta}</Link>
          </li>
          <li className="overview-card">
            <h3>{t.home.panelsCardTitle}</h3>
            <p>{t.home.panelsCardBody}</p>
            <Link to={ROUTES.panels}>{t.catalog.panels.detailCta}</Link>
          </li>
          <li className="overview-card">
            <h3>{t.home.preparationsCardTitle}</h3>
            <p>{t.home.preparationsCardBody}</p>
            <Link to={ROUTES.preparations}>{t.catalog.preparations.detailCta}</Link>
          </li>
        </ul>
      </section>

      <section aria-labelledby="locations-heading" className="locations">
        <h2 id="locations-heading">{t.home.locationsTitle}</h2>
        <p>{t.home.locationsIntro}</p>
        <ul className="locations-list">
          {siteConfig.branches.map((branch) => (
            <li key={branch.branchId} className="locations-list__item">
              <h3>{pickLocalized(locale, branch.nameEs, branch.nameEn)}</h3>
              <p>{pickLocalized(locale, branch.addressEs, branch.addressEn)}</p>
              {branch.phone && <p>{branch.phone}</p>}
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
