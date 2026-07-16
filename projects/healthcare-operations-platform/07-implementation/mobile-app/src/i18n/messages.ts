/**
 * Baseline message catalog (HOP-QA-ALIGN-005). Centralizes the mobile-foundation validation
 * strings so a future locale switch has a single place to start. Full localization-resource
 * adoption remains tracked by TD-I18N-001 pending a renderable mobile UI layer.
 */
export const MESSAGES = {
  tenantIdRequired: "Tenant id is required.",
  userIdRequired: "User id is required.",
  displayNameRequired: "Display name is required.",
  emailRequired: "Email is required.",
  emailInvalid: "Email must be valid.",
  sessionRequired: "Authenticated session is required.",
} as const;
