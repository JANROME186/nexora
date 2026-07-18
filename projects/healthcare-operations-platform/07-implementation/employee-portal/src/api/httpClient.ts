import { readSessionHeaders } from "../state/SessionContext";

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

interface ApiErrorPayload {
  status?: number;
  message?: string;
}

async function parseErrorMessage(response: Response): Promise<string> {
  try {
    const payload = (await response.json()) as ApiErrorPayload;
    if (payload && typeof payload.message === "string") {
      return payload.message;
    }
  } catch {
    // Response body was not JSON. Fall back to the status text below.
  }
  return response.statusText || "Request failed.";
}

async function request<TResponse>(path: string, init?: RequestInit): Promise<TResponse> {
  const response = await fetch(path, {
    ...init,
    headers: {
      Accept: "application/json",
      ...(init?.body ? { "Content-Type": "application/json" } : {}),
      ...readSessionHeaders(),
      ...init?.headers,
    },
  });

  if (!response.ok) {
    throw new ApiError(response.status, await parseErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as TResponse;
  }

  return (await response.json()) as TResponse;
}

export function get<TResponse>(path: string): Promise<TResponse> {
  return request<TResponse>(path, { method: "GET" });
}

export function post<TResponse, TBody = unknown>(path: string, body: TBody): Promise<TResponse> {
  return request<TResponse>(path, { method: "POST", body: JSON.stringify(body) });
}

export function put<TResponse, TBody = unknown>(path: string, body: TBody): Promise<TResponse> {
  return request<TResponse>(path, { method: "PUT", body: JSON.stringify(body) });
}
