import { get } from "./httpClient";
import type { DiagnosticOrder } from "./types";

/**
 * Lists the diagnostic orders on which {@code doctorId} is captured as the referring doctor
 * (COM-MOD-009-PORTAL-002). Server-side filtered by `DiagnosticOrderController` / the
 * `HopAuthorizationInterceptor` REFERRING_DOCTOR self-access block: the backend never returns
 * another doctor's orders for this call, so no client-side filtering is required or performed.
 */
export function listReferredOrders(tenantId: string, doctorId: string): Promise<DiagnosticOrder[]> {
  const params = new URLSearchParams({ tenantId, doctorId });
  return get(`/api/clinical-operations/diagnostic-orders?${params.toString()}`);
}
