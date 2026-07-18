import { createContext, useContext, useState, useEffect, type ReactNode } from "react";

export interface SessionUser {
  userId: string;
  roleCode: string;
  name: string;
  patientId?: string;
  doctorId?: string;
}

const mockSessions: SessionUser[] = [
  {
    userId: "p-001",
    roleCode: "PATIENT",
    name: "John Doe (Patient)",
    patientId: "Patient-01",
  },
  {
    userId: "d-001",
    roleCode: "DOCTOR",
    name: "Dr. Smith (Doctor)",
    doctorId: "Doctor-01",
  },
];

interface SessionContextType {
  session: SessionUser | null;
  setSession: (session: SessionUser | null) => void;
  mockSessions: SessionUser[];
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<SessionUser | null>(() => {
    const saved = localStorage.getItem("dev_session");
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        console.warn("Failed to parse session", e);
        return null;
      }
    }
    return mockSessions[0];
  });

  useEffect(() => {
    if (session) {
      localStorage.setItem("dev_session", JSON.stringify(session));
    } else {
      localStorage.removeItem("dev_session");
    }
  }, [session]);

  return (
    <SessionContext.Provider value={{ session, setSession, mockSessions }}>
      <div style={{ background: "#eee", padding: "0.5rem", marginBottom: "1rem" }}>
        <strong>Dev Session: </strong>
        <select
          value={session?.userId || ""}
          onChange={(e) => {
            const user = mockSessions.find((s) => s.userId === e.target.value);
            setSession(user || null);
          }}
        >
          {mockSessions.map((s) => (
            <option key={s.userId} value={s.userId}>
              {s.name}
            </option>
          ))}
        </select>
      </div>
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
  let session = mockSessions[0];
  try {
    const saved = localStorage.getItem("dev_session");
    if (saved) {
      session = JSON.parse(saved);
    }
  } catch (e) {
    console.warn("Failed to read dev session", e);
  }
  return {
    "X-HOP-USER-ID": session.userId,
    "X-HOP-ROLES": session.roleCode,
  };
}
