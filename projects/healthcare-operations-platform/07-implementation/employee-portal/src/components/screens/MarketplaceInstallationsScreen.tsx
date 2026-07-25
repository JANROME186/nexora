/**
 * Marketplace Package Installations administration screen (COM-MOD-017-FE-001).
 *
 * Installs, activates, upgrades, rolls back, suspends and uninstalls packages per tenant, scoped
 * by `AdminScope.tenantId` (BCM-PLT-011 ui-model.md SCREEN_PACKAGE_INSTALLATIONS). Backed by
 * PackageInstallationController.
 *
 * Closes TD-BE-019's remaining acceptance criterion (a real, non-fabricated IAM/menu decision
 * gated by marketplace entitlement/installation runtime state): before allowing "Install package"
 * for a given packageId, this screen loads the tenant's real entitlements
 * (`marketplaceApi.listTenantEntitlements`) and only enables the install action when an
 * entitlement for that exact packageId is effectively active (status "active" and, if set,
 * `expiresAt` in the future — mirroring the backend's `TenantEntitlement.isEffectivelyActive`).
 * Otherwise the control is disabled and an explanatory, localized status is shown.
 */
import { useState } from "react";
import {
  activateInstallation,
  installPackage,
  listInstallations,
  listTenantEntitlements,
  rollbackInstallation,
  suspendInstallation,
  uninstallInstallation,
  upgradeInstallation,
} from "../../api/marketplaceApi";
import type { PackageInstallation, TenantEntitlement } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type Labels = MessageCatalog["marketplace"]["installations"];
type SharedLabels = MessageCatalog["marketplace"]["shared"];

/**
 * Mirrors the backend's `TenantEntitlement.isEffectivelyActive` (active status, and not expired)
 * so the install control is gated on the same real runtime state the backend would enforce.
 */
export function isPackageEntitled(entitlements: TenantEntitlement[], packageId: string): boolean {
  if (!packageId) return false;
  const now = new Date();
  return entitlements.some(
    (entitlement) =>
      entitlement.packageId === packageId &&
      entitlement.status === "active" &&
      (!entitlement.expiresAt || new Date(entitlement.expiresAt) > now),
  );
}

interface InstallPackageFormProps {
  labels: Labels;
  shared: SharedLabels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  entitlements: TenantEntitlement[];
  onSubmit: (fields: {
    packageId: string;
    version: string;
    entitlementId: string;
    actorId: string;
  }) => void;
}

function InstallPackageForm({
  labels,
  disabled,
  status,
  errorMessage,
  entitlements,
  onSubmit,
}: InstallPackageFormProps) {
  const [packageId, setPackageId] = useState("");
  const [version, setVersion] = useState("");
  const [entitlementId, setEntitlementId] = useState("");
  const [actorId, setActorId] = useState("");

  const entitled = isPackageEntitled(entitlements, packageId);

  return (
    <div className="panel">
      <h3>{labels.installHeading}</h3>
      <label htmlFor="mkt-inst-package-id">{labels.packageId}</label>
      <input
        id="mkt-inst-package-id"
        value={packageId}
        onChange={(e) => setPackageId(e.target.value)}
      />
      <label htmlFor="mkt-inst-version">{labels.version}</label>
      <input id="mkt-inst-version" value={version} onChange={(e) => setVersion(e.target.value)} />
      <label htmlFor="mkt-inst-entitlement-id">{labels.entitlementId}</label>
      <input
        id="mkt-inst-entitlement-id"
        value={entitlementId}
        onChange={(e) => setEntitlementId(e.target.value)}
      />
      <label htmlFor="mkt-inst-install-actor">{labels.actorId}</label>
      <input
        id="mkt-inst-install-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-inst-install-btn"
        disabled={disabled || !entitled}
        onClick={() => onSubmit({ packageId, version, entitlementId, actorId })}
      >
        {labels.install}
      </button>
      {packageId && !entitled ? (
        <p id="mkt-inst-not-entitled" role="status" className="status-banner status-banner--error">
          {labels.notEntitled}
        </p>
      ) : null}
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.installSuccess}
      />
    </div>
  );
}

interface ManageInstallationFormProps {
  labels: Labels;
  disabled: boolean;
  onActivate: (actorId: string) => void;
  onSuspend: (actorId: string) => void;
  onUninstall: (actorId: string) => void;
  activateStatus: AsyncStatus;
  activateErrorMessage?: string;
  suspendStatus: AsyncStatus;
  suspendErrorMessage?: string;
  uninstallStatus: AsyncStatus;
  uninstallErrorMessage?: string;
}

function ManageInstallationForm({
  labels,
  disabled,
  onActivate,
  onSuspend,
  onUninstall,
  activateStatus,
  activateErrorMessage,
  suspendStatus,
  suspendErrorMessage,
  uninstallStatus,
  uninstallErrorMessage,
}: ManageInstallationFormProps) {
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.manageHeading}</h3>
      <label htmlFor="mkt-inst-manage-actor">{labels.actorId}</label>
      <input
        id="mkt-inst-manage-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-inst-activate-btn"
        disabled={disabled}
        onClick={() => onActivate(actorId)}
      >
        {labels.activate}
      </button>
      <StatusBanner
        status={activateStatus}
        errorMessage={activateErrorMessage}
        successMessage={labels.activateSuccess}
      />
      <button
        type="button"
        id="mkt-inst-suspend-btn"
        disabled={disabled}
        onClick={() => onSuspend(actorId)}
      >
        {labels.suspend}
      </button>
      <StatusBanner
        status={suspendStatus}
        errorMessage={suspendErrorMessage}
        successMessage={labels.suspendSuccess}
      />
      <button
        type="button"
        id="mkt-inst-uninstall-btn"
        disabled={disabled}
        onClick={() => onUninstall(actorId)}
      >
        {labels.uninstall}
      </button>
      <StatusBanner
        status={uninstallStatus}
        errorMessage={uninstallErrorMessage}
        successMessage={labels.uninstallSuccess}
      />
    </div>
  );
}

interface UpgradeInstallationFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { targetVersion: string; actorId: string }) => void;
}

function UpgradeInstallationForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: UpgradeInstallationFormProps) {
  const [targetVersion, setTargetVersion] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.upgradeHeading}</h3>
      <label htmlFor="mkt-inst-target-version">{labels.targetVersion}</label>
      <input
        id="mkt-inst-target-version"
        value={targetVersion}
        onChange={(e) => setTargetVersion(e.target.value)}
      />
      <label htmlFor="mkt-inst-upgrade-actor">{labels.actorId}</label>
      <input
        id="mkt-inst-upgrade-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-inst-upgrade-btn"
        disabled={disabled}
        onClick={() => onSubmit({ targetVersion, actorId })}
      >
        {labels.upgrade}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.upgradeSuccess}
      />
    </div>
  );
}

interface RollbackInstallationFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (actorId: string) => void;
}

function RollbackInstallationForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: RollbackInstallationFormProps) {
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.rollbackHeading}</h3>
      <label htmlFor="mkt-inst-rollback-actor">{labels.actorId}</label>
      <input
        id="mkt-inst-rollback-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-inst-rollback-btn"
        disabled={disabled}
        onClick={() => onSubmit(actorId)}
      >
        {labels.rollback}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.rollbackSuccess}
      />
    </div>
  );
}

function installationColumns(
  labels: Labels,
  shared: SharedLabels,
): DataTableColumn<PackageInstallation>[] {
  return [
    { key: "installationId", header: labels.installationId, render: (r) => r.installationId },
    { key: "packageId", header: labels.packageId, render: (r) => r.packageId },
    { key: "version", header: labels.version, render: (r) => r.version },
    { key: "lifecycleStatus", header: shared.status, render: (r) => r.lifecycleStatus },
  ];
}

export function MarketplaceInstallationsScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.marketplace.shared;
  const labels = t.marketplace.installations;
  const { scope } = useAdminScope();

  const [installations, setInstallations] = useState<PackageInstallation[]>([]);
  const [entitlements, setEntitlements] = useState<TenantEntitlement[]>([]);
  const [selected, setSelected] = useState<PackageInstallation | undefined>();

  const loadAction = useAsyncAction(listInstallations);
  const loadEntitlementsAction = useAsyncAction(listTenantEntitlements);
  const installAction = useAsyncAction(installPackage);
  const activateAction = useAsyncAction(activateInstallation);
  const suspendAction = useAsyncAction(suspendInstallation);
  const uninstallAction = useAsyncAction(uninstallInstallation);
  const upgradeAction = useAsyncAction(upgradeInstallation);
  const rollbackAction = useAsyncAction(rollbackInstallation);

  const tenantId = scope.tenantId ?? "";
  const scopeReady = tenantId.length > 0;

  async function handleLoad() {
    const [installationsResult, entitlementsResult] = await Promise.all([
      loadAction.run(tenantId),
      loadEntitlementsAction.run(tenantId),
    ]);
    if (installationsResult.ok) setInstallations(installationsResult.data);
    if (entitlementsResult.ok) setEntitlements(entitlementsResult.data);
  }

  async function handleInstall(fields: {
    packageId: string;
    version: string;
    entitlementId: string;
    actorId: string;
  }) {
    const result = await installAction.run(tenantId, {
      packageId: fields.packageId,
      version: fields.version,
      entitlementId: fields.entitlementId || undefined,
      actorId: fields.actorId,
    });
    if (result.ok) setInstallations((prev) => [...prev, result.data]);
  }

  function replaceSelected(updated: PackageInstallation) {
    setSelected(updated);
    setInstallations((prev) =>
      prev.map((i) => (i.installationId === updated.installationId ? updated : i)),
    );
  }

  async function handleActivate(actorId: string) {
    if (!selected) return;
    const result = await activateAction.run(tenantId, selected.installationId, { actorId });
    if (result.ok) replaceSelected(result.data);
  }

  async function handleSuspend(actorId: string) {
    if (!selected) return;
    const result = await suspendAction.run(tenantId, selected.installationId, { actorId });
    if (result.ok) replaceSelected(result.data);
  }

  async function handleUninstall(actorId: string) {
    if (!selected) return;
    const result = await uninstallAction.run(tenantId, selected.installationId, { actorId });
    if (result.ok) replaceSelected(result.data);
  }

  async function handleUpgrade(fields: { targetVersion: string; actorId: string }) {
    if (!selected) return;
    const result = await upgradeAction.run(tenantId, selected.installationId, fields);
    if (result.ok) replaceSelected(result.data);
  }

  async function handleRollback(actorId: string) {
    if (!selected) return;
    const result = await rollbackAction.run(tenantId, selected.installationId, { actorId });
    if (result.ok) replaceSelected(result.data);
  }

  return (
    <section aria-labelledby="mkt-inst-heading">
      <h2 id="mkt-inst-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="mkt-inst-load-btn"
        disabled={loadAction.status === "loading" || !scopeReady}
        onClick={handleLoad}
      >
        {labels.loadInstallations}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {!scopeReady ? <p className="empty-state">{shared.tenantRequired}</p> : null}
      {loadAction.status === "success" && installations.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={installationColumns(labels, shared)}
        rows={installations}
        rowKey={(r) => r.installationId}
        onSelectRow={setSelected}
      />

      <InstallPackageForm
        labels={labels}
        shared={shared}
        disabled={installAction.status === "loading" || !scopeReady}
        status={installAction.status}
        errorMessage={installAction.errorMessage}
        entitlements={entitlements}
        onSubmit={handleInstall}
      />

      {selected ? (
        <>
          <ManageInstallationForm
            labels={labels}
            disabled={!scopeReady}
            onActivate={handleActivate}
            onSuspend={handleSuspend}
            onUninstall={handleUninstall}
            activateStatus={activateAction.status}
            activateErrorMessage={activateAction.errorMessage}
            suspendStatus={suspendAction.status}
            suspendErrorMessage={suspendAction.errorMessage}
            uninstallStatus={uninstallAction.status}
            uninstallErrorMessage={uninstallAction.errorMessage}
          />
          <UpgradeInstallationForm
            labels={labels}
            disabled={upgradeAction.status === "loading" || !scopeReady}
            status={upgradeAction.status}
            errorMessage={upgradeAction.errorMessage}
            onSubmit={handleUpgrade}
          />
          <RollbackInstallationForm
            labels={labels}
            disabled={rollbackAction.status === "loading" || !scopeReady}
            status={rollbackAction.status}
            errorMessage={rollbackAction.errorMessage}
            onSubmit={handleRollback}
          />
        </>
      ) : null}
    </section>
  );
}
