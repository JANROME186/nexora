export type MobileSession = {
  token: string;
  tenantId: string;
  userId: string;
  displayName: string;
  email: string;
  createdAt: string;
};

export type SessionStore = {
  getSession: () => MobileSession | null;
  saveSession: (session: MobileSession) => void;
  clearSession: () => void;
};

export function createMemorySessionStore(
  initialSession: MobileSession | null = null,
): SessionStore {
  let currentSession = initialSession;

  return {
    getSession: () => currentSession,
    saveSession: (session) => {
      currentSession = session;
    },
    clearSession: () => {
      currentSession = null;
    },
  };
}
