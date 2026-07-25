/**
 * Marketplace Commercial Offers administration screen (COM-MOD-017-FE-001).
 *
 * Manages commercial offers: list offers (optionally filtered by packageId), publish a new offer
 * and accept an offer for a tenant. Offers are a global catalog (BCM-PLT-011 ui-model.md
 * SCREEN_MARKETPLACE_OFFERS), not tenant-scoped; accepting an offer targets a specific tenant
 * supplied on the accept form. Backed by CommercialOfferController.
 */
import { useState } from "react";
import { acceptOffer, listOffers, publishOffer } from "../../api/marketplaceApi";
import type { CommercialOffer } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type Labels = MessageCatalog["marketplace"]["offers"];
type SharedLabels = MessageCatalog["marketplace"]["shared"];

interface PublishOfferFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    packageId: string;
    packageVersion: string;
    offerCode: string;
    offerType: string;
    tierCodes: string;
    trialPeriodDays: string;
    billingEventRulesSummary: string;
    actorId: string;
  }) => void;
}

function PublishOfferForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: PublishOfferFormProps) {
  const [packageId, setPackageId] = useState("");
  const [packageVersion, setPackageVersion] = useState("");
  const [offerCode, setOfferCode] = useState("");
  const [offerType, setOfferType] = useState("");
  const [tierCodes, setTierCodes] = useState("");
  const [trialPeriodDays, setTrialPeriodDays] = useState("");
  const [billingEventRulesSummary, setBillingEventRulesSummary] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.publishHeading}</h3>
      <label htmlFor="mkt-offer-package-id">{labels.packageId}</label>
      <input
        id="mkt-offer-package-id"
        value={packageId}
        onChange={(e) => setPackageId(e.target.value)}
      />
      <label htmlFor="mkt-offer-package-version">{labels.packageVersion}</label>
      <input
        id="mkt-offer-package-version"
        value={packageVersion}
        onChange={(e) => setPackageVersion(e.target.value)}
      />
      <label htmlFor="mkt-offer-code">{labels.offerCode}</label>
      <input id="mkt-offer-code" value={offerCode} onChange={(e) => setOfferCode(e.target.value)} />
      <label htmlFor="mkt-offer-type">{labels.offerType}</label>
      <input id="mkt-offer-type" value={offerType} onChange={(e) => setOfferType(e.target.value)} />
      <label htmlFor="mkt-offer-tier-codes">{labels.tierCodes}</label>
      <input
        id="mkt-offer-tier-codes"
        value={tierCodes}
        onChange={(e) => setTierCodes(e.target.value)}
      />
      <label htmlFor="mkt-offer-trial-period-days">{labels.trialPeriodDays}</label>
      <input
        id="mkt-offer-trial-period-days"
        value={trialPeriodDays}
        onChange={(e) => setTrialPeriodDays(e.target.value)}
      />
      <label htmlFor="mkt-offer-billing-rules">{labels.billingEventRulesSummary}</label>
      <input
        id="mkt-offer-billing-rules"
        value={billingEventRulesSummary}
        onChange={(e) => setBillingEventRulesSummary(e.target.value)}
      />
      <label htmlFor="mkt-offer-publish-actor">{labels.actorId}</label>
      <input
        id="mkt-offer-publish-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-offer-publish-btn"
        disabled={disabled}
        onClick={() =>
          onSubmit({
            packageId,
            packageVersion,
            offerCode,
            offerType,
            tierCodes,
            trialPeriodDays,
            billingEventRulesSummary,
            actorId,
          })
        }
      >
        {labels.publish}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.publishSuccess}
      />
    </div>
  );
}

interface AcceptOfferFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { tenantId: string; actorId: string }) => void;
}

function AcceptOfferForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: AcceptOfferFormProps) {
  const [tenantId, setTenantId] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.acceptHeading}</h3>
      <label htmlFor="mkt-offer-accept-tenant">{labels.acceptTenantId}</label>
      <input
        id="mkt-offer-accept-tenant"
        value={tenantId}
        onChange={(e) => setTenantId(e.target.value)}
      />
      <label htmlFor="mkt-offer-accept-actor">{labels.actorId}</label>
      <input
        id="mkt-offer-accept-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-offer-accept-btn"
        disabled={disabled}
        onClick={() => onSubmit({ tenantId, actorId })}
      >
        {labels.acceptOffer}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.acceptSuccess}
      />
    </div>
  );
}

function offerColumns(labels: Labels, shared: SharedLabels): DataTableColumn<CommercialOffer>[] {
  return [
    { key: "offerCode", header: labels.offerCode, render: (r) => r.offerCode },
    { key: "packageId", header: labels.packageId, render: (r) => r.packageId },
    { key: "offerType", header: labels.offerType, render: (r) => r.offerType },
    { key: "lifecycleStatus", header: shared.status, render: (r) => r.lifecycleStatus },
  ];
}

export function MarketplaceOffersScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.marketplace.shared;
  const labels = t.marketplace.offers;

  const [offers, setOffers] = useState<CommercialOffer[]>([]);
  const [selected, setSelected] = useState<CommercialOffer | undefined>();
  const [filterPackageId, setFilterPackageId] = useState("");

  const loadAction = useAsyncAction(listOffers);
  const publishAction = useAsyncAction(publishOffer);
  const acceptAction = useAsyncAction(acceptOffer);

  async function handleLoad() {
    const result = await loadAction.run(filterPackageId || undefined);
    if (result.ok) setOffers(result.data);
  }

  async function handlePublish(fields: {
    packageId: string;
    packageVersion: string;
    offerCode: string;
    offerType: string;
    tierCodes: string;
    trialPeriodDays: string;
    billingEventRulesSummary: string;
    actorId: string;
  }) {
    const result = await publishAction.run({
      packageId: fields.packageId,
      packageVersion: fields.packageVersion,
      offerCode: fields.offerCode,
      offerType: fields.offerType,
      tierCodes: fields.tierCodes
        .split(",")
        .map((v) => v.trim())
        .filter((v) => v.length > 0),
      trialPeriodDays: fields.trialPeriodDays ? Number(fields.trialPeriodDays) : undefined,
      billingEventRulesSummary: fields.billingEventRulesSummary || undefined,
      actorId: fields.actorId,
    });
    if (result.ok) setOffers((prev) => [...prev, result.data]);
  }

  async function handleAccept(fields: { tenantId: string; actorId: string }) {
    if (!selected) return;
    await acceptAction.run(selected.offerId, fields);
  }

  return (
    <section aria-labelledby="mkt-offer-heading">
      <h2 id="mkt-offer-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <label htmlFor="mkt-offer-filter-package-id">{labels.filterByPackageId}</label>
      <input
        id="mkt-offer-filter-package-id"
        value={filterPackageId}
        onChange={(e) => setFilterPackageId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-offer-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadOffers}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && offers.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={offerColumns(labels, shared)}
        rows={offers}
        rowKey={(r) => r.offerId}
        onSelectRow={setSelected}
      />

      <PublishOfferForm
        labels={labels}
        disabled={publishAction.status === "loading"}
        status={publishAction.status}
        errorMessage={publishAction.errorMessage}
        onSubmit={handlePublish}
      />

      {selected ? (
        <AcceptOfferForm
          labels={labels}
          disabled={acceptAction.status === "loading"}
          status={acceptAction.status}
          errorMessage={acceptAction.errorMessage}
          onSubmit={handleAccept}
        />
      ) : null}
    </section>
  );
}
