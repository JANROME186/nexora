/**
 * Integration endpoint administration screen (MVP-MOD-008-FE-001, BCM-PLT-004).
 *
 * Exposes endpoint registration/retirement plus message receive/detail/retry actions backed by
 * IntegrationEndpointController and IntegrationMessageController.
 */
import { useState } from "react";
import {
  getIntegrationMessage,
  listIntegrationEndpoints,
  receiveIntegrationMessage,
  registerIntegrationEndpoint,
  retireIntegrationEndpoint,
  retryIntegrationMessage,
} from "../../api/integrationMigrationApi";
import type { IntegrationEndpoint, IntegrationMessageDetail } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

function statusClass(status: string): string {
  const normalized = status.toLowerCase();
  if (normalized.includes("active") || normalized.includes("acknowledged")) {
    return "catalog-status catalog-status--published";
  }
  if (
    normalized.includes("dead") ||
    normalized.includes("failed") ||
    normalized.includes("retired")
  ) {
    return "catalog-status catalog-status--retired";
  }
  return "catalog-status catalog-status--draft";
}

function formatFields(fields?: Record<string, string>): string {
  if (!fields || Object.keys(fields).length === 0) {
    return "-";
  }
  return Object.entries(fields)
    .map(([key, value]) => `${key}: ${value}`)
    .join("; ");
}

export function IntegrationEndpointsScreen() {
  const { t } = useLocale();
  const labels = t.integrationMigration;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId } = scope;

  const [endpointName, setEndpointName] = useState("");
  const [protocol, setProtocol] = useState("HL7V2");
  const [direction, setDirection] = useState("INBOUND");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [endpoints, setEndpoints] = useState<IntegrationEndpoint[]>([]);
  const [selectedEndpoint, setSelectedEndpoint] = useState<IntegrationEndpoint | undefined>();
  const [externalMessageId, setExternalMessageId] = useState("");
  const [rawPayload, setRawPayload] = useState("");
  const [messageId, setMessageId] = useState("");
  const [messageDetail, setMessageDetail] = useState<IntegrationMessageDetail | undefined>();

  const listAction = useAsyncAction(async () => {
    const loaded = await listIntegrationEndpoints(tenantId ?? "");
    setEndpoints(loaded);
    setSelectedEndpoint(undefined);
    return loaded;
  });

  const registerAction = useAsyncAction(async () => {
    const created = await registerIntegrationEndpoint({
      tenantId: tenantId ?? "",
      laboratoryId: laboratoryId ?? "",
      endpointName,
      protocol,
      direction,
      actorId,
    });
    setEndpoints((current) => [
      created,
      ...current.filter((item) => item.endpointId !== created.endpointId),
    ]);
    setSelectedEndpoint(created);
    return created;
  });

  const retireAction = useAsyncAction(async () => {
    const updated = await retireIntegrationEndpoint(selectedEndpoint?.endpointId ?? "", actorId);
    setEndpoints((current) =>
      current.map((item) => (item.endpointId === updated.endpointId ? updated : item)),
    );
    setSelectedEndpoint(updated);
    return updated;
  });

  const receiveAction = useAsyncAction(async () => {
    const received = await receiveIntegrationMessage(selectedEndpoint?.endpointId ?? "", {
      externalMessageId,
      rawPayload,
      actorId,
    });
    setMessageId(received.messageId);
    return received;
  });

  const detailAction = useAsyncAction(async () => {
    const detail = await getIntegrationMessage(messageId);
    setMessageDetail(detail);
    return detail;
  });

  const retryAction = useAsyncAction(async () => {
    const detail = await retryIntegrationMessage(messageId, { rawPayload, actorId });
    setMessageDetail(detail);
    return detail;
  });

  const canUseScope = Boolean(tenantId && laboratoryId);

  return (
    <section aria-labelledby="integration-endpoints-heading">
      <h2 id="integration-endpoints-heading">{labels.integrationEndpoints.heading}</h2>
      <p>{labels.integrationEndpoints.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.laboratoryRequired}</p>
      ) : null}

      <div className="panel">
        <h3>{labels.shared.create}</h3>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            registerAction.run();
          }}
        >
          <label htmlFor="integration-endpoint-name">
            {labels.integrationEndpoints.endpointName}
          </label>
          <input
            id="integration-endpoint-name"
            value={endpointName}
            onChange={(event) => setEndpointName(event.target.value)}
          />
          <label htmlFor="integration-protocol">{labels.integrationEndpoints.protocol}</label>
          <input
            id="integration-protocol"
            value={protocol}
            onChange={(event) => setProtocol(event.target.value)}
          />
          <label htmlFor="integration-direction">{labels.integrationEndpoints.direction}</label>
          <select
            id="integration-direction"
            value={direction}
            onChange={(event) => setDirection(event.target.value)}
          >
            <option value="INBOUND">INBOUND</option>
            <option value="OUTBOUND">OUTBOUND</option>
          </select>
          <label htmlFor="integration-actor-id">{labels.shared.actorId}</label>
          <input
            id="integration-actor-id"
            value={actorId}
            onChange={(event) => setActorId(event.target.value)}
          />
          <button
            type="submit"
            disabled={!canUseScope || !endpointName || registerAction.status === "loading"}
          >
            {labels.shared.create}
          </button>
        </form>
        <StatusBanner
          status={registerAction.status}
          errorMessage={registerAction.errorMessage}
          successMessage={labels.integrationEndpoints.registerSuccess}
        />
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.integrationEndpoints.loadEndpoints}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && endpoints.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      {endpoints.length > 0 ? (
        <table>
          <caption>{labels.integrationEndpoints.heading}</caption>
          <thead>
            <tr>
              <th scope="col">{labels.integrationEndpoints.endpointId}</th>
              <th scope="col">{labels.integrationEndpoints.endpointName}</th>
              <th scope="col">{labels.integrationEndpoints.protocol}</th>
              <th scope="col">{labels.integrationEndpoints.direction}</th>
              <th scope="col">{labels.shared.status}</th>
            </tr>
          </thead>
          <tbody>
            {endpoints.map((endpoint) => (
              <tr key={endpoint.endpointId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => setSelectedEndpoint(endpoint)}
                  >
                    {endpoint.endpointId}
                  </button>
                </td>
                <td>{endpoint.endpointName}</td>
                <td>{endpoint.protocol}</td>
                <td>{endpoint.direction}</td>
                <td>
                  <span className={statusClass(endpoint.status)}>{endpoint.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      <div className="panel">
        <h3>{selectedEndpoint?.endpointId ?? labels.shared.selectFirst}</h3>
        <button
          type="button"
          disabled={!selectedEndpoint || retireAction.status === "loading"}
          onClick={() => retireAction.run()}
        >
          {labels.shared.retire}
        </button>
        <StatusBanner
          status={retireAction.status}
          errorMessage={retireAction.errorMessage}
          successMessage={labels.integrationEndpoints.retireSuccess}
        />

        <form
          onSubmit={(event) => {
            event.preventDefault();
            receiveAction.run();
          }}
        >
          <label htmlFor="integration-external-message-id">
            {labels.integrationEndpoints.externalMessageId}
          </label>
          <input
            id="integration-external-message-id"
            value={externalMessageId}
            onChange={(event) => setExternalMessageId(event.target.value)}
          />
          <label htmlFor="integration-raw-payload">{labels.integrationEndpoints.rawPayload}</label>
          <input
            id="integration-raw-payload"
            value={rawPayload}
            onChange={(event) => setRawPayload(event.target.value)}
          />
          <button
            type="submit"
            disabled={!selectedEndpoint || !externalMessageId || receiveAction.status === "loading"}
          >
            {labels.integrationEndpoints.sendMessage}
          </button>
        </form>
        <StatusBanner
          status={receiveAction.status}
          errorMessage={receiveAction.errorMessage}
          successMessage={labels.integrationEndpoints.receiveSuccess}
        />
      </div>

      <div className="panel">
        <h3>{labels.integrationEndpoints.loadMessage}</h3>
        <label htmlFor="integration-message-id">{labels.integrationEndpoints.messageId}</label>
        <input
          id="integration-message-id"
          value={messageId}
          onChange={(event) => setMessageId(event.target.value)}
        />
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={!messageId || detailAction.status === "loading"}
            onClick={() => detailAction.run()}
          >
            {labels.shared.load}
          </button>
          <button
            type="button"
            disabled={!messageId || retryAction.status === "loading"}
            onClick={() => retryAction.run()}
          >
            {labels.shared.retry}
          </button>
        </div>
        <StatusBanner
          status={detailAction.status}
          errorMessage={detailAction.errorMessage}
          successMessage={labels.shared.loaded}
        />
        <StatusBanner
          status={retryAction.status}
          errorMessage={retryAction.errorMessage}
          successMessage={labels.integrationEndpoints.retrySuccess}
        />
        {messageDetail ? (
          <table>
            <tbody>
              <tr>
                <th scope="row">{labels.integrationEndpoints.normalizationStatus}</th>
                <td>
                  <span className={statusClass(messageDetail.normalizationStatus)}>
                    {messageDetail.normalizationStatus}
                  </span>
                </td>
              </tr>
              <tr>
                <th scope="row">{labels.integrationEndpoints.retryCount}</th>
                <td>{messageDetail.retryCount}</td>
              </tr>
              <tr>
                <th scope="row">canonicalFields</th>
                <td>{formatFields(messageDetail.canonicalFields)}</td>
              </tr>
            </tbody>
          </table>
        ) : null}
      </div>
    </section>
  );
}
