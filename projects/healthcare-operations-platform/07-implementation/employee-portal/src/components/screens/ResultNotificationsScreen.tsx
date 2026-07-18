/**
 * Result Notification History screen (MVP-MOD-007-FE-001, BCM-RES-007 Result Notifications).
 *
 * Implements:
 *   SCR-RNT-007-01 Result Notification History (/results/{resultId}/notifications)
 *
 * Shows the dispatch status history (DispatchStatusBadge) for all notification requests
 * associated with a given result ID.
 */
import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { listResultNotifications } from "../../api/resultsDeliveryApi";
import { MESSAGES } from "../../i18n/messages";
import type { ResultNotificationRequest } from "../../api/types";

function dispatchStatusClass(status: string): string {
  const s = status.toLowerCase();
  if (s === "delivered") return "catalog-status catalog-status--released";
  if (s === "failed") return "catalog-status catalog-status--critical";
  if (s === "dispatched") return "catalog-status catalog-status--medically-validated";
  return "catalog-status catalog-status--pending";
}

export function ResultNotificationsScreen() {
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [resultId, setResultId] = useState("");
  const [notifications, setNotifications] = useState<ResultNotificationRequest[]>([]);

  const listAction = useAsyncAction(async () => {
    if (!resultId.trim() || !tenantId) throw new Error(MESSAGES.selectResultFirst);
    const loaded = await listResultNotifications(resultId.trim(), tenantId);
    setNotifications(loaded);
    return loaded;
  });

  async function handleLoadNotifications() {
    await listAction.run();
  }

  return (
    <section aria-labelledby="result-notifications-heading">
      <h2 id="result-notifications-heading">Result Notification History</h2>
      <ScopeIndicator />
      {!tenantId && (
        <p className="status-banner status-banner--error">
          Select a tenant before viewing notification history.
        </p>
      )}

      <label htmlFor="notif-result-id">Result ID</label>
      <input
        id="notif-result-id"
        type="text"
        value={resultId}
        onChange={(e) => setResultId(e.target.value)}
        placeholder="Enter result ID"
      />
      <button
        type="button"
        disabled={!resultId.trim() || !tenantId || listAction.status === "loading"}
        onClick={handleLoadNotifications}
        style={{ marginLeft: "0.5rem" }}
      >
        Load Notifications
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Notifications loaded."
      />

      {listAction.status === "success" && notifications.length === 0 ? (
        <p className="empty-state">{MESSAGES.noNotificationsFound}</p>
      ) : null}

      {notifications.length > 0 ? (
        <table>
          <caption>Notification Dispatch History</caption>
          <thead>
            <tr>
              <th scope="col">Notification ID</th>
              <th scope="col">Recipient Type</th>
              <th scope="col">Recipient ID</th>
              <th scope="col">Channel</th>
              <th scope="col">Dispatch Status</th>
              <th scope="col">Dispatched At</th>
              <th scope="col">Delivered At</th>
            </tr>
          </thead>
          <tbody>
            {notifications.map((n) => (
              <tr key={n.notificationRequestId}>
                <td>{n.notificationRequestId}</td>
                <td>{n.recipientType}</td>
                <td>{n.recipientId}</td>
                <td>{n.channel}</td>
                <td>
                  <span className={dispatchStatusClass(n.status)}>{n.status}</span>
                </td>
                <td>{n.dispatchedAt ? new Date(n.dispatchedAt).toLocaleString() : "—"}</td>
                <td>{n.deliveredAt ? new Date(n.deliveredAt).toLocaleString() : "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}
    </section>
  );
}
