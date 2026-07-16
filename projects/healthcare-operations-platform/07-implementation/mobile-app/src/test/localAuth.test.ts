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
});
