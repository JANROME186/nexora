import type { FetchLike } from "./platformFoundationApi";
import type {
  DeliveryTicketResponse,
  PatientAppointmentResponse,
  PatientNotificationResponse,
  PatientOrderResponse,
  PatientProfileResponse,
} from "./types";

export type PatientMobileApiOptions = {
  baseUrl: string;
  fetcher?: FetchLike;
  getToken?: () => string | null;
  getSessionHeaders?: () => Record<string, string>;
};

export type PatientMobileApi = ReturnType<typeof createPatientMobileApi>;

export function createPatientMobileApi(options: PatientMobileApiOptions) {
  const fetcher = options.fetcher ?? fetch;
  const baseUrl = options.baseUrl.replace(/\/$/, "");

  async function request<T>(path: string): Promise<T> {
    const token = options.getToken?.();
    const headers = new Headers();
    headers.set("Accept", "application/json");
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    const sessionHeaders = options.getSessionHeaders?.() ?? {};
    for (const [name, value] of Object.entries(sessionHeaders)) {
      headers.set(name, value);
    }

    const response = await fetcher(`${baseUrl}${path}`, { method: "GET", headers });
    if (!response.ok) {
      throw new Error(`Patient mobile API request failed with status ${response.status}.`);
    }
    return response.json() as Promise<T>;
  }

  function patientPath(path: string, patientId: string) {
    const params = new URLSearchParams({ patientId });
    return `${path}?${params.toString()}`;
  }

  return {
    getProfile: (patientId: string) =>
      request<PatientProfileResponse>(
        `/api/portal/patient/profile/${encodeURIComponent(patientId)}`,
      ),
    listAppointments: (patientId: string) =>
      request<PatientAppointmentResponse[]>(
        patientPath("/api/portal/patient/appointments", patientId),
      ),
    listOrders: (patientId: string) =>
      request<PatientOrderResponse[]>(
        patientPath("/api/clinical-operations/diagnostic-orders", patientId),
      ),
    listResults: (patientId: string) =>
      request<DeliveryTicketResponse[]>(patientPath("/api/delivery/results", patientId)),
    listNotifications: (patientId: string) =>
      request<PatientNotificationResponse[]>(
        patientPath("/api/portal/patient/notifications", patientId),
      ),
  };
}
