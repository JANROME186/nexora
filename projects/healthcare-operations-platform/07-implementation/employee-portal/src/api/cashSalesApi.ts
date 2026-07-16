import { get, post } from "./httpClient";
import type {
  CancelSaleRequest,
  CashSession,
  CloseCashSessionRequest,
  CreateBillingRequestRequest,
  CreateSaleRequest,
  InvoiceRequest,
  OpenCashSessionRequest,
  PaymentAllocation,
  RegisterPaymentRequest,
  Sale,
  SaleLine,
  TaxLine,
} from "./types";

const SESSIONS_BASE = "/api/revenue/cashier/sessions";
const SALES_BASE = "/api/revenue/cashier/sales";
const BILLING_REQUESTS_BASE = "/api/revenue/billing-requests";

// -- BCM-ATT-005 Cash Session Management -----------------------------------------------------------

export function listCashSessions(tenantId: string): Promise<CashSession[]> {
  const query = new URLSearchParams({ tenantId });
  return get<CashSession[]>(`${SESSIONS_BASE}?${query.toString()}`);
}

export function getCashSession(sessionId: string): Promise<CashSession> {
  return get<CashSession>(`${SESSIONS_BASE}/${encodeURIComponent(sessionId)}`);
}

export function openCashSession(request: OpenCashSessionRequest): Promise<CashSession> {
  return post<CashSession, OpenCashSessionRequest>(SESSIONS_BASE, request);
}

export function closeCashSession(
  sessionId: string,
  request: CloseCashSessionRequest,
): Promise<CashSession> {
  return post<CashSession, CloseCashSessionRequest>(
    `${SESSIONS_BASE}/${encodeURIComponent(sessionId)}/close`,
    request,
  );
}

// -- BCM-ATT-005 Sale Management -------------------------------------------------------------------

export function listSales(tenantId: string): Promise<Sale[]> {
  const query = new URLSearchParams({ tenantId });
  return get<Sale[]>(`${SALES_BASE}?${query.toString()}`);
}

export function getSale(saleId: string): Promise<Sale> {
  return get<Sale>(`${SALES_BASE}/${encodeURIComponent(saleId)}`);
}

export function listSaleLines(saleId: string): Promise<SaleLine[]> {
  return get<SaleLine[]>(`${SALES_BASE}/${encodeURIComponent(saleId)}/lines`);
}

export function listSalePayments(saleId: string): Promise<PaymentAllocation[]> {
  return get<PaymentAllocation[]>(`${SALES_BASE}/${encodeURIComponent(saleId)}/payments`);
}

export function createSale(request: CreateSaleRequest): Promise<Sale> {
  return post<Sale, CreateSaleRequest>(SALES_BASE, request);
}

export function registerPayment(
  saleId: string,
  request: RegisterPaymentRequest,
): Promise<PaymentAllocation> {
  return post<PaymentAllocation, RegisterPaymentRequest>(
    `${SALES_BASE}/${encodeURIComponent(saleId)}/payments`,
    request,
  );
}

export function cancelSale(saleId: string, request: CancelSaleRequest): Promise<Sale> {
  return post<Sale, CancelSaleRequest>(
    `${SALES_BASE}/${encodeURIComponent(saleId)}/cancel`,
    request,
  );
}

// -- BCM-ATT-008 Billing Request Management -------------------------------------------------------

export function listBillingRequests(tenantId: string): Promise<InvoiceRequest[]> {
  const query = new URLSearchParams({ tenantId });
  return get<InvoiceRequest[]>(`${BILLING_REQUESTS_BASE}?${query.toString()}`);
}

export function getBillingRequest(invoiceRequestId: string): Promise<InvoiceRequest> {
  return get<InvoiceRequest>(`${BILLING_REQUESTS_BASE}/${encodeURIComponent(invoiceRequestId)}`);
}

export function listTaxLines(invoiceRequestId: string): Promise<TaxLine[]> {
  return get<TaxLine[]>(
    `${BILLING_REQUESTS_BASE}/${encodeURIComponent(invoiceRequestId)}/tax-lines`,
  );
}

export function createBillingRequest(
  request: CreateBillingRequestRequest,
): Promise<InvoiceRequest> {
  return post<InvoiceRequest, CreateBillingRequestRequest>(BILLING_REQUESTS_BASE, request);
}

export function submitBillingRequest(invoiceRequestId: string): Promise<InvoiceRequest> {
  return post<InvoiceRequest, Record<string, never>>(
    `${BILLING_REQUESTS_BASE}/${encodeURIComponent(invoiceRequestId)}/submit`,
    {},
  );
}

export function retryBillingRequest(invoiceRequestId: string): Promise<InvoiceRequest> {
  return post<InvoiceRequest, Record<string, never>>(
    `${BILLING_REQUESTS_BASE}/${encodeURIComponent(invoiceRequestId)}/retry`,
    {},
  );
}

export function cancelBillingRequest(invoiceRequestId: string): Promise<InvoiceRequest> {
  return post<InvoiceRequest, Record<string, never>>(
    `${BILLING_REQUESTS_BASE}/${encodeURIComponent(invoiceRequestId)}/cancel`,
    {},
  );
}
