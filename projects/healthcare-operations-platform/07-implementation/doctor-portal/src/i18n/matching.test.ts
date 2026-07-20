import { describe, it, expect } from "vitest";
import { confidenceClass } from "./matching";

describe("matching utils", () => {
  it("returns correct confidence classes", () => {
    expect(confidenceClass(0.9)).toBe("confidence-badge confidence-badge--high");
    expect(confidenceClass(0.7)).toBe("confidence-badge confidence-badge--medium");
    expect(confidenceClass(0.3)).toBe("confidence-badge confidence-badge--low");
  });
});
