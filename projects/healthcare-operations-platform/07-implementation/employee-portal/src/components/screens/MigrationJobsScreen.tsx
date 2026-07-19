/**
 * Migration administration screen (MVP-MOD-008-FE-001, BCM-PLT-010).
 *
 * Covers migration jobs, simple provider-deliverable import packages, dry-run validation,
 * approval, commit, retry and reconciliation reporting.
 */
import { useState } from "react";
import {
  approveImport,
  commitImport,
  createMigrationJob,
  listMigrationJobs,
  listReconciliationReports,
  receiveImportPackage,
  retryImportExecution,
  runDryRunValidation,
} from "../../api/integrationMigrationApi";
import type {
  DryRunReport,
  ImportBatch,
  ImportExecution,
  MigrationJob,
  ReconciliationReport,
} from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

function statusClass(status: string): string {
  const normalized = status.toLowerCase();
  if (
    normalized.includes("completed") ||
    normalized.includes("approved") ||
    normalized.includes("passed")
  ) {
    return "catalog-status catalog-status--published";
  }
  if (normalized.includes("failed") || normalized.includes("rejected")) {
    return "catalog-status catalog-status--retired";
  }
  return "catalog-status catalog-status--draft";
}

function formatCounts(counts: Record<string, number>): string {
  const entries = Object.entries(counts);
  if (entries.length === 0) {
    return "-";
  }
  return entries.map(([key, value]) => `${key}: ${value}`).join("; ");
}

export function MigrationJobsScreen() {
  const { t } = useLocale();
  const labels = t.integrationMigration;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId } = scope;

  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [sourceSystemName, setSourceSystemName] = useState("");
  const [migrationJobId, setMigrationJobId] = useState("");
  const [importBatchId, setImportBatchId] = useState("");
  const [manifest, setManifest] = useState<File | undefined>();
  const [packageFile, setPackageFile] = useState<File | undefined>();
  const [zipBundle, setZipBundle] = useState(false);
  const [jobs, setJobs] = useState<MigrationJob[]>([]);
  const [batch, setBatch] = useState<ImportBatch | undefined>();
  const [dryRun, setDryRun] = useState<DryRunReport | undefined>();
  const [execution, setExecution] = useState<ImportExecution | undefined>();
  const [reconciliation, setReconciliation] = useState<ReconciliationReport[]>([]);

  const listAction = useAsyncAction(async () => {
    const loaded = await listMigrationJobs(tenantId ?? "");
    setJobs(loaded);
    return loaded;
  });

  const createAction = useAsyncAction(async () => {
    const created = await createMigrationJob({
      tenantId: tenantId ?? "",
      laboratoryId: laboratoryId ?? "",
      sourceSystemName,
      actorId,
    });
    setJobs((current) => [
      created,
      ...current.filter((item) => item.migrationJobId !== created.migrationJobId),
    ]);
    setMigrationJobId(created.migrationJobId);
    return created;
  });

  const uploadAction = useAsyncAction(async () => {
    if (!manifest || !packageFile) {
      throw new Error(labels.migrationJobs.packageFile);
    }
    const uploaded = await receiveImportPackage(
      migrationJobId,
      manifest,
      packageFile,
      zipBundle,
      actorId,
    );
    setBatch(uploaded);
    setImportBatchId(uploaded.importBatchId);
    return uploaded;
  });

  const dryRunAction = useAsyncAction(async () => {
    const report = await runDryRunValidation(importBatchId, actorId);
    setDryRun(report);
    return report;
  });

  const approveAction = useAsyncAction(async () => {
    const status = await approveImport(importBatchId, actorId);
    setJobs((current) =>
      current.map((job) =>
        job.migrationJobId === status.migrationJobId ? { ...job, status: status.status } : job,
      ),
    );
    return status;
  });

  const commitAction = useAsyncAction(async () => {
    const committed = await commitImport(importBatchId, actorId);
    setExecution(committed);
    return committed;
  });

  const retryAction = useAsyncAction(async () => {
    const retried = await retryImportExecution(migrationJobId, actorId);
    setExecution(retried);
    return retried;
  });

  const reconciliationAction = useAsyncAction(async () => {
    const reports = await listReconciliationReports(migrationJobId);
    setReconciliation(reports);
    return reports;
  });

  const canUseScope = Boolean(tenantId && laboratoryId);

  return (
    <section aria-labelledby="migration-jobs-heading">
      <h2 id="migration-jobs-heading">{labels.migrationJobs.heading}</h2>
      <p>{labels.migrationJobs.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.laboratoryRequired}</p>
      ) : null}

      <div className="panel">
        <h3>{labels.migrationJobs.sourceSystemName}</h3>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            createAction.run();
          }}
        >
          <label htmlFor="migration-source-system">{labels.migrationJobs.sourceSystemName}</label>
          <input
            id="migration-source-system"
            value={sourceSystemName}
            onChange={(event) => setSourceSystemName(event.target.value)}
          />
          <label htmlFor="migration-actor-id">{labels.shared.actorId}</label>
          <input
            id="migration-actor-id"
            value={actorId}
            onChange={(event) => setActorId(event.target.value)}
          />
          <button
            type="submit"
            disabled={!canUseScope || !sourceSystemName || createAction.status === "loading"}
          >
            {labels.shared.create}
          </button>
        </form>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage={labels.migrationJobs.createSuccess}
        />
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.migrationJobs.loadJobs}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {jobs.length > 0 ? (
        <table>
          <caption>{labels.migrationJobs.heading}</caption>
          <thead>
            <tr>
              <th scope="col">{labels.migrationJobs.migrationJobId}</th>
              <th scope="col">{labels.migrationJobs.sourceSystemName}</th>
              <th scope="col">{labels.shared.status}</th>
            </tr>
          </thead>
          <tbody>
            {jobs.map((job) => (
              <tr key={job.migrationJobId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => setMigrationJobId(job.migrationJobId)}
                  >
                    {job.migrationJobId}
                  </button>
                </td>
                <td>{job.sourceSystemName}</td>
                <td>
                  <span className={statusClass(job.status)}>{job.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      <div className="panel">
        <h3>{labels.migrationJobs.uploadPackage}</h3>
        <label htmlFor="migration-job-id">{labels.migrationJobs.migrationJobId}</label>
        <input
          id="migration-job-id"
          value={migrationJobId}
          onChange={(event) => setMigrationJobId(event.target.value)}
        />
        <label htmlFor="migration-manifest">{labels.migrationJobs.manifest}</label>
        <input
          id="migration-manifest"
          type="file"
          onChange={(event) => setManifest(event.target.files?.[0])}
        />
        <label htmlFor="migration-package-file">{labels.migrationJobs.packageFile}</label>
        <input
          id="migration-package-file"
          type="file"
          onChange={(event) => setPackageFile(event.target.files?.[0])}
        />
        <label htmlFor="migration-zip-bundle">
          <input
            id="migration-zip-bundle"
            type="checkbox"
            checked={zipBundle}
            onChange={(event) => setZipBundle(event.target.checked)}
          />
          {labels.migrationJobs.zipBundle}
        </label>
        <button
          type="button"
          disabled={
            !migrationJobId || !manifest || !packageFile || uploadAction.status === "loading"
          }
          onClick={() => uploadAction.run()}
        >
          {labels.migrationJobs.uploadPackage}
        </button>
        <StatusBanner
          status={uploadAction.status}
          errorMessage={uploadAction.errorMessage}
          successMessage={labels.migrationJobs.uploadSuccess}
        />
        {batch ? (
          <p className="field-hint">
            {batch.importBatchId}: {formatCounts(batch.entityCounts)}
          </p>
        ) : null}
      </div>

      <div className="panel">
        <h3>{labels.migrationJobs.importBatchId}</h3>
        <label htmlFor="migration-import-batch-id">{labels.migrationJobs.importBatchId}</label>
        <input
          id="migration-import-batch-id"
          value={importBatchId}
          onChange={(event) => setImportBatchId(event.target.value)}
        />
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={!importBatchId || dryRunAction.status === "loading"}
            onClick={() => dryRunAction.run()}
          >
            {labels.shared.runDryRun}
          </button>
          <button
            type="button"
            disabled={!importBatchId || approveAction.status === "loading"}
            onClick={() => approveAction.run()}
          >
            {labels.shared.approve}
          </button>
          <button
            type="button"
            disabled={!importBatchId || commitAction.status === "loading"}
            onClick={() => commitAction.run()}
          >
            {labels.shared.commit}
          </button>
        </div>
        <StatusBanner
          status={dryRunAction.status}
          errorMessage={dryRunAction.errorMessage}
          successMessage={labels.migrationJobs.dryRunSuccess}
        />
        <StatusBanner
          status={approveAction.status}
          errorMessage={approveAction.errorMessage}
          successMessage={labels.migrationJobs.approveSuccess}
        />
        <StatusBanner
          status={commitAction.status}
          errorMessage={commitAction.errorMessage}
          successMessage={labels.migrationJobs.commitSuccess}
        />
        {dryRun ? (
          <table>
            <tbody>
              <tr>
                <th scope="row">{labels.shared.status}</th>
                <td>
                  <span className={statusClass(dryRun.passed ? "passed" : "failed")}>
                    {String(dryRun.passed)}
                  </span>
                </td>
              </tr>
              <tr>
                <th scope="row">{labels.migrationJobs.warnings}</th>
                <td>{dryRun.rowLevelWarnings.join("; ") || "-"}</td>
              </tr>
            </tbody>
          </table>
        ) : null}
      </div>

      <div className="panel">
        <h3>{labels.migrationJobs.loadReconciliation}</h3>
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={!migrationJobId || retryAction.status === "loading"}
            onClick={() => retryAction.run()}
          >
            {labels.shared.retry}
          </button>
          <button
            type="button"
            disabled={!migrationJobId || reconciliationAction.status === "loading"}
            onClick={() => reconciliationAction.run()}
          >
            {labels.migrationJobs.loadReconciliation}
          </button>
        </div>
        <StatusBanner
          status={retryAction.status}
          errorMessage={retryAction.errorMessage}
          successMessage={labels.migrationJobs.retrySuccess}
        />
        <StatusBanner
          status={reconciliationAction.status}
          errorMessage={reconciliationAction.errorMessage}
          successMessage={labels.migrationJobs.reconciliationSuccess}
        />
        {execution ? (
          <p className="field-hint">
            {labels.migrationJobs.executionId}: {execution.executionId};{" "}
            {labels.migrationJobs.checkpoint}: {execution.checkpoint}
          </p>
        ) : null}
        {reconciliation.length > 0 ? (
          <table>
            <caption>{labels.migrationJobs.loadReconciliation}</caption>
            <thead>
              <tr>
                <th scope="col">{labels.migrationJobs.phase}</th>
                <th scope="col">{labels.migrationJobs.imported}</th>
                <th scope="col">{labels.migrationJobs.rejected}</th>
                <th scope="col">{labels.migrationJobs.skipped}</th>
                <th scope="col">{labels.migrationJobs.warnings}</th>
              </tr>
            </thead>
            <tbody>
              {reconciliation.map((report) => (
                <tr key={report.reconciliationReportId}>
                  <td>{report.phase}</td>
                  <td>{formatCounts(report.importedCounts)}</td>
                  <td>{formatCounts(report.rejectedCounts)}</td>
                  <td>{formatCounts(report.skippedCounts)}</td>
                  <td>{formatCounts(report.warningCounts)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}
      </div>
    </section>
  );
}
