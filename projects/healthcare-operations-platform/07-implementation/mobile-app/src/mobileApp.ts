import { createPlatformFoundationApi } from "./api/platformFoundationApi";
import type { PlatformFoundationApi } from "./api/platformFoundationApi";
import { createLocalAuthService } from "./auth/localAuth";
import type { LocalAuthService } from "./auth/localAuth";
import { createMemorySessionStore } from "./auth/sessionStore";
import type { SessionStore } from "./auth/sessionStore";
import { createInitialNavigationState, navigate } from "./navigation/routes";
import type { MobileRoute, NavigationState } from "./navigation/routes";
import { MESSAGES } from "./i18n/messages";
import { createHomeScreenModel } from "./screens/homeScreenModel";
import { createLoginScreenModel } from "./screens/loginScreenModel";

export type MobileAppOptions = {
  apiBaseUrl: string;
  sessionStore?: SessionStore;
};

export type MobileApp = {
  api: PlatformFoundationApi;
  auth: LocalAuthService;
  getNavigation: () => NavigationState;
  navigateTo: (route: MobileRoute) => NavigationState;
  loginScreen: ReturnType<typeof createLoginScreenModel>;
  homeScreen: () => ReturnType<typeof createHomeScreenModel>;
};

export function createMobileApp(options: MobileAppOptions): MobileApp {
  const sessionStore = options.sessionStore ?? createMemorySessionStore();
  const auth = createLocalAuthService(sessionStore);
  const api = createPlatformFoundationApi({
    baseUrl: options.apiBaseUrl,
    getToken: () => auth.currentSession()?.token ?? null,
  });
  let navigation = createInitialNavigationState(auth.currentSession() !== null);

  function setAuthenticatedRoute() {
    navigation = navigate(navigation, "home");
  }

  return {
    api,
    auth,
    getNavigation: () => navigation,
    navigateTo: (route) => {
      navigation = navigate(navigation, route);
      return navigation;
    },
    loginScreen: createLoginScreenModel(auth, setAuthenticatedRoute),
    homeScreen: () => {
      const session = auth.currentSession();
      if (!session) {
        throw new Error(MESSAGES.sessionRequired);
      }
      return createHomeScreenModel(session);
    },
  };
}
