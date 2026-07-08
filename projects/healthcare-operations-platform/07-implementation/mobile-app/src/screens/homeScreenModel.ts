import type { MobileSession } from "../auth/sessionStore";
import type { MobileRoute } from "../navigation/routes";

export type HomeAction = {
  label: string;
  route: MobileRoute;
};

export type HomeScreenModel = {
  greeting: string;
  scopeLabel: string;
  actions: HomeAction[];
};

export function createHomeScreenModel(session: MobileSession): HomeScreenModel {
  return {
    greeting: `Welcome, ${session.displayName}`,
    scopeLabel: `Tenant scope: ${session.tenantId}`,
    actions: [
      { label: "Tenant", route: "tenant-summary" },
      { label: "Laboratory", route: "laboratory-summary" },
      { label: "Branch", route: "branch-summary" },
      { label: "Users", route: "user-summary" },
      { label: "Audit", route: "audit-summary" }
    ]
  };
}
