/**
 * Marketplace Tenant Entitlements administration screen (COM-MOD-017-FE-001).
 *
 * Grants, revokes and inspects tenant entitlements, scoped by `AdminScope.tenantId`
 * (BCM-PLT-011 ui-model.md SCREEN_TENANT_ENTITLEMENTS). Revoke is a sensitive action (uses
 * `ConfirmDialog`, mirroring the `linkQualityEvent` pattern) since it removes tenant access to a
 * package. Backed by TenantEntitlementController.
 */
import { useState } from "react";
import {
  grantEntitlement,
  listTenantEntitlements,
  revokeEntitlement,
} from "../../api/marketplaceApi";
import type { TenantEntitlement } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["marketplace"]["entitlements"];
type SharedLabels = MessageCatalog["marketplace"]["shared"];

interface GrantEntitlementFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    packageId: string;
    offerId: string;
    expiresAt: string;
    usageLimit: string;
    actorId: string;
  }) => void;
}

function GrantEntitlementForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: GrantEntitlementFormProps) {
  const [packageId, setPackageId] = useState("");
  const [offerId, setOfferId] = useState("");
  const [expiresAt, setExpiresAt] = useState("");
  const [usageLimit, setUsageLimit] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.grantHeading}</h3>
      <label htmlFor="mkt-ent-package-id">{labels.packageId}</label>
      <input
        id="mkt-ent-package-id"
        value={packageId}
        onChange={(e) => setPackageId(e.target.value)}
      />
      <label htmlFor="mkt-ent-offer-id">{labels.offerId}</label>
      <input id="mkt-ent-offer-id" value={offerId} onChange={(e) => setOfferId(e.target.value)} />
      <label htmlFor="mkt-ent-expires-at">{labels.expiresAt}</label>
      <input
        id="mkt-ent-expires-at"
        type="date"
        value={expiresAt}
        onChange={(e) => setExpiresAt(e.target.value)}
      />
      <label htmlFor="mkt-ent-usage-limit">{labels.usageLimit}</label>
      <input
        id="mkt-ent-usage-limit"
        value={usageLimit}
        onChange={(e) => setUsageLimit(e.target.value)}
      />
      <label htmlFor="mkt-ent-grant-actor">{labels.actorId}</label>
      <input
        id="mkt-ent-grant-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-ent-grant-btn"
        disabled={disabled}
        onClick={() => onSubmit({ packageId, offerId, expiresAt, usageLimit, actorId })}
      >
        {labels.grant}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.grantSuccess}
      />
    </div>
  );
}

interface RevokeEntitlementFormProps {
  labels: Labels;
  disabled: boolean;
  onRequest: (fields: { reason: string; actorId: string }) => void;
}

function RevokeEntitlementForm({ labels, disabled, onRequest }: RevokeEntitlementFormProps) {
  const [reason, setReason] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.revokeHeading}</h3>
      <label htmlFor="mkt-ent-revoke-reason">{labels.revokeReason}</label>
      <input
        id="mkt-ent-revoke-reason"
        value={reason}
        onChange={(e) => setReason(e.target.value)}
      />
      <label htmlFor="mkt-ent-revoke-actor">{labels.actorId}</label>
      <input
        id="mkt-ent-revoke-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-ent-revoke-btn"
        disabled={disabled}
        onClick={() => onRequest({ reason, actorId })}
      >
        {labels.revoke}
      </button>
    </div>
  );
}

function entitlementColumns(
  labels: Labels,
  shared: SharedLabels,
): DataTableColumn<TenantEntitlement>[] {
  return [
    { key: "entitlementId", header: labels.entitlementId, render: (r) => r.entitlementId },
    { key: "packageId", header: labels.packageId, render: (r) => r.packageId },
    { key: "status", header: shared.status, render: (r) => r.status },
    { key: "expiresAt", header: labels.expiresAt, render: (r) => r.expiresAt ?? "-" },
  ];
}

export function MarketplaceEntitlementsScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.marketplace.shared;
  const labels = t.marketplace.entitlements;
  const { scope } = useAdminScope();

  const [entitlements, setEntitlements] = useState<TenantEntitlement[]>([]);
  const [selected, setSelected] = useState<TenantEntitlement | undefined>();
  const [confirmRevoke, setConfirmRevoke] = useState(false);
  const [pendingRevoke, setPendingRevoke] = useState<{ reason: string; actorId: string } | null>(
    null,
  );

  const loadAction = useAsyncAction(listTenantEntitlements);
  const grantAction = useAsyncAction(grantEntitlement);
  const revokeAction = useAsyncAction(revokeEntitlement);

  const tenantId = scope.tenantId ?? "";

  async function handleLoad() {
    const result = await loadAction.run(tenantId);
    if (result.ok) setEntitlements(result.data);
  }

  async function handleGrant(fields: {
    packageId: string;
    offerId: string;
    expiresAt: string;
    usageLimit: string;
    actorId: string;
  }) {
    const result = await grantAction.run(tenantId, {
      packageId: fields.packageId,
      offerId: fields.offerId || undefined,
      expiresAt: fields.expiresAt || undefined,
      usageLimit: fields.usageLimit ? Number(fields.usageLimit) : undefined,
      actorId: fields.actorId,
    });
    if (result.ok) setEntitlements((prev) => [...prev, result.data]);
  }

  function requestRevoke(fields: { reason: string; actorId: string }) {
    setPendingRevoke(fields);
    setConfirmRevoke(true);
  }

  async function confirmRevokeAction() {
    if (!selected || !pendingRevoke) return;
    setConfirmRevoke(false);
    const result = await revokeAction.run(tenantId, selected.entitlementId, {
      reason: pendingRevoke.reason,
      actorId: pendingRevoke.actorId,
    });
    if (result.ok)
      setEntitlements((prev) =>
        prev.map((e) => (e.entitlementId === result.data.entitlementId ? result.data : e)),
      );
  }

  return (
    <section aria-labelledby="mkt-ent-heading">
      <h2 id="mkt-ent-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="mkt-ent-load-btn"
        disabled={loadAction.status === "loading" || !tenantId}
        onClick={handleLoad}
      >
        {labels.loadEntitlements}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {!tenantId ? <p className="empty-state">{shared.tenantRequired}</p> : null}
      {loadAction.status === "success" && entitlements.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={entitlementColumns(labels, shared)}
        rows={entitlements}
        rowKey={(r) => r.entitlementId}
        onSelectRow={setSelected}
      />

      <GrantEntitlementForm
        labels={labels}
        disabled={grantAction.status === "loading" || !tenantId}
        status={grantAction.status}
        errorMessage={grantAction.errorMessage}
        onSubmit={handleGrant}
      />

      {selected ? (
        <>
          <RevokeEntitlementForm
            labels={labels}
            disabled={revokeAction.status === "loading" || !tenantId}
            onRequest={requestRevoke}
          />
          <StatusBanner
            status={revokeAction.status}
            errorMessage={revokeAction.errorMessage}
            successMessage={labels.revokeSuccess}
          />
        </>
      ) : null}

      <ConfirmDialog
        open={confirmRevoke}
        title={labels.revokeDialog.title}
        description={labels.revokeDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmRevokeAction}
        onCancel={() => setConfirmRevoke(false)}
      />
    </section>
  );
}
