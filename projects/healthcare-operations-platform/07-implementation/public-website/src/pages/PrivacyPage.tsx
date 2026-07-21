import { useLocale } from "../i18n/LocaleContext";
import { usePageMeta } from "../seo/usePageMeta";

export function PrivacyPage() {
  const { t } = useLocale();
  usePageMeta("privacyTitle", "privacyDescription");

  return (
    <section aria-labelledby="privacy-heading" className="privacy-page">
      <h1 id="privacy-heading">{t.privacyPage.title}</h1>
      <p>{t.privacyPage.intro}</p>
      <h2>{t.privacyPage.dataCollectedTitle}</h2>
      <p>{t.privacyPage.dataCollectedBody}</p>
      <h2>{t.privacyPage.dataUseTitle}</h2>
      <p>{t.privacyPage.dataUseBody}</p>
      <h2>{t.privacyPage.contactTitle}</h2>
      <p>{t.privacyPage.contactBody}</p>
    </section>
  );
}
