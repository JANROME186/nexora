import { useEffect } from "react";
import { useLocale } from "../i18n/LocaleContext";

const SITE_TITLE_SUFFIX = " | Healthcare Operations Platform";

function setMetaTag(attribute: "name" | "property", key: string, content: string): void {
  let element = document.head.querySelector<HTMLMetaElement>(`meta[${attribute}="${key}"]`);
  if (!element) {
    element = document.createElement("meta");
    element.setAttribute(attribute, key);
    document.head.appendChild(element);
  }
  element.setAttribute("content", content);
}

function setCanonicalLink(href: string): void {
  let element = document.head.querySelector<HTMLLinkElement>('link[rel="canonical"]');
  if (!element) {
    element = document.createElement("link");
    element.setAttribute("rel", "canonical");
    document.head.appendChild(element);
  }
  element.setAttribute("href", href);
}

/**
 * Sets the per-page `<title>`, meta description, Open Graph tags and canonical link for the
 * currently rendered page. This is a small hand-rolled hook rather than react-helmet(-async):
 * the site has no SSR step, so a client-only DOM-mutation hook is sufficient and keeps the
 * dependency surface minimal (see Router.tsx for the same rationale).
 */
export function usePageMeta(titleKey: string, descriptionKey: string): void {
  const { t } = useLocale();

  useEffect(() => {
    const catalog = t.seo as unknown as Record<string, string>;
    const title = catalog[titleKey] ?? t.seo.homeTitle;
    const description = catalog[descriptionKey] ?? t.seo.defaultDescription;
    const fullTitle = `${title}${SITE_TITLE_SUFFIX}`;

    document.title = fullTitle;
    document.documentElement.lang = document.documentElement.lang || "es-MX";
    setMetaTag("name", "description", description);
    setMetaTag("property", "og:title", fullTitle);
    setMetaTag("property", "og:description", description);
    setMetaTag("property", "og:type", "website");
    setCanonicalLink(window.location.pathname);
  }, [t, titleKey, descriptionKey]);
}
