import { createContext, useContext, useState, useEffect, type ReactNode, useCallback } from "react";

export interface SessionUser {
  userId: string;
  tenantId: string;
  roleCode: string;
  name: string;
  patientId: string;
  token: string;
}

export const mockSessions: Omit<SessionUser, "token">[] = [
  {
    userId: "Patient-01",
    tenantId: "tenant-local",
    roleCode: "PATIENT",
    name: "John Doe (Patient)",
    patientId: "Patient-01",
  },
  {
    userId: "Doctor-01",
    tenantId: "tenant-local",
    roleCode: "REFERRING_DOCTOR",
    name: "Dr. Smith (Doctor)",
    patientId: "Doctor-01",
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
  mockSessions: Omit<SessionUser, "token">[];
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

const STORAGE_KEY = "hop.patient.session";

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

      // Try fetching patient name if available, fallback to username
      let displayName = username;
      try {
        const patientRes = await fetch(`/api/people/patients/${parsedUserId}`, {
          headers: {
            "X-HOP-AUTH-TOKEN": token,
            "X-HOP-USER-ID": parsedUserId,
            "X-HOP-TENANT-ID": parsedTenantId,
            "X-HOP-ROLES": "PATIENT",
          },
        });
        if (patientRes.ok) {
          const patient = await patientRes.json();
          displayName = `${patient.givenName} ${patient.familyName}`;
        }
      } catch (e) {
        console.warn("Failed to fetch patient profile for name", e);
      }

      const user: SessionUser = {
        userId: parsedUserId,
        tenantId: parsedTenantId,
        roleCode: "PATIENT", // Default role code for patient portal
        name: displayName,
        patientId: parsedUserId,
        token,
      };

      setSession(user);
      return { ok: true };
    } catch (e: any) {
      return { ok: false, error: e?.message || "Unexpected error occurred" };
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

  return (
    <SessionContext.Provider value={{ session, isLoading, login, loginMock, logout, mockSessions }}>
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
