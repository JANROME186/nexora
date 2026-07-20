import { createContext, useContext, useState, useEffect, type ReactNode, useCallback } from "react";

export interface SessionUser {
  userId: string;
  tenantId: string;
  roleCode: string;
  name: string;
  doctorId: string;
  token: string;
}

export const mockSessions: Omit<SessionUser, "token">[] = [
  {
    userId: "Doctor-01",
    tenantId: "tenant-local",
    roleCode: "REFERRING_DOCTOR",
    name: "Dr. Grace Hopper",
    doctorId: "Doctor-01",
  },
];

interface SessionContextType {
  session: SessionUser | null;
  isLoading: boolean;
  login: (
    tenantId: string,
    username: string,
    password: string,
  ) => Promise<{ ok: boolean; error?: string }>;
  loginMock: (userId: string) => void;
  logout: () => void;
  expireSession: () => void;
  mockSessions: Omit<SessionUser, "token">[];
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

const STORAGE_KEY = "hop.doctor.session";

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<SessionUser | null>(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        console.warn("Failed to parse session", e);
        return null;
      }
    }
    return null;
  });
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (session) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [session]);

  const login = useCallback(async (tenantId: string, username: string, password: string) => {
    setIsLoading(true);
    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        body: JSON.stringify({ tenantId, username, password }),
      });

      if (!response.ok) {
        let msg = "Invalid credentials";
        if (response.status === 403) {
          msg = "Account locked or suspended";
        }
        return { ok: false, error: msg };
      }

      const data = await response.json();
      const token = data.token || "";

      // Parse token: local-session:tenantId:userId
      const parts = token.split(":");
      const parsedTenantId = parts[1] || tenantId;
      const parsedUserId = parts[2] || username;

      // Try fetching doctor name if available, fallback to username
      let displayName = username;
      try {
        const doctorRes = await fetch(`/api/people/doctors/${parsedUserId}`, {
          headers: {
            "X-HOP-AUTH-TOKEN": token,
            "X-HOP-USER-ID": parsedUserId,
            "X-HOP-TENANT-ID": parsedTenantId,
            "X-HOP-ROLES": "REFERRING_DOCTOR",
          },
        });
        if (doctorRes.ok) {
          const doctor = await doctorRes.json();
          displayName = `${doctor.givenName} ${doctor.familyName}`;
        }
      } catch (e) {
        console.warn("Failed to fetch doctor profile for name", e);
      }

      const user: SessionUser = {
        userId: parsedUserId,
        tenantId: parsedTenantId,
        roleCode: "REFERRING_DOCTOR",
        name: displayName,
        doctorId: parsedUserId,
        token,
      };

      setSession(user);
      return { ok: true };
    } catch (e: unknown) {
      const message = e instanceof Error ? e.message : "Unexpected error occurred";
      return { ok: false, error: message };
    } finally {
      setIsLoading(false);
    }
  }, []);

  const loginMock = useCallback((userId: string) => {
    const mock = mockSessions.find((s) => s.userId === userId);
    if (mock) {
      setSession({
        ...mock,
        token: `local-session:${mock.tenantId}:${mock.userId}`,
      });
    }
  }, []);

  const logout = useCallback(() => {
    setSession(null);
  }, []);

  const expireSession = useCallback(() => {
    setSession(null);
  }, []);

  return (
    <SessionContext.Provider
      value={{ session, isLoading, login, loginMock, logout, expireSession, mockSessions }}
    >
      {children}
    </SessionContext.Provider>
  );
}

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error("useSession must be within SessionProvider");
  return ctx;
}

export function readSessionHeaders(): Record<string, string> {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const session = JSON.parse(saved) as SessionUser;
      return {
        "X-HOP-AUTH-TOKEN": session.token,
        "X-HOP-USER-ID": session.userId,
        "X-HOP-TENANT-ID": session.tenantId,
        "X-HOP-ROLES": session.roleCode,
      };
    }
  } catch (e) {
    console.warn("Failed to read session headers", e);
  }
  return {};
}
