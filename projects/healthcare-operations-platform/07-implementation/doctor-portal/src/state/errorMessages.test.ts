import { describe, it, expect, vi } from "vitest";
import { resolveApiErrorMessage } from "./errorMessages";
import { ApiError } from "../api/httpClient";
import { esMX } from "../i18n/locales/es-MX";

describe("resolveApiErrorMessage", () => {
  it("returns the session-expired message and invokes the callback for a 401 ApiError", () => {
    const onSessionExpired = vi.fn();
    const message = resolveApiErrorMessage(
      new ApiError(401, "AUTHENTICATION_REQUIRED"),
      esMX,
      onSessionExpired,
    );

    expect(message).toBe(esMX.appShell.states.sessionExpired);
    expect(onSessionExpired).toHaveBeenCalledTimes(1);
  });

  it("returns the no-permission message for a 403 ApiError without expiring the session", () => {
    const onSessionExpired = vi.fn();
    const message = resolveApiErrorMessage(
      new ApiError(403, "PERMISSION_DENIED"),
      esMX,
      onSessionExpired,
    );

    expect(message).toBe(esMX.appShell.states.noPermission);
    expect(onSessionExpired).not.toHaveBeenCalled();
  });

  it("returns the generic error message for other ApiError statuses", () => {
    const onSessionExpired = vi.fn();
    const message = resolveApiErrorMessage(new ApiError(500, "boom"), esMX, onSessionExpired);

    expect(message).toBe(esMX.appShell.states.error);
    expect(onSessionExpired).not.toHaveBeenCalled();
  });

  it("returns the generic error message for a non-ApiError", () => {
    const onSessionExpired = vi.fn();
    const message = resolveApiErrorMessage(new Error("network down"), esMX, onSessionExpired);

    expect(message).toBe(esMX.appShell.states.error);
    expect(onSessionExpired).not.toHaveBeenCalled();
  });
});
