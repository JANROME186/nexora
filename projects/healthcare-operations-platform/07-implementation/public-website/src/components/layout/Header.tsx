import { useState } from "react";
import { useLocale } from "../../i18n/LocaleContext";
import { Link, useRouter } from "../../router/Router";
import { ROUTES } from "../../router/routes";
import { LanguageSwitcher } from "./LanguageSwitcher";

const NAV_ITEMS: {
  to: string;
  labelKey: "home" | "services" | "tests" | "panels" | "preparations" | "marketplace";
}[] = [
  { to: ROUTES.home, labelKey: "home" },
  { to: ROUTES.services, labelKey: "services" },
  { to: ROUTES.tests, labelKey: "tests" },
  { to: ROUTES.panels, labelKey: "panels" },
  { to: ROUTES.preparations, labelKey: "preparations" },
  { to: ROUTES.marketplace, labelKey: "marketplace" },
];

export function Header() {
  const { t } = useLocale();
  const { pathname } = useRouter();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="site-header">
      <div className="site-header__bar">
        <Link to={ROUTES.home} className="site-header__brand" onClick={() => setMenuOpen(false)}>
          {t.siteName}
        </Link>
        <button
          type="button"
          className="site-header__menu-toggle"
          aria-expanded={menuOpen}
          aria-controls="primary-navigation"
          onClick={() => setMenuOpen((open) => !open)}
        >
          <span className="sr-only">{t.nav.home}</span>
          <span aria-hidden="true">☰</span>
        </button>
      </div>
      <nav
        id="primary-navigation"
        className={`site-header__nav ${menuOpen ? "is-open" : ""}`}
        aria-label={t.siteName}
      >
        <ul>
          {NAV_ITEMS.map((item) => (
            <li key={item.to}>
              <Link
                to={item.to}
                aria-current={pathname === item.to ? "page" : undefined}
                onClick={() => setMenuOpen(false)}
              >
                {t.nav[item.labelKey]}
              </Link>
            </li>
          ))}
        </ul>
        <div className="site-header__actions">
          <Link
            to={ROUTES.appointmentRequest}
            className="btn btn--primary"
            onClick={() => setMenuOpen(false)}
          >
            {t.nav.appointment}
          </Link>
          <Link
            to={ROUTES.quotationRequest}
            className="btn btn--secondary"
            onClick={() => setMenuOpen(false)}
          >
            {t.nav.quotation}
          </Link>
          <LanguageSwitcher />
        </div>
      </nav>
    </header>
  );
}
