import { describe, expect, it } from "vitest";
import { ApiError } from "../../api/httpClient";
import { esMX } from "../../i18n/locales/es-MX";
import { resolveErrorMessage } from "./resolveErrorMessage";

describe("resolveErrorMessage", () => {
  it("resolves a known error code to its localized message", () => {
    const error = new ApiError(404, "PUBLIC_CATALOG_NOT_PUBLISHED", "not found");
    expect(resolveErrorMessage(error, esMX.errors)).toBe(esMX.errors.PUBLIC_CATALOG_NOT_PUBLISHED);
  });

  it("resolves a rate-limit error without a recognized code via isRateLimited", () => {
    const error = new ApiError(429, undefined, "too many");
    expect(resolveErrorMessage(error, esMX.errors)).toBe(esMX.errors.PUBLIC_RATE_LIMIT_EXCEEDED);
  });

  it("resolves a network error to the network message", () => {
    const error = new ApiError(0, "NETWORK_ERROR", "offline");
    expect(resolveErrorMessage(error, esMX.errors)).toBe(esMX.errors.network);
  });

  it("falls back to the generic message for an unknown ApiError code", () => {
    const error = new ApiError(400, "SOMETHING_ELSE", "oops");
    expect(resolveErrorMessage(error, esMX.errors)).toBe(esMX.errors.generic);
  });

  it("falls back to the generic message for a non-ApiError value", () => {
    expect(resolveErrorMessage(new Error("boom"), esMX.errors)).toBe(esMX.errors.generic);
    expect(resolveErrorMessage("boom", esMX.errors)).toBe(esMX.errors.generic);
  });
});
