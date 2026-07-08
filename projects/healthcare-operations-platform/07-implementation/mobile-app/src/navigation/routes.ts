export type MobileRoute =
  | "login"
  | "home"
  | "tenant-summary"
  | "laboratory-summary"
  | "branch-summary"
  | "user-summary"
  | "audit-summary";

export type NavigationState = {
  currentRoute: MobileRoute;
  history: MobileRoute[];
};

export function createInitialNavigationState(isAuthenticated: boolean): NavigationState {
  return {
    currentRoute: isAuthenticated ? "home" : "login",
    history: []
  };
}

export function navigate(state: NavigationState, route: MobileRoute): NavigationState {
  if (route === state.currentRoute) {
    return state;
  }
  return {
    currentRoute: route,
    history: [...state.history, state.currentRoute]
  };
}

export function goBack(state: NavigationState): NavigationState {
  const previousRoute = state.history.at(-1);
  if (!previousRoute) {
    return state;
  }
  return {
    currentRoute: previousRoute,
    history: state.history.slice(0, -1)
  };
}
