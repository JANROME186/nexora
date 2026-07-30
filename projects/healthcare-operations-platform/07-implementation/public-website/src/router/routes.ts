export const ROUTES = {
  home: "/",
  services: "/services",
  serviceDetail: "/services/:id",
  tests: "/tests",
  testDetail: "/tests/:id",
  panels: "/panels",
  panelDetail: "/panels/:id",
  preparations: "/preparations",
  preparationDetail: "/preparations/:id",
  appointmentRequest: "/appointment-request",
  quotationRequest: "/quotation-request",
  marketplace: "/marketplace",
  marketplaceDetail: "/marketplace/:id",
  privacy: "/privacy",
} as const;

export function serviceDetailPath(id: string): string {
  return `/services/${encodeURIComponent(id)}`;
}

export function testDetailPath(id: string): string {
  return `/tests/${encodeURIComponent(id)}`;
}

export function panelDetailPath(id: string): string {
  return `/panels/${encodeURIComponent(id)}`;
}

export function preparationDetailPath(id: string): string {
  return `/preparations/${encodeURIComponent(id)}`;
}

export function marketplaceDetailPath(id: string): string {
  return `/marketplace/${encodeURIComponent(id)}`;
}

/** Matches a concrete pathname against a route pattern like "/services/:id". Returns the matched
 * params, or null when the pattern does not match. */
export function matchPath(pattern: string, pathname: string): Record<string, string> | null {
  const patternSegments = pattern.split("/").filter(Boolean);
  const pathSegments = pathname.split("/").filter(Boolean);
  if (patternSegments.length !== pathSegments.length) {
    return null;
  }
  const params: Record<string, string> = {};
  for (let i = 0; i < patternSegments.length; i += 1) {
    const patternSegment = patternSegments[i];
    const pathSegment = pathSegments[i];
    if (patternSegment.startsWith(":")) {
      params[patternSegment.slice(1)] = decodeURIComponent(pathSegment);
    } else if (patternSegment !== pathSegment) {
      return null;
    }
  }
  return params;
}
