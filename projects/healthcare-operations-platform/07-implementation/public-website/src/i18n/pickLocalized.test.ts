import { describe, expect, it } from "vitest";
import { pickLocalized } from "./pickLocalized";

describe("pickLocalized", () => {
  it("returns the Spanish variant for es-MX", () => {
    expect(pickLocalized("es-MX", "Glucosa", "Glucose")).toBe("Glucosa");
  });

  it("returns the English variant for en-US", () => {
    expect(pickLocalized("en-US", "Glucosa", "Glucose")).toBe("Glucose");
  });
});
