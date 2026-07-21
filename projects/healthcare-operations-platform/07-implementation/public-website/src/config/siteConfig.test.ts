import { describe, expect, it } from "vitest";
import { siteConfig } from "./siteConfig";

describe("siteConfig", () => {
  it("falls back to the local-solution seed fixture identity", () => {
    expect(siteConfig.tenantId).toBe("tenant-local");
    expect(siteConfig.laboratoryId).toBe("lab-local");
    expect(siteConfig.branches).toHaveLength(1);
    expect(siteConfig.branches[0].branchId).toBe("branch-local");
  });
});
