import type { MessageCatalog } from "./es-MX";

/**
 * en-US message catalog (fallback locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `fallback_locale: en-US`).
 *
 * Doctor-portal domain catalog (COM-MOD-009-PORTAL-002). Typed against `MessageCatalog` (derived
 * from `es-MX.ts`) so TypeScript enforces key parity between the two locales.
 */
export const enUS: MessageCatalog = {
  unexpectedError: "Unexpected error. Please try again.",
  selectPatientFirst: "Select a patient first.",
  sessionExpiredRetry: "Your session has expired. Please log in again to continue.",
  appShell: {
    title: "HOP Doctor Portal",
    subtitle:
      "Referring doctor access: consult your referred patients, authorized released results, diagnostic orders, and notifications.",
    navAriaLabel: "Doctor portal screens",
    languageSwitcherLabel: "Language",
    tabs: {
      patients: "My Patients",
      results: "Results",
      orders: "My Orders",
      notifications: "Notifications",
    },
    login: {
      title: "Sign In",
      tenantId: "Organization ID",
      username: "Username",
      // eslint-disable-next-line sonarjs/no-hardcoded-passwords -- UI label text, not a credential
      passwordLabel: "Password",
      submit: "Sign In",
      loggingIn: "Signing In...",
      errorInvalid: "Invalid credentials.",
      errorLocked: "Account temporarily locked due to failed attempts.",
      errorSuspended: "Account suspended. Please contact support.",
    },
    states: {
      loading: "Loading information...",
      empty: "No records found.",
      error: "An error occurred while loading the information.",
      noPermission: "You do not have permission to access this section.",
      sessionExpired: "Your session has expired. Please log in again.",
      logout: "Log Out",
      welcome: "Welcome",
    },
    patients: {
      name: "Name",
      document: "Document",
      birthDate: "Birth Date",
      referredOrders: "Referred Orders",
      viewResults: "View Results",
      emptyHint: "You have no referred patients in this organization yet.",
    },
    results: {
      selectPatient: "Select a patient",
      selectPatientPlaceholder: "-- Select a referred patient --",
      abnormal: "Abnormal",
      normal: "Normal",
      analyte: "Analyte",
      value: "Result",
      range: "Reference Range",
      releasedAt: "Released on",
    },
    orders: {
      orderId: "Order ID",
      patient: "Patient",
      branch: "Branch",
      status: "Status",
      createdAt: "Created At",
    },
    notifications: {
      resultId: "Result ID",
      channel: "Channel",
      recipient: "Recipient",
      status: "Status",
      dispatchedAt: "Dispatched At",
      failureReason: "Failure Reason",
    },
  },
};
