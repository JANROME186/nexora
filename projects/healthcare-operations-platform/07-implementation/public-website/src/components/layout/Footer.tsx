import { useLocale } from "../../i18n/LocaleContext";
import { Link } from "../../router/Router";
import { ROUTES } from "../../router/routes";

export function Footer() {
  const { t } = useLocale();
  const year = new Date().getFullYear();

  return (
    <footer className="site-footer">
      <p>{t.footer.tagline}</p>
      <p>
        <Link to={ROUTES.privacy}>{t.footer.privacyLink}</Link>
      </p>
      <p>
        © {year} {t.siteName}. {t.footer.rightsReserved}
      </p>
    </footer>
  );
}
