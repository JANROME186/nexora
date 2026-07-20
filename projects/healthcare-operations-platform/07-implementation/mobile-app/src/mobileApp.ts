import { createPlatformFoundationApi } from "./api/platformFoundationApi";
import type { FetchLike, PlatformFoundationApi } from "./api/platformFoundationApi";
import { createLocalAuthService } from "./auth/localAuth";
import type { LocalAuthService } from "./auth/localAuth";
import { createMemorySessionStore } from "./auth/sessionStore";
import type { SessionStore } from "./auth/sessionStore";
import { createInitialNavigationState, navigate } from "./navigation/routes";
import type { MobileRoute, NavigationState } from "./navigation/routes";
import { MESSAGES } from "./i18n/messages";
import { createHomeScreenModel } from "./screens/homeScreenModel";
import { createLoginScreenModel } from "./screens/loginScreenModel";
import { createResultsApi } from "./api/resultsApi";
import type { ResultsApi } from "./api/resultsApi";
import { createPatientMobileApi } from "./api/patientMobileApi";
import type { PatientMobileApi } from "./api/patientMobileApi";
import { createResultsScreenModel } from "./screens/resultsScreenModel";
import { createResultDetailScreenModel } from "./screens/resultDetailScreenModel";
import { createResultHistoryScreenModel } from "./screens/resultHistoryScreenModel";
import { createPatientMobileWorkflowModel } from "./screens/patientMobileWorkflowModel";

export type MobileAppOptions = {
  apiBaseUrl: string;
  fetcher?: FetchLike;
  sessionStore?: SessionStore;
};

export type MobileApp = {
  api: PlatformFoundationApi;
  resultsApi: ResultsApi;
  patientMobileApi: PatientMobileApi;
  auth: LocalAuthService;
  getNavigation: () => NavigationState;
  navigateTo: (route: MobileRoute) => NavigationState;
  loginScreen: ReturnType<typeof createLoginScreenModel>;
  homeScreen: () => ReturnType<typeof createHomeScreenModel>;
  resultsScreen: () => ReturnType<typeof createResultsScreenModel>;
  resultDetailScreen: (ticketId: string) => ReturnType<typeof createResultDetailScreenModel>;
  resultHistoryScreen: () => ReturnType<typeof createResultHistoryScreenModel>;
  patientMobileWorkflow: () => ReturnType<typeof createPatientMobileWorkflowModel>;
};

export function createMobileApp(options: MobileAppOptions): MobileApp {
  const sessionStore = options.sessionStore ?? createMemorySessionStore();
  const auth = createLocalAuthService(sessionStore);
  const getSessionHeaders = (): Record<string, string> => {
    const session = auth.currentSession();
    return session
      ? {
          "X-Tenant-Id": session.tenantId,
          "X-User-Id": session.userId,
        }
      : {};
  };
  const api = createPlatformFoundationApi({
    baseUrl: options.apiBaseUrl,
    fetcher: options.fetcher,
    getToken: () => auth.currentSession()?.token ?? null,
  });
  const resultsApi = createResultsApi({
    baseUrl: options.apiBaseUrl,
    fetcher: options.fetcher,
    getToken: () => auth.currentSession()?.token ?? null,
  });
  const patientMobileApi = createPatientMobileApi({
    baseUrl: options.apiBaseUrl,
    fetcher: options.fetcher,
    getToken: () => auth.currentSession()?.token ?? null,
    getSessionHeaders,
  });
  let navigation = createInitialNavigationState(auth.currentSession() !== null);

  function setAuthenticatedRoute() {
    navigation = navigate(navigation, "home");
  }

  return {
    api,
    resultsApi,
    patientMobileApi,
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
    resultsScreen: () => {
      const session = auth.currentSession();
      if (!session) {
        throw new Error(MESSAGES.sessionRequired);
      }
      return createResultsScreenModel(resultsApi, session);
    },
    resultDetailScreen: (ticketId: string) => {
      return createResultDetailScreenModel(resultsApi, ticketId);
    },
    resultHistoryScreen: () => {
      const session = auth.currentSession();
      if (!session) {
        throw new Error(MESSAGES.sessionRequired);
      }
      return createResultHistoryScreenModel(resultsApi, session);
    },
    patientMobileWorkflow: () => {
      const session = auth.currentSession();
      if (!session) {
        throw new Error(MESSAGES.sessionRequired);
      }
      return createPatientMobileWorkflowModel(patientMobileApi, session);
    },
  };
}
