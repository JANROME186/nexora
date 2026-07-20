import type { MobileSession } from "../auth/sessionStore";
import { permissionsForRoles } from "../auth/permissions";
import { getMessages, type Locale, DEFAULT_LOCALE } from "../i18n/locale";
import type { MessageCatalog } from "../i18n/locales/es-MX";
import type { MobileRoute } from "../navigation/routes";
import { visibleRoutesForPermissions } from "../navigation/routes";

export type HomeAction = {
  label: string;
  route: MobileRoute;
};

export type HomeScreenModel = {
  greeting: string;
  scopeLabel: string;
  actions: HomeAction[];
};

type LabeledActionRoute = keyof MessageCatalog["mobileRouteLabels"] & MobileRoute;

const ACTION_ROUTES: readonly LabeledActionRoute[] = [
  "tenant-summary",
  "laboratory-summary",
  "branch-summary",
  "user-summary",
  "audit-summary",
  "patient-profile",
  "patient-appointments",
  "patient-orders",
  "patient-results",
  "patient-notifications",
];

export function createHomeScreenModel(
  session: MobileSession,
  locale: Locale = DEFAULT_LOCALE,
): HomeScreenModel {
  const messages = getMessages(locale);
  const permissions = permissionsForRoles(session.roleCodes);
  const visibleRoutes = new Set(visibleRoutesForPermissions(permissions));
  return {
    greeting: messages.mobileGreeting.replace("{displayName}", session.displayName),
    scopeLabel: messages.mobileTenantScope.replace("{tenantId}", session.tenantId),
    actions: ACTION_ROUTES.filter((route) => visibleRoutes.has(route)).map((route) => ({
      label: messages.mobileRouteLabels[route],
      route,
    })),
  };
}
