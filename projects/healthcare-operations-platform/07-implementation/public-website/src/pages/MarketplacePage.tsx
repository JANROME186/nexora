import { useState } from "react";
import { listPublishedMarketplacePackages } from "../api/publicMarketplaceApi";
import type { PublicMarketplacePackageSnapshot } from "../api/types";
import { CatalogCard } from "../components/common/CatalogCard";
import { ErrorState } from "../components/common/ErrorState";
import { LoadingState } from "../components/common/LoadingState";
import { resolveErrorMessage } from "../components/common/resolveErrorMessage";
import { useLocale } from "../i18n/LocaleContext";
import { marketplaceDetailPath } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

export function MarketplacePage() {
  const { t } = useLocale();
  usePageMeta("marketplaceTitle", "marketplaceDescription");

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("ALL");

  const {
    status,
    data: packages,
    error,
    retry,
  } = useFetch(() => listPublishedMarketplacePackages(), "published-packages");

  const m = t.marketplace;

  const categories = packages ? Array.from(new Set(packages.map((p) => p.category))) : [];

  const filteredPackages = (packages || []).filter((pkg) => {
    const matchesSearch =
      pkg.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      pkg.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
      pkg.category.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === "ALL" || pkg.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <main className="container page-content" id="main-content">
      <header className="page-header">
        <h1 id="marketplace-heading">{m.title}</h1>
        <p className="page-header__intro">{m.intro}</p>
      </header>

      {status === "loading" && <LoadingState />}
      {status === "error" && (
        <ErrorState message={resolveErrorMessage(error, t.errors)} onRetry={retry} />
      )}

      {status === "success" && (
        <section aria-labelledby="marketplace-heading">
          <div className="catalog-controls" style={{ marginBottom: "2rem" }}>
            <div className="form-group" style={{ flex: 1, minWidth: "240px" }}>
              <label htmlFor="marketplace-search" className="sr-only">
                {m.searchPlaceholder}
              </label>
              <input
                id="marketplace-search"
                type="search"
                className="form-control"
                placeholder={m.searchPlaceholder}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            {categories.length > 0 && (
              <div className="form-group" style={{ minWidth: "180px" }}>
                <label htmlFor="marketplace-category" className="sr-only">
                  {m.categoryAll}
                </label>
                <select
                  id="marketplace-category"
                  className="form-control"
                  value={selectedCategory}
                  onChange={(e) => setSelectedCategory(e.target.value)}
                >
                  <option value="ALL">{m.categoryAll}</option>
                  {categories.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {filteredPackages.length === 0 ? (
            <p className="catalog-empty">{t.states.empty}</p>
          ) : (
            <ul className="catalog-grid" aria-label={m.title}>
              {filteredPackages.map((pkg: PublicMarketplacePackageSnapshot) => (
                <li key={pkg.packageId}>
                  <CatalogCard
                    title={pkg.name}
                    meta={
                      <div style={{ display: "flex", gap: "0.5rem", flexWrap: "wrap" }}>
                        <span className="catalog-card__code">
                          {m.codeLabel}: {pkg.code}
                        </span>
                        <span className="badge badge--secondary">{pkg.category}</span>
                      </div>
                    }
                    href={marketplaceDetailPath(pkg.packageId)}
                    cta={t.catalog.diagnosticServices.detailCta}
                  />
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </main>
  );
}
