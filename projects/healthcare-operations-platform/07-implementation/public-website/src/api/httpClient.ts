import type { PublicApiErrorPayload } from "./types";

/**
 * Thrown for every non-2xx response from `/api/public/**`. `code` carries the backend's stable
 * `PublicWebErrorCodes` value (e.g. `PUBLIC_RATE_LIMIT_EXCEEDED`) when the server returned the
 * shared error envelope, so callers can resolve a localized message instead of displaying the
 * server's English `message` text. `message` is kept only as a last-resort fallback.
 */
export class ApiError extends Error {
  readonly status: number;
  readonly code: string | undefined;

  constructor(status: number, code: string | undefined, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
  }

  get isRateLimited(): boolean {
    return this.status === 429;
  }
}

async function readErrorPayload(response: Response): Promise<PublicApiErrorPayload> {
  try {
    return (await response.json()) as PublicApiErrorPayload;
  } catch {
    return {};
  }
}

async function request<TResponse>(path: string, init?: RequestInit): Promise<TResponse> {
  let response: Response;
  try {
    response = await fetch(path, {
      ...init,
      headers: {
        Accept: "application/json",
        ...(init?.body ? { "Content-Type": "application/json" } : {}),
        ...init?.headers,
      },
    });
  } catch {
    throw new ApiError(0, "NETWORK_ERROR", "Network request failed.");
  }

  if (!response.ok) {
    const payload = await readErrorPayload(response);
    throw new ApiError(response.status, payload.code, payload.message ?? response.statusText);
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
