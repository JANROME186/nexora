import { describe, expect, it } from "vitest";

import { createLocalAuthService } from "../auth/localAuth";
import { createMemorySessionStore } from "../auth/sessionStore";

describe("local auth service", () => {
  it("creates and stores a local mobile session", () => {
    const store = createMemorySessionStore();
    const auth = createLocalAuthService(store, () => new Date("2026-07-08T18:00:00.000Z"));

    const session = auth.login({
      tenantId: "tenant-1",
      userId: "user-1",
      displayName: "Mobile Admin",
      email: "MOBILE.ADMIN@example.test",
    });

    expect(session.token).toBe("local-session:tenant-1:user-1");
    expect(session.email).toBe("mobile.admin@example.test");
    expect(auth.currentSession()).toEqual(session);
  });

  it("clears the current session on logout", () => {
    const auth = createLocalAuthService(createMemorySessionStore());
    auth.login({
      tenantId: "tenant-1",
      userId: "user-1",
      displayName: "Mobile Admin",
      email: "mobile.admin@example.test",
    });

    auth.logout();

    expect(auth.currentSession()).toBeNull();
  });

  it("sources validation errors from the default es-MX locale", () => {
    const auth = createLocalAuthService(createMemorySessionStore());

    expect(() =>
      auth.login({ tenantId: "", userId: "user-1", displayName: "Mobile Admin", email: "a@b.c" }),
    ).toThrow("El id del tenant es obligatorio.");
  });

  it("sources validation errors from en-US when that locale is requested", () => {
    const auth = createLocalAuthService(createMemorySessionStore(), () => new Date(), "en-US");

    expect(() =>
      auth.login({ tenantId: "", userId: "user-1", displayName: "Mobile Admin", email: "a@b.c" }),
    ).toThrow("Tenant id is required.");
  });
});
