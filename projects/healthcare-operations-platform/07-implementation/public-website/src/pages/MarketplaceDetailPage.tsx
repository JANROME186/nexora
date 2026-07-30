import { useState } from "react";
import {
  getPublishedMarketplacePackageSnapshot,
  listPublishedMarketplaceOffers,
} from "../api/publicMarketplaceApi";
import type { PublicMarketplaceOfferSnapshot } from "../api/types";
import { DetailField } from "../components/common/DetailField";
import { ErrorState } from "../components/common/ErrorState";
import { LoadingState } from "../components/common/LoadingState";
import { resolveErrorMessage } from "../components/common/resolveErrorMessage";
import { useLocale } from "../i18n/LocaleContext";
import { Link } from "../router/Router";
import { ROUTES } from "../router/routes";
import { usePageMeta } from "../seo/usePageMeta";
import { useFetch } from "../state/useFetch";

interface MarketplaceDetailPageProps {
  packageId: string;
}

export function MarketplaceDetailPage({ packageId }: MarketplaceDetailPageProps) {
  const { t } = useLocale();
  const m = t.marketplace;

  const {
    status: pkgStatus,
    data: pkg,
    error: pkgError,
    retry: retryPkg,
  } = useFetch(() => getPublishedMarketplacePackageSnapshot(packageId), `pkg-${packageId}`);

  const {
    status: offersStatus,
    data: offers,
    error: offersError,
    retry: retryOffers,
  } = useFetch(() => listPublishedMarketplaceOffers(packageId), `offers-${packageId}`);

  usePageMeta("marketplaceTitle", "marketplaceDescription");

  const [contactSubmitted, setContactSubmitted] = useState(false);

  const isLoading = pkgStatus === "loading" || offersStatus === "loading";
  const hasError = pkgStatus === "error" || offersStatus === "error";
  const combinedError = pkgError || offersError;

  const handleContactRequest = (e: React.FormEvent) => {
    e.preventDefault();
    setContactSubmitted(true);
  };

  return (
    <main className="container page-content" id="main-content">
      <div style={{ marginBottom: "1.5rem" }}>
        <Link to={ROUTES.marketplace} className="btn btn--secondary">
          &larr; {m.backToList}
        </Link>
      </div>

      {isLoading && <LoadingState />}
      {hasError && (
        <ErrorState
          message={resolveErrorMessage(combinedError, t.errors)}
          onRetry={() => {
            retryPkg();
            retryOffers();
          }}
        />
      )}

      {!isLoading && !hasError && pkg && (
        <article className="detail-view">
          <header className="detail-view__header">
            <h1>{pkg.name}</h1>
            <p className="detail-view__code">
              {m.codeLabel}: <code>{pkg.code}</code>
            </p>
          </header>

          <section className="detail-view__grid" aria-label="Package Details">
            <DetailField label={m.statusLabel} value={pkg.status} />
            <DetailField label="Category" value={pkg.category} />
          </section>

          {pkg.capabilityMappings && pkg.capabilityMappings.length > 0 && (
            <section style={{ marginTop: "2rem" }}>
              <h2>{m.capabilitiesTitle}</h2>
              <ul
                style={{
                  display: "flex",
                  gap: "0.5rem",
                  flexWrap: "wrap",
                  listStyle: "none",
                  padding: 0,
                }}
              >
                {pkg.capabilityMappings.map((cap) => (
                  <li key={cap}>
                    <span className="badge badge--primary">{cap}</span>
                  </li>
                ))}
              </ul>
            </section>
          )}

          <section style={{ marginTop: "2.5rem" }}>
            <h2>{m.offersTitle}</h2>
            {offers && offers.length > 0 ? (
              <ul
                className="catalog-grid"
                style={{ listStyle: "none", padding: 0 }}
                aria-label={m.offersTitle}
              >
                {offers.map((offer: PublicMarketplaceOfferSnapshot) => (
                  <li key={offer.offerId} className="catalog-card">
                    <h3>{offer.offerCode}</h3>
                    <p style={{ fontSize: "0.9rem", color: "var(--color-text-muted)" }}>
                      Type: <strong>{offer.offerType}</strong> | Version: {offer.packageVersion}
                    </p>
                    {offer.tierCodes && offer.tierCodes.length > 0 && (
                      <p style={{ fontSize: "0.9rem" }}>
                        {m.tierLabel}: {offer.tierCodes.join(", ")}
                      </p>
                    )}
                    {offer.trialPeriodDays !== null && offer.trialPeriodDays !== undefined && (
                      <p style={{ fontSize: "0.9rem" }}>
                        {m.trialDaysLabel}: <strong>{offer.trialPeriodDays}</strong>
                      </p>
                    )}
                    {offer.billingEventRulesSummary && (
                      <p style={{ fontSize: "0.85rem", color: "var(--color-text-muted)" }}>
                        {m.billingRulesLabel}: {offer.billingEventRulesSummary}
                      </p>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="catalog-empty">{m.noOffers}</p>
            )}
          </section>

          <section
            style={{
              marginTop: "3rem",
              padding: "2rem",
              backgroundColor: "var(--color-surface-elevated, #f8f9fa)",
              borderRadius: "8px",
            }}
          >
            <h2>{m.contactModalTitle}</h2>
            {contactSubmitted ? (
              <div className="alert alert--success" role="alert">
                {m.contactModalSent}
              </div>
            ) : (
              <form
                onSubmit={handleContactRequest}
                style={{ display: "flex", flexDirection: "column", gap: "1rem", maxWidth: "480px" }}
              >
                <p>{m.intro}</p>
                <button type="submit" className="btn btn--primary">
                  {m.contactCta}
                </button>
              </form>
            )}
          </section>
        </article>
      )}
    </main>
  );
}
