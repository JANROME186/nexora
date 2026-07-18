import type { PermissionCode } from "../auth/permissions";

export type MobileRoute =
  | "login"
  | "home"
  | "tenant-summary"
  | "laboratory-summary"
  | "branch-summary"
  | "user-summary"
  | "audit-summary"
  | "results"
  | "resultDetail"
  | "resultHistory";

/**
 * Maps each permission-gated route to the employee-portal-equivalent `PermissionCode`. The
 * mobile foundation only models 5 of the employee-portal's 27 screens today (the rest await
 * mobile screens being built out per TD-APP-001), so this is necessarily a subset mapping:
 * `login` and `home` are intentionally absent (no permission required — `login` precedes having
 * a session at all, and `home` is the authenticated landing route every role can reach).
 */
const ROUTE_TO_PERMISSION: Partial<Record<MobileRoute, PermissionCode>> = {
  "tenant-summary": "SCREEN_TENANTS",
  "laboratory-summary": "SCREEN_LABORATORIES",
  "branch-summary": "SCREEN_BRANCHES",
  "user-summary": "SCREEN_USERS",
  "audit-summary": "SCREEN_AUDIT_EVENTS",
};

const ALL_ROUTES: readonly MobileRoute[] = [
  "login",
  "home",
  "tenant-summary",
  "laboratory-summary",
  "branch-summary",
  "user-summary",
  "audit-summary",
  "results",
  "resultDetail",
  "resultHistory",
];

/**
 * Filters the full route list down to routes the given permission set may navigate to, the same
 * way the employee-portal's `AppShell` filters its tabs (enterprise-product-foundation-standard
 * `iam_permission_model`: unauthorized navigation must be hidden). Routes with no entry in
 * `ROUTE_TO_PERMISSION` (`login`, `home`) are always visible.
 */
export function visibleRoutesForPermissions(
  permissions: ReadonlySet<PermissionCode>,
): MobileRoute[] {
  return ALL_ROUTES.filter((route) => {
    const requiredPermission = ROUTE_TO_PERMISSION[route];
    return requiredPermission === undefined || permissions.has(requiredPermission);
  });
}

export type NavigationState = {
  currentRoute: MobileRoute;
  history: MobileRoute[];
};

export function createInitialNavigationState(isAuthenticated: boolean): NavigationState {
  return {
    currentRoute: isAuthenticated ? "home" : "login",
    history: [],
  };
}

export function navigate(state: NavigationState, route: MobileRoute): NavigationState {
  if (route === state.currentRoute) {
    return state;
  }
  return {
    currentRoute: route,
    history: [...state.history, state.currentRoute],
  };
}

export function goBack(state: NavigationState): NavigationState {
  const previousRoute = state.history.at(-1);
  if (!previousRoute) {
    return state;
  }
  return {
    currentRoute: previousRoute,
    history: state.history.slice(0, -1),
  };
}
