import { describe, expect, it } from "vitest";

import { createInitialNavigationState, goBack, navigate } from "../navigation/routes";

describe("mobile navigation", () => {
  it("starts on login until a session exists", () => {
    expect(createInitialNavigationState(false).currentRoute).toBe("login");
    expect(createInitialNavigationState(true).currentRoute).toBe("home");
  });

  it("tracks route history for mobile back navigation", () => {
    const state = navigate(
      navigate(createInitialNavigationState(true), "user-summary"),
      "audit-summary",
    );

    expect(state.currentRoute).toBe("audit-summary");
    expect(goBack(state).currentRoute).toBe("user-summary");
  });

  it("handles navigation state edge cases", () => {
    const state = createInitialNavigationState(true);

    // navigate to same route returns same state instance
    const sameState = navigate(state, "home");
    expect(sameState).toBe(state);

    // goBack on empty history returns same state instance
    const loginState = createInitialNavigationState(false);
    expect(goBack(loginState)).toBe(loginState);
  });
});
