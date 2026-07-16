import type { MobileSession, SessionStore } from "./sessionStore";

export type LoginRequest = {
  tenantId: string;
  userId: string;
  displayName: string;
  email: string;
};

export type LocalAuthService = {
  login: (request: LoginRequest) => MobileSession;
  logout: () => void;
  currentSession: () => MobileSession | null;
};

export function createLocalAuthService(
  sessionStore: SessionStore,
  now: () => Date = () => new Date(),
): LocalAuthService {
  return {
    login: (request) => {
      const tenantId = requiredText(request.tenantId, "Tenant id is required.");
      const userId = requiredText(request.userId, "User id is required.");
      const displayName = requiredText(request.displayName, "Display name is required.");
      const email = requiredEmail(request.email);
      const session: MobileSession = {
        token: `local-session:${tenantId}:${userId}`,
        tenantId,
        userId,
        displayName,
        email,
        createdAt: now().toISOString(),
      };
      sessionStore.saveSession(session);
      return session;
    },
    logout: () => {
      sessionStore.clearSession();
    },
    currentSession: () => sessionStore.getSession(),
  };
}

function requiredText(value: string, message: string) {
  if (!value || !value.trim()) {
    throw new Error(message);
  }
  return value.trim();
}

function requiredEmail(value: string) {
  const email = requiredText(value, "Email is required.").toLowerCase();
  if (!email.includes("@")) {
    throw new Error("Email must be valid.");
  }
  return email;
}
