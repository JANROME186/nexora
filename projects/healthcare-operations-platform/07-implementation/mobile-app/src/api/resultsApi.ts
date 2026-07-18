import type { DeliveryTicketResponse, ResultHistoryResponse } from "./types";
import type { FetchLike } from "./platformFoundationApi";

export type ResultsApiOptions = {
  baseUrl: string;
  fetcher?: FetchLike;
  getToken?: () => string | null;
  getSessionHeaders?: () => Record<string, string>;
};

export type ResultsApi = ReturnType<typeof createResultsApi>;

export function createResultsApi(options: ResultsApiOptions) {
  const fetcher = options.fetcher ?? fetch;
  const baseUrl = options.baseUrl.replace(/\/$/, "");

  async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const token = options.getToken?.();
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    const sessionHeaders = options.getSessionHeaders?.() ?? {};
    for (const [name, value] of Object.entries(sessionHeaders)) {
      headers.set(name, value);
    }

    const response = await fetcher(`${baseUrl}${path}`, { ...init, headers });
    if (!response.ok) {
      throw new Error(`Results API request failed with status ${response.status}.`);
    }
    if (response.status === 204) {
      return undefined as T;
    }
    return response.json() as Promise<T>;
  }

  return {
    listResults: (patientId: string) =>
      request<DeliveryTicketResponse[]>(
        `/api/delivery/results?patientId=${encodeURIComponent(patientId)}`,
      ),
    getResultDetail: (deliveryTicketId: string) =>
      request<DeliveryTicketResponse>(
        `/api/delivery/results/${encodeURIComponent(deliveryTicketId)}`,
      ),
    recordResultViewed: (deliveryTicketId: string) =>
      request<void>(`/api/delivery/results/${encodeURIComponent(deliveryTicketId)}/views`, {
        method: "POST",
      }),
    getResultHistory: (patientId: string, testId?: string) => {
      const params = new URLSearchParams();
      params.set("patientId", patientId);
      if (testId) {
        params.set("testId", testId);
      }
      return request<ResultHistoryResponse[]>(`/api/history/results?${params.toString()}`);
    },
  };
}
