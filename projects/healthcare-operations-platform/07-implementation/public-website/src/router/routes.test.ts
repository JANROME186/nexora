import { describe, expect, it } from "vitest";
import {
  marketplaceDetailPath,
  matchPath,
  panelDetailPath,
  preparationDetailPath,
  ROUTES,
  serviceDetailPath,
  testDetailPath,
} from "./routes";

describe("routes", () => {
  it("matches the home route only for the root path", () => {
    expect(matchPath(ROUTES.home, "/")).toEqual({});
    expect(matchPath(ROUTES.home, "/services")).toBeNull();
  });

  it("matches a static route", () => {
    expect(matchPath(ROUTES.services, "/services")).toEqual({});
    expect(matchPath(ROUTES.marketplace, "/marketplace")).toEqual({});
    expect(matchPath(ROUTES.services, "/services/abc")).toBeNull();
  });

  it("matches a parameterized detail route and extracts the param", () => {
    expect(matchPath(ROUTES.serviceDetail, "/services/svc-1")).toEqual({ id: "svc-1" });
    expect(matchPath(ROUTES.testDetail, "/tests/test-1")).toEqual({ id: "test-1" });
    expect(matchPath(ROUTES.panelDetail, "/panels/panel-1")).toEqual({ id: "panel-1" });
    expect(matchPath(ROUTES.preparationDetail, "/preparations/prep-1")).toEqual({ id: "prep-1" });
    expect(matchPath(ROUTES.marketplaceDetail, "/marketplace/pkg-1")).toEqual({ id: "pkg-1" });
  });

  it("decodes URL-encoded params", () => {
    expect(matchPath(ROUTES.serviceDetail, "/services/svc%201")).toEqual({ id: "svc 1" });
    expect(matchPath(ROUTES.marketplaceDetail, "/marketplace/pkg%201")).toEqual({ id: "pkg 1" });
  });

  it("returns null when segment counts differ", () => {
    expect(matchPath(ROUTES.serviceDetail, "/services")).toBeNull();
    expect(matchPath(ROUTES.serviceDetail, "/services/svc-1/extra")).toBeNull();
  });

  it("builds encoded detail paths", () => {
    expect(serviceDetailPath("svc 1")).toBe("/services/svc%201");
    expect(testDetailPath("test 1")).toBe("/tests/test%201");
    expect(panelDetailPath("panel 1")).toBe("/panels/panel%201");
    expect(preparationDetailPath("prep 1")).toBe("/preparations/prep%201");
    expect(marketplaceDetailPath("pkg 1")).toBe("/marketplace/pkg%201");
  });
});
