/**
 * Local/dev authentication stand-in.
 *
 * This module (together with `./sessionStore.ts`) implements a `local-session:<tenant>:<user>`
 * token scheme that is a placeholder, not a production authentication mechanism. There is no
 * backend authentication endpoint for the mobile app yet either — that is a known, tracked gap
 * (see the enterprise-product-foundation-standard's `login_and_session_management` foundation),
 * not a bug to fix here. Do not treat this as a production login flow; it exists so mobile-app
 * screens/models have something to build against until real backend-backed authentication lands.
 */
import { DEFAULT_LOCALE, getMessages, type Locale } from "../i18n/locale";
import type { MobileSession, SessionStore } from "./sessionStore";

export type LoginRequest = {
  tenantId: string;
  userId: string;
  displayName: string;
  email: string;
  roleCodes?: readonly string[];
};

export type LocalAuthService = {
  login: (request: LoginRequest) => MobileSession;
  logout: () => void;
  currentSession: () => MobileSession | null;
};

export function createLocalAuthService(
  sessionStore: SessionStore,
  now: () => Date = () => new Date(),
  locale: Locale = DEFAULT_LOCALE,
): LocalAuthService {
  const messages = getMessages(locale);
  return {
    login: (request) => {
      const tenantId = requiredText(request.tenantId, messages.tenantIdRequired);
      const userId = requiredText(request.userId, messages.userIdRequired);
      const displayName = requiredText(request.displayName, messages.displayNameRequired);
      const email = requiredEmail(request.email, messages);
      const session: MobileSession = {
        token: `local-session:${tenantId}:${userId}`,
        tenantId,
        userId,
        displayName,
        email,
        roleCodes: request.roleCodes ?? ["ADMIN"],
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

function requiredEmail(value: string, messages: ReturnType<typeof getMessages>) {
  const email = requiredText(value, messages.emailRequired).toLowerCase();
  if (!email.includes("@")) {
    throw new Error(messages.emailInvalid);
  }
  return email;
}
