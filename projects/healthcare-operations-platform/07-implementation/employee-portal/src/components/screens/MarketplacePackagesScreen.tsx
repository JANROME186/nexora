/**
 * Marketplace Package Catalog administration screen (COM-MOD-017-FE-001).
 *
 * Reviews the published package catalog and manages package version lifecycle: submit a new
 * package, publish a version, certify a version, retire a version and view a specific version.
 * Packages are a global catalog (BCM-PLT-011 ui-model.md SCREEN_MARKETPLACE_CATALOG_ADMIN), not
 * tenant-scoped. Backed by PackageCatalogController.
 */
import { useState } from "react";
import {
  certifyPackageVersion,
  getPackageVersion,
  listPublishedPackages,
  publishPackage,
  retirePackageVersion,
  submitPackage,
} from "../../api/marketplaceApi";
import type { MarketplacePackageRecord, PackageVersionRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["marketplace"]["packages"];
type SharedLabels = MessageCatalog["marketplace"]["shared"];

interface SubmitPackageFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    code: string;
    name: string;
    category: string;
    capabilityMappings: string;
    initialVersion: string;
    actorId: string;
  }) => void;
}

function SubmitPackageForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: SubmitPackageFormProps) {
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [capabilityMappings, setCapabilityMappings] = useState("");
  const [initialVersion, setInitialVersion] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.submitHeading}</h3>
      <label htmlFor="mkt-pkg-code">{labels.code}</label>
      <input id="mkt-pkg-code" value={code} onChange={(e) => setCode(e.target.value)} />
      <label htmlFor="mkt-pkg-name">{labels.name}</label>
      <input id="mkt-pkg-name" value={name} onChange={(e) => setName(e.target.value)} />
      <label htmlFor="mkt-pkg-category">{labels.category}</label>
      <input id="mkt-pkg-category" value={category} onChange={(e) => setCategory(e.target.value)} />
      <label htmlFor="mkt-pkg-capability-mappings">{labels.capabilityMappings}</label>
      <input
        id="mkt-pkg-capability-mappings"
        value={capabilityMappings}
        onChange={(e) => setCapabilityMappings(e.target.value)}
      />
      <label htmlFor="mkt-pkg-initial-version">{labels.initialVersion}</label>
      <input
        id="mkt-pkg-initial-version"
        value={initialVersion}
        onChange={(e) => setInitialVersion(e.target.value)}
      />
      <label htmlFor="mkt-pkg-submit-actor">{labels.actorId}</label>
      <input
        id="mkt-pkg-submit-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-pkg-submit-btn"
        disabled={disabled}
        onClick={() =>
          onSubmit({ code, name, category, capabilityMappings, initialVersion, actorId })
        }
      >
        {labels.submit}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.submitSuccess}
      />
    </div>
  );
}

interface PublishVersionFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { version: string; actorId: string }) => void;
}

function PublishVersionForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: PublishVersionFormProps) {
  const [version, setVersion] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.publishHeading}</h3>
      <label htmlFor="mkt-pkg-publish-version">{labels.version}</label>
      <input
        id="mkt-pkg-publish-version"
        value={version}
        onChange={(e) => setVersion(e.target.value)}
      />
      <label htmlFor="mkt-pkg-publish-actor">{labels.actorId}</label>
      <input
        id="mkt-pkg-publish-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-pkg-publish-btn"
        disabled={disabled}
        onClick={() => onSubmit({ version, actorId })}
      >
        {labels.publishVersion}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.publishSuccess}
      />
    </div>
  );
}

interface ViewVersionFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onView: (version: string) => void;
}

function ViewVersionForm({ labels, disabled, status, errorMessage, onView }: ViewVersionFormProps) {
  const [version, setVersion] = useState("");

  return (
    <div className="panel">
      <h3>{labels.viewHeading}</h3>
      <label htmlFor="mkt-pkg-view-version">{labels.version}</label>
      <input
        id="mkt-pkg-view-version"
        value={version}
        onChange={(e) => setVersion(e.target.value)}
      />
      <button
        type="button"
        id="mkt-pkg-view-btn"
        disabled={disabled}
        onClick={() => onView(version)}
      >
        {labels.viewVersion}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.viewVersionSuccess}
      />
    </div>
  );
}

interface CertifyVersionFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    compatibilityApproved: boolean;
    securityReviewApproved: boolean;
    supportModelApproved: boolean;
    telemetryModelApproved: boolean;
    compatibilityMetadataText: string;
    actorId: string;
  }) => void;
}

function CertifyVersionForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: CertifyVersionFormProps) {
  const [compatibilityApproved, setCompatibilityApproved] = useState(false);
  const [securityReviewApproved, setSecurityReviewApproved] = useState(false);
  const [supportModelApproved, setSupportModelApproved] = useState(false);
  const [telemetryModelApproved, setTelemetryModelApproved] = useState(false);
  const [compatibilityMetadataText, setCompatibilityMetadataText] = useState("");
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.certifyHeading}</h3>
      <label htmlFor="mkt-pkg-certify-compatibility">{labels.compatibilityApproved}</label>
      <input
        id="mkt-pkg-certify-compatibility"
        type="checkbox"
        checked={compatibilityApproved}
        onChange={(e) => setCompatibilityApproved(e.target.checked)}
      />
      <label htmlFor="mkt-pkg-certify-security">{labels.securityReviewApproved}</label>
      <input
        id="mkt-pkg-certify-security"
        type="checkbox"
        checked={securityReviewApproved}
        onChange={(e) => setSecurityReviewApproved(e.target.checked)}
      />
      <label htmlFor="mkt-pkg-certify-support">{labels.supportModelApproved}</label>
      <input
        id="mkt-pkg-certify-support"
        type="checkbox"
        checked={supportModelApproved}
        onChange={(e) => setSupportModelApproved(e.target.checked)}
      />
      <label htmlFor="mkt-pkg-certify-telemetry">{labels.telemetryModelApproved}</label>
      <input
        id="mkt-pkg-certify-telemetry"
        type="checkbox"
        checked={telemetryModelApproved}
        onChange={(e) => setTelemetryModelApproved(e.target.checked)}
      />
      <label htmlFor="mkt-pkg-certify-metadata">{labels.compatibilityMetadataText}</label>
      <input
        id="mkt-pkg-certify-metadata"
        value={compatibilityMetadataText}
        onChange={(e) => setCompatibilityMetadataText(e.target.value)}
      />
      <label htmlFor="mkt-pkg-certify-actor">{labels.actorId}</label>
      <input
        id="mkt-pkg-certify-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-pkg-certify-btn"
        disabled={disabled}
        onClick={() =>
          onSubmit({
            compatibilityApproved,
            securityReviewApproved,
            supportModelApproved,
            telemetryModelApproved,
            compatibilityMetadataText,
            actorId,
          })
        }
      >
        {labels.certifyVersion}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.certifySuccess}
      />
    </div>
  );
}

interface RetireVersionFormProps {
  labels: Labels;
  disabled: boolean;
  onRequest: (actorId: string) => void;
}

function RetireVersionForm({ labels, disabled, onRequest }: RetireVersionFormProps) {
  const [actorId, setActorId] = useState("");

  return (
    <div className="panel">
      <h3>{labels.retireHeading}</h3>
      <label htmlFor="mkt-pkg-retire-actor">{labels.actorId}</label>
      <input
        id="mkt-pkg-retire-actor"
        value={actorId}
        onChange={(e) => setActorId(e.target.value)}
      />
      <button
        type="button"
        id="mkt-pkg-retire-btn"
        disabled={disabled}
        onClick={() => onRequest(actorId)}
      >
        {labels.retireVersion}
      </button>
    </div>
  );
}

function packageColumns(
  labels: Labels,
  shared: SharedLabels,
): DataTableColumn<MarketplacePackageRecord>[] {
  return [
    { key: "code", header: labels.code, render: (r) => r.code },
    { key: "name", header: labels.name, render: (r) => r.name },
    { key: "category", header: labels.category, render: (r) => r.category },
    { key: "status", header: shared.status, render: (r) => r.status },
  ];
}

export function MarketplacePackagesScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.marketplace.shared;
  const labels = t.marketplace.packages;

  const [packages, setPackages] = useState<MarketplacePackageRecord[]>([]);
  const [selected, setSelected] = useState<MarketplacePackageRecord | undefined>();
  const [selectedVersion, setSelectedVersion] = useState<PackageVersionRecord | undefined>();
  const [confirmRetire, setConfirmRetire] = useState(false);
  const [pendingRetireActor, setPendingRetireActor] = useState("");

  const loadAction = useAsyncAction(listPublishedPackages);
  const submitAction = useAsyncAction(submitPackage);
  const publishAction = useAsyncAction(publishPackage);
  const viewVersionAction = useAsyncAction(getPackageVersion);
  const certifyAction = useAsyncAction(certifyPackageVersion);
  const retireAction = useAsyncAction(retirePackageVersion);

  async function handleLoad() {
    const result = await loadAction.run();
    if (result.ok) setPackages(result.data);
  }

  async function handleSubmit(fields: {
    code: string;
    name: string;
    category: string;
    capabilityMappings: string;
    initialVersion: string;
    actorId: string;
  }) {
    const result = await submitAction.run({
      code: fields.code,
      name: fields.name,
      category: fields.category,
      capabilityMappings: fields.capabilityMappings
        .split(",")
        .map((v) => v.trim())
        .filter((v) => v.length > 0),
      initialVersion: fields.initialVersion,
      actorId: fields.actorId,
    });
    if (result.ok) setPackages((prev) => [...prev, result.data]);
  }

  function selectPackage(pkg: MarketplacePackageRecord) {
    setSelected(pkg);
    setSelectedVersion(undefined);
  }

  async function handlePublish(fields: { version: string; actorId: string }) {
    if (!selected) return;
    const result = await publishAction.run(selected.packageId, fields);
    if (result.ok) {
      setSelected(result.data);
      setPackages((prev) =>
        prev.map((p) => (p.packageId === result.data.packageId ? result.data : p)),
      );
    }
  }

  async function handleViewVersion(version: string) {
    if (!selected) return;
    const result = await viewVersionAction.run(selected.packageId, version);
    if (result.ok) setSelectedVersion(result.data);
  }

  async function handleCertify(fields: {
    compatibilityApproved: boolean;
    securityReviewApproved: boolean;
    supportModelApproved: boolean;
    telemetryModelApproved: boolean;
    compatibilityMetadataText: string;
    actorId: string;
  }) {
    if (!selected || !selectedVersion) return;
    const result = await certifyAction.run(selected.packageId, selectedVersion.version, fields);
    if (result.ok) setSelectedVersion(result.data);
  }

  function requestRetire(actorId: string) {
    setPendingRetireActor(actorId);
    setConfirmRetire(true);
  }

  async function confirmRetireAction() {
    if (!selected || !selectedVersion) return;
    setConfirmRetire(false);
    const result = await retireAction.run(selected.packageId, selectedVersion.version, {
      actorId: pendingRetireActor,
    });
    if (result.ok) setSelectedVersion(result.data);
  }

  return (
    <section aria-labelledby="mkt-pkg-heading">
      <h2 id="mkt-pkg-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="mkt-pkg-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadPackages}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && packages.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={packageColumns(labels, shared)}
        rows={packages}
        rowKey={(r) => r.packageId}
        onSelectRow={selectPackage}
      />

      <SubmitPackageForm
        labels={labels}
        disabled={submitAction.status === "loading"}
        status={submitAction.status}
        errorMessage={submitAction.errorMessage}
        onSubmit={handleSubmit}
      />

      {selected ? (
        <>
          <PublishVersionForm
            labels={labels}
            disabled={publishAction.status === "loading"}
            status={publishAction.status}
            errorMessage={publishAction.errorMessage}
            onSubmit={handlePublish}
          />
          <ViewVersionForm
            labels={labels}
            disabled={viewVersionAction.status === "loading"}
            status={viewVersionAction.status}
            errorMessage={viewVersionAction.errorMessage}
            onView={handleViewVersion}
          />
        </>
      ) : null}

      {selected && selectedVersion ? (
        <>
          <p>
            {labels.lifecycleStatus}: {selectedVersion.lifecycleStatus}
          </p>
          <CertifyVersionForm
            labels={labels}
            disabled={certifyAction.status === "loading"}
            status={certifyAction.status}
            errorMessage={certifyAction.errorMessage}
            onSubmit={handleCertify}
          />
          <RetireVersionForm
            labels={labels}
            disabled={retireAction.status === "loading"}
            onRequest={requestRetire}
          />
          <StatusBanner
            status={retireAction.status}
            errorMessage={retireAction.errorMessage}
            successMessage={labels.retireSuccess}
          />
        </>
      ) : null}

      <ConfirmDialog
        open={confirmRetire}
        title={labels.retireDialog.title}
        description={labels.retireDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmRetireAction}
        onCancel={() => setConfirmRetire(false)}
      />
    </section>
  );
}
