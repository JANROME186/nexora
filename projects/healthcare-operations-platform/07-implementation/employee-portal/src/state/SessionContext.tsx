import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import { permissionsForRoles, type PermissionCode } from "./permissions";

export interface Session {
  userId: string;
  displayName: string;
  roleCodes: string[];
  permissions: Set<PermissionCode>;
}

interface SessionContextValue extends Session {
  setRoleCodes: (roleCodes: string[]) => void;
}

/**
 * Local development fixture only. Employee-portal has no production login/session mechanism
 * yet; this stands in until real authentication is implemented (tracked as follow-up technical
 * debt) and must never be treated as a production default identity.
 *
 * `roleCodes: ["ADMIN"]` keeps every screen visible by default in local dev, preserving current
 * (pre-permission-filtering) behavior for anyone running the portal without a real session.
 */
const LOCAL_DEV_FIXTURE_SESSION: Omit<Session, "permissions"> = {
  userId: "local-dev-fixture-user",
  displayName: "Local Dev Fixture",
  roleCodes: ["ADMIN"],
};

const SessionContext = createContext<SessionContextValue | undefined>(undefined);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [roleCodes, setRoleCodes] = useState<string[]>(LOCAL_DEV_FIXTURE_SESSION.roleCodes);

  const value = useMemo<SessionContextValue>(
    () => ({
      userId: LOCAL_DEV_FIXTURE_SESSION.userId,
      displayName: LOCAL_DEV_FIXTURE_SESSION.displayName,
      roleCodes,
      permissions: permissionsForRoles(roleCodes),
      setRoleCodes,
    }),
    [roleCodes],
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession(): SessionContextValue {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error("useSession must be used within a SessionProvider.");
  }
  return context;
}
