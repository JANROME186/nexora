import { useEffect, useState } from "react";
import { listPanels, listTests } from "../api/publicCatalogApi";
import { siteConfig } from "../config/siteConfig";
import type { Locale } from "../i18n/LocaleContext";
import { pickLocalized } from "../i18n/pickLocalized";
import type { PublicCatalogItemKind } from "../api/types";

export interface CatalogItemOption {
  id: string;
  kind: PublicCatalogItemKind;
  label: string;
}

export type CatalogItemOptionsStatus = "loading" | "success" | "error";

/** Loads published tests and panels together so appointment/quotation forms can offer a single
 * combined picker, without re-implementing the published-catalog fetch twice. */
export function useCatalogItemOptions(locale: Locale): {
  options: CatalogItemOption[];
  status: CatalogItemOptionsStatus;
} {
  const [options, setOptions] = useState<CatalogItemOption[]>([]);
  const [status, setStatus] = useState<CatalogItemOptionsStatus>("loading");

  useEffect(() => {
    let cancelled = false;
    // Resetting to "loading" when `locale` changes is intended: it clears stale option labels
    // while the relabeled options for the new locale are fetched.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setStatus("loading");
    Promise.all([listTests(siteConfig.laboratoryId), listPanels(siteConfig.laboratoryId)])
      .then(([tests, panels]) => {
        if (cancelled) {
          return;
        }
        const testOptions: CatalogItemOption[] = tests.map((test) => ({
          id: test.testDefinitionId,
          kind: "test",
          label: pickLocalized(locale, test.nameEs, test.nameEn),
        }));
        const panelOptions: CatalogItemOption[] = panels.map((panel) => ({
          id: panel.panelId,
          kind: "panel",
          label: pickLocalized(locale, panel.nameEs, panel.nameEn),
        }));
        setOptions([...testOptions, ...panelOptions]);
        setStatus("success");
      })
      .catch(() => {
        if (!cancelled) {
          setStatus("error");
        }
      });
    return () => {
      cancelled = true;
    };
  }, [locale]);

  return { options, status };
}
