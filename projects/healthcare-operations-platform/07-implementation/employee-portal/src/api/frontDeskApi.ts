import { get, post } from "./httpClient";
import type {
  AcceptOrderRequest,
  CancelOrderRequest,
  ConfirmReceptionIdentityRequest,
  CreateDiagnosticOrderRequest,
  DiagnosticOrder,
  OrderLine,
  PriceOrderRequest,
  ReceptionVisit,
  StartReceptionVisitRequest,
  UpdateReceptionPriorityRequest,
} from "./types";

const RECEPTION_VISITS_BASE = "/api/care-delivery/reception-visits";
const DIAGNOSTIC_ORDERS_BASE = "/api/clinical-operations/diagnostic-orders";

// -- BCM-ATT-003 Reception Management --------------------------------------------------------------

export function listReceptionVisits(tenantId: string): Promise<ReceptionVisit[]> {
  const query = new URLSearchParams({ tenantId });
  return get<ReceptionVisit[]>(`${RECEPTION_VISITS_BASE}?${query.toString()}`);
}

export function getReceptionVisit(visitId: string): Promise<ReceptionVisit> {
  return get<ReceptionVisit>(`${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}`);
}

export function startReceptionVisit(request: StartReceptionVisitRequest): Promise<ReceptionVisit> {
  return post<ReceptionVisit, StartReceptionVisitRequest>(RECEPTION_VISITS_BASE, request);
}

export function confirmReceptionIdentity(
  visitId: string,
  request: ConfirmReceptionIdentityRequest,
): Promise<ReceptionVisit> {
  return post<ReceptionVisit, ConfirmReceptionIdentityRequest>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/confirm-identity`,
    request,
  );
}

export function advanceReceptionToAdmission(visitId: string): Promise<ReceptionVisit> {
  return post<ReceptionVisit, Record<string, never>>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/advance-to-admission`,
    {},
  );
}

export function updateReceptionPriority(
  visitId: string,
  request: UpdateReceptionPriorityRequest,
): Promise<ReceptionVisit> {
  return post<ReceptionVisit, UpdateReceptionPriorityRequest>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/priority`,
    request,
  );
}

export function abandonReceptionVisit(visitId: string): Promise<ReceptionVisit> {
  return post<ReceptionVisit, Record<string, never>>(
    `${RECEPTION_VISITS_BASE}/${encodeURIComponent(visitId)}/abandon`,
    {},
  );
}

// -- BCM-LAB-001 Diagnostic Order Management --------------------------------------------------------

export function listDiagnosticOrders(tenantId: string): Promise<DiagnosticOrder[]> {
  const query = new URLSearchParams({ tenantId });
  return get<DiagnosticOrder[]>(`${DIAGNOSTIC_ORDERS_BASE}?${query.toString()}`);
}

export function getDiagnosticOrder(orderId: string): Promise<DiagnosticOrder> {
  return get<DiagnosticOrder>(`${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}`);
}

export function listDiagnosticOrderLines(orderId: string): Promise<OrderLine[]> {
  return get<OrderLine[]>(`${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/lines`);
}

export function createDiagnosticOrder(
  request: CreateDiagnosticOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, CreateDiagnosticOrderRequest>(DIAGNOSTIC_ORDERS_BASE, request);
}

export function priceDiagnosticOrder(
  orderId: string,
  request?: PriceOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, PriceOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/price`,
    request ?? {},
  );
}

export function acceptDiagnosticOrder(
  orderId: string,
  request?: AcceptOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, AcceptOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/accept`,
    request ?? {},
  );
}

export function cancelDiagnosticOrder(
  orderId: string,
  request: CancelOrderRequest,
): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, CancelOrderRequest>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/cancel`,
    request,
  );
}

export function completeDiagnosticOrder(orderId: string): Promise<DiagnosticOrder> {
  return post<DiagnosticOrder, Record<string, never>>(
    `${DIAGNOSTIC_ORDERS_BASE}/${encodeURIComponent(orderId)}/complete`,
    {},
  );
}
