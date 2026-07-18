import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { permissionsForRoles, type PermissionCode } from "./permissions";

export interface Session {
  userId: string;
  tenantId: string;
  branchId: string;
  token: string;
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
  tenantId: "tenant-local",
  branchId: "branch-local",
  token: "local-dev-token",
  displayName: "Local Dev Fixture",
  roleCodes: ["ADMIN"],
};

const STORAGE_KEY = "hop.session";

export function readSessionHeaders(): Record<string, string> {
  const session = readStoredSession();
  return {
    "X-HOP-AUTH-TOKEN": session.token,
    "X-HOP-USER-ID": session.userId,
    "X-HOP-TENANT-ID": session.tenantId,
    "X-HOP-BRANCH-ID": session.branchId,
    "X-HOP-ROLES": session.roleCodes.join(","),
  };
}

const SessionContext = createContext<SessionContextValue | undefined>(undefined);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [roleCodes, setRoleCodes] = useState<string[]>(() => readStoredSession().roleCodes);

  useEffect(() => {
    writeStoredSession({ ...LOCAL_DEV_FIXTURE_SESSION, roleCodes });
  }, [roleCodes]);

  const value = useMemo<SessionContextValue>(
    () => ({
      userId: LOCAL_DEV_FIXTURE_SESSION.userId,
      tenantId: LOCAL_DEV_FIXTURE_SESSION.tenantId,
      branchId: LOCAL_DEV_FIXTURE_SESSION.branchId,
      token: LOCAL_DEV_FIXTURE_SESSION.token,
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

function readStoredSession(): Omit<Session, "permissions"> {
  if (typeof window === "undefined") {
    return LOCAL_DEV_FIXTURE_SESSION;
  }
  try {
    const stored = window.localStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return LOCAL_DEV_FIXTURE_SESSION;
    }
    const parsed = JSON.parse(stored) as Partial<Omit<Session, "permissions">>;
    if (!parsed.userId || !parsed.tenantId || !parsed.token || !Array.isArray(parsed.roleCodes)) {
      return LOCAL_DEV_FIXTURE_SESSION;
    }
    return {
      userId: parsed.userId,
      tenantId: parsed.tenantId,
      branchId: parsed.branchId || LOCAL_DEV_FIXTURE_SESSION.branchId,
      token: parsed.token,
      displayName: parsed.displayName || LOCAL_DEV_FIXTURE_SESSION.displayName,
      roleCodes: parsed.roleCodes,
    };
  } catch {
    return LOCAL_DEV_FIXTURE_SESSION;
  }
}

function writeStoredSession(session: Omit<Session, "permissions">) {
  if (typeof window !== "undefined") {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  }
}
