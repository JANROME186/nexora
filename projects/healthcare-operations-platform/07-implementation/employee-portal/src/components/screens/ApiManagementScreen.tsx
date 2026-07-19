/**
 * API governance administration screen (MVP-MOD-008-FE-001, BCM-PLT-005).
 *
 * Uses the generated-client-shaped integrationMigrationApi facade so the hand-written UI can move
 * to OpenAPI Generator output without changing component interaction points.
 */
import { useState } from "react";
import {
  classifyApiOperation,
  issuePartnerApiKey,
  listApiOperations,
  listPartnerApiKeys,
  retireApiOperation,
  revokePartnerApiKey,
  scheduleApiDeprecation,
  setRateLimitPolicy,
} from "../../api/integrationMigrationApi";
import type { ApiSurfaceRegistration, PartnerApiKey, RateLimitPolicy } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

function statusClass(status: string): string {
  const normalized = status.toLowerCase();
  if (normalized.includes("active") || normalized.includes("stable")) {
    return "catalog-status catalog-status--published";
  }
  if (normalized.includes("retired") || normalized.includes("revoked")) {
    return "catalog-status catalog-status--retired";
  }
  return "catalog-status catalog-status--draft";
}

function parseScopes(value: string): string[] {
  return value
    .split(",")
    .map((scope) => scope.trim())
    .filter(Boolean);
}

export function ApiManagementScreen() {
  const { t } = useLocale();
  const labels = t.integrationMigration;
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [operationId, setOperationId] = useState("");
  const [ownerCapability, setOwnerCapability] = useState("BCM-PLT-005");
  const [classification, setClassification] = useState("partner");
  const [apiVersion, setApiVersion] = useState("v1");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [deprecationWindowFrom, setDeprecationWindowFrom] = useState("");
  const [deprecationWindowTo, setDeprecationWindowTo] = useState("");
  const [migrationNote, setMigrationNote] = useState("");
  const [consumerName, setConsumerName] = useState("");
  const [grantedScopes, setGrantedScopes] = useState("integration:read");
  const [keyId, setKeyId] = useState("");
  const [requestsPerMinute, setRequestsPerMinute] = useState(60);
  const [operations, setOperations] = useState<ApiSurfaceRegistration[]>([]);
  const [keys, setKeys] = useState<PartnerApiKey[]>([]);
  const [policy, setPolicy] = useState<RateLimitPolicy | undefined>();

  const listOperationsAction = useAsyncAction(async () => {
    const loaded = await listApiOperations();
    setOperations(loaded);
    return loaded;
  });

  const classifyAction = useAsyncAction(async () => {
    const saved = await classifyApiOperation(operationId, {
      ownerCapability,
      classification,
      apiVersion,
      tenantId,
      actorId,
    });
    setOperations((current) => [
      saved,
      ...current.filter((item) => item.operationId !== saved.operationId),
    ]);
    return saved;
  });

  const deprecationAction = useAsyncAction(async () => {
    const saved = await scheduleApiDeprecation(operationId, {
      deprecationWindowFrom,
      deprecationWindowTo,
      migrationNote,
      actorId,
    });
    setOperations((current) =>
      current.map((item) => (item.operationId === saved.operationId ? saved : item)),
    );
    return saved;
  });

  const retirementAction = useAsyncAction(async () => {
    const saved = await retireApiOperation(operationId, actorId);
    setOperations((current) =>
      current.map((item) => (item.operationId === saved.operationId ? saved : item)),
    );
    return saved;
  });

  const listKeysAction = useAsyncAction(async () => {
    const loaded = await listPartnerApiKeys(tenantId ?? "");
    setKeys(loaded);
    return loaded;
  });

  const issueKeyAction = useAsyncAction(async () => {
    const saved = await issuePartnerApiKey({
      tenantId: tenantId ?? "",
      consumerName,
      grantedScopes: parseScopes(grantedScopes),
      actorId,
    });
    setKeys((current) => [saved, ...current.filter((item) => item.keyId !== saved.keyId)]);
    setKeyId(saved.keyId);
    return saved;
  });

  const revokeKeyAction = useAsyncAction(async () => {
    const saved = await revokePartnerApiKey(keyId, actorId);
    setKeys((current) => current.map((item) => (item.keyId === saved.keyId ? saved : item)));
    return saved;
  });

  const rateLimitAction = useAsyncAction(async () => {
    const saved = await setRateLimitPolicy(classification, { requestsPerMinute, actorId });
    setPolicy(saved);
    return saved;
  });

  return (
    <section aria-labelledby="api-management-heading">
      <h2 id="api-management-heading">{labels.apiManagement.heading}</h2>
      <p>{labels.apiManagement.description}</p>
      <ScopeIndicator />
      {!tenantId ? (
        <p className="status-banner status-banner--error">{labels.shared.tenantRequired}</p>
      ) : null}

      <div className="panel">
        <h3>{labels.apiManagement.loadOperations}</h3>
        <button
          type="button"
          disabled={listOperationsAction.status === "loading"}
          onClick={() => listOperationsAction.run()}
        >
          {labels.apiManagement.loadOperations}
        </button>
        <StatusBanner
          status={listOperationsAction.status}
          errorMessage={listOperationsAction.errorMessage}
          successMessage={labels.shared.loaded}
        />
        {operations.length > 0 ? (
          <table>
            <caption>{labels.apiManagement.heading}</caption>
            <thead>
              <tr>
                <th scope="col">{labels.apiManagement.operationId}</th>
                <th scope="col">{labels.apiManagement.ownerCapability}</th>
                <th scope="col">{labels.apiManagement.classification}</th>
                <th scope="col">{labels.apiManagement.apiVersion}</th>
                <th scope="col">{labels.shared.status}</th>
              </tr>
            </thead>
            <tbody>
              {operations.map((operation) => (
                <tr key={operation.registrationId}>
                  <td>
                    <button
                      type="button"
                      className="link-button"
                      onClick={() => {
                        setOperationId(operation.operationId);
                        setClassification(operation.classification);
                      }}
                    >
                      {operation.operationId}
                    </button>
                  </td>
                  <td>{operation.ownerCapability}</td>
                  <td>{operation.classification}</td>
                  <td>{operation.apiVersion}</td>
                  <td>
                    <span className={statusClass(operation.deprecationStatus)}>
                      {operation.deprecationStatus}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}
      </div>

      <div className="panel">
        <h3>{labels.apiManagement.classification}</h3>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            classifyAction.run();
          }}
        >
          <label htmlFor="api-operation-id">{labels.apiManagement.operationId}</label>
          <input
            id="api-operation-id"
            value={operationId}
            onChange={(event) => setOperationId(event.target.value)}
          />
          <label htmlFor="api-owner-capability">{labels.apiManagement.ownerCapability}</label>
          <input
            id="api-owner-capability"
            value={ownerCapability}
            onChange={(event) => setOwnerCapability(event.target.value)}
          />
          <label htmlFor="api-classification">{labels.apiManagement.classification}</label>
          <input
            id="api-classification"
            value={classification}
            onChange={(event) => setClassification(event.target.value)}
          />
          <label htmlFor="api-version">{labels.apiManagement.apiVersion}</label>
          <input
            id="api-version"
            value={apiVersion}
            onChange={(event) => setApiVersion(event.target.value)}
          />
          <label htmlFor="api-actor-id">{labels.shared.actorId}</label>
          <input
            id="api-actor-id"
            value={actorId}
            onChange={(event) => setActorId(event.target.value)}
          />
          <button type="submit" disabled={!operationId || classifyAction.status === "loading"}>
            {labels.shared.create}
          </button>
        </form>
        <StatusBanner
          status={classifyAction.status}
          errorMessage={classifyAction.errorMessage}
          successMessage={labels.apiManagement.classifySuccess}
        />
      </div>

      <div className="panel">
        <h3>{labels.apiManagement.deprecationFrom}</h3>
        <label htmlFor="api-deprecation-from">{labels.apiManagement.deprecationFrom}</label>
        <input
          id="api-deprecation-from"
          type="datetime-local"
          value={deprecationWindowFrom}
          onChange={(event) => setDeprecationWindowFrom(event.target.value)}
        />
        <label htmlFor="api-deprecation-to">{labels.apiManagement.deprecationTo}</label>
        <input
          id="api-deprecation-to"
          type="datetime-local"
          value={deprecationWindowTo}
          onChange={(event) => setDeprecationWindowTo(event.target.value)}
        />
        <label htmlFor="api-migration-note">{labels.apiManagement.migrationNote}</label>
        <input
          id="api-migration-note"
          value={migrationNote}
          onChange={(event) => setMigrationNote(event.target.value)}
        />
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={!operationId || deprecationAction.status === "loading"}
            onClick={() => deprecationAction.run()}
          >
            {labels.apiManagement.scheduleDeprecation}
          </button>
          <button
            type="button"
            disabled={!operationId || retirementAction.status === "loading"}
            onClick={() => retirementAction.run()}
          >
            {labels.shared.retire}
          </button>
        </div>
        <StatusBanner
          status={deprecationAction.status}
          errorMessage={deprecationAction.errorMessage}
          successMessage={labels.apiManagement.deprecationSuccess}
        />
        <StatusBanner
          status={retirementAction.status}
          errorMessage={retirementAction.errorMessage}
          successMessage={labels.apiManagement.retirementSuccess}
        />
      </div>

      <div className="panel">
        <h3>{labels.apiManagement.consumerName}</h3>
        <button
          type="button"
          disabled={!tenantId || listKeysAction.status === "loading"}
          onClick={() => listKeysAction.run()}
        >
          {labels.apiManagement.loadKeys}
        </button>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            issueKeyAction.run();
          }}
        >
          <label htmlFor="api-consumer-name">{labels.apiManagement.consumerName}</label>
          <input
            id="api-consumer-name"
            value={consumerName}
            onChange={(event) => setConsumerName(event.target.value)}
          />
          <label htmlFor="api-granted-scopes">{labels.apiManagement.grantedScopes}</label>
          <input
            id="api-granted-scopes"
            value={grantedScopes}
            onChange={(event) => setGrantedScopes(event.target.value)}
          />
          <label htmlFor="api-key-id">{labels.apiManagement.keyId}</label>
          <input id="api-key-id" value={keyId} onChange={(event) => setKeyId(event.target.value)} />
          <button
            type="submit"
            disabled={!tenantId || !consumerName || issueKeyAction.status === "loading"}
          >
            {labels.apiManagement.issueKey}
          </button>
        </form>
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={!keyId || revokeKeyAction.status === "loading"}
            onClick={() => revokeKeyAction.run()}
          >
            {labels.apiManagement.revokeKey}
          </button>
        </div>
        <StatusBanner
          status={listKeysAction.status}
          errorMessage={listKeysAction.errorMessage}
          successMessage={labels.shared.loaded}
        />
        <StatusBanner
          status={issueKeyAction.status}
          errorMessage={issueKeyAction.errorMessage}
          successMessage={labels.apiManagement.keySuccess}
        />
        <StatusBanner
          status={revokeKeyAction.status}
          errorMessage={revokeKeyAction.errorMessage}
          successMessage={labels.apiManagement.revokeSuccess}
        />
        {keys.length > 0 ? (
          <table>
            <caption>{labels.apiManagement.consumerName}</caption>
            <tbody>
              {keys.map((key) => (
                <tr key={key.keyId}>
                  <td>
                    <button
                      type="button"
                      className="link-button"
                      onClick={() => setKeyId(key.keyId)}
                    >
                      {key.keyId}
                    </button>
                  </td>
                  <td>{key.consumerName}</td>
                  <td>{key.grantedScopes.join(", ")}</td>
                  <td>
                    <span className={statusClass(key.status)}>{key.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}
      </div>

      <div className="panel">
        <h3>{labels.apiManagement.requestsPerMinute}</h3>
        <label htmlFor="api-requests-per-minute">{labels.apiManagement.requestsPerMinute}</label>
        <input
          id="api-requests-per-minute"
          type="number"
          min="1"
          value={requestsPerMinute}
          onChange={(event) => setRequestsPerMinute(Number(event.target.value))}
        />
        <button
          type="button"
          disabled={!classification || rateLimitAction.status === "loading"}
          onClick={() => rateLimitAction.run()}
        >
          {labels.apiManagement.updateRateLimit}
        </button>
        <StatusBanner
          status={rateLimitAction.status}
          errorMessage={rateLimitAction.errorMessage}
          successMessage={labels.apiManagement.rateLimitSuccess}
        />
        {policy ? (
          <p className="field-hint">
            {policy.classification}: {policy.requestsPerMinute}
          </p>
        ) : null}
      </div>
    </section>
  );
}
