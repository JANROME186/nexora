import { MESSAGES } from "../i18n/messages";
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
      const tenantId = requiredText(request.tenantId, MESSAGES.tenantIdRequired);
      const userId = requiredText(request.userId, MESSAGES.userIdRequired);
      const displayName = requiredText(request.displayName, MESSAGES.displayNameRequired);
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
  const email = requiredText(value, MESSAGES.emailRequired).toLowerCase();
  if (!email.includes("@")) {
    throw new Error(MESSAGES.emailInvalid);
  }
  return email;
}
