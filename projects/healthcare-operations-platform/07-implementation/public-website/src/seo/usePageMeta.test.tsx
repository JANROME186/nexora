import { render } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { LocaleProvider } from "../i18n/LocaleContext";
import { usePageMeta } from "./usePageMeta";

function Probe({ titleKey, descriptionKey }: { titleKey: string; descriptionKey: string }) {
  usePageMeta(titleKey, descriptionKey);
  return null;
}

describe("usePageMeta", () => {
  it("sets document title, description and canonical link for a known key", () => {
    render(
      <LocaleProvider>
        <Probe titleKey="servicesTitle" descriptionKey="servicesDescription" />
      </LocaleProvider>,
    );

    expect(document.title).toBe("Servicios diagnósticos | Healthcare Operations Platform");
    expect(document.querySelector('meta[name="description"]')?.getAttribute("content")).toBe(
      "Catálogo público de servicios diagnósticos disponibles.",
    );
    expect(document.querySelector('link[rel="canonical"]')?.getAttribute("href")).toBe(
      window.location.pathname,
    );
    expect(document.querySelector('meta[property="og:title"]')).not.toBeNull();
  });

  it("falls back to the default title/description for an unknown key", () => {
    render(
      <LocaleProvider>
        <Probe titleKey="doesNotExist" descriptionKey="doesNotExist" />
      </LocaleProvider>,
    );

    expect(document.title).toBe("Inicio | Healthcare Operations Platform");
    expect(document.querySelector('meta[name="description"]')?.getAttribute("content")).toBe(
      "Descubre servicios diagnósticos, pruebas de laboratorio, paneles e instrucciones de preparación, y solicita una cita o cotización en línea.",
    );
  });
});
