import type { MessageCatalog } from "./es-MX";

/**
 * en-US message catalog (fallback locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `fallback_locale: en-US`).
 *
 * The flat, top-level keys keep the exact original English text that used to be the sole content
 * of `src/i18n/messages.ts`, so switching to en-US reproduces the pre-i18n-split behavior exactly.
 * Typed against `MessageCatalog` (derived from `es-MX.ts`) so TypeScript enforces key parity
 * between the two locales.
 */
export const enUS: MessageCatalog = {
  selectDoctorFirst: "Select a doctor first.",
  selectPatientFirst: "Select a patient first.",
  unexpectedError: "Unexpected error. Please try again.",
  selectReceptionVisitFirst: "Select a reception visit first.",
  selectOrderFirst: "Select a diagnostic order first.",
  selectCashSessionFirst: "Select a cash session first.",
  selectSaleFirst: "Select a sale first.",
  selectBillingRequestFirst: "Select a billing request first.",
  // -- Results and Digital Delivery (MVP-MOD-007) --
  selectResultFirst: "Select a result first.",
  selectEscalationFirst: "Select a critical escalation first.",
  noResultsPendingRelease: "No released results found for this tenant.",
  noEscalationsOpen: "No open critical open for this tenant.",
  noReportsGenerated: "No reports generated for this result.",
  noNotificationsFound: "No notification records found for this result.",
  reportRegenerated: "Report regeneration triggered.",
  escalationAcknowledged: "Escalation acknowledged.",
  escalationEscalated: "Escalation escalated to next tier.",
  escalationClosed: "Escalation closed.",
  appShell: {
    title: "HOP Patient Portal",
    subtitle:
      "Patient self-service access: consult your clinical history, appointments, orders, and authorized results.",
    navAriaLabel: "Portal screens",
    languageSwitcherLabel: "Language",
    tabs: {
      profile: "My Profile",
      results: "Medical Results",
      appointments: "My Appointments",
      orders: "My Orders",
      notifications: "Notifications",
      imaging: "Imaging",
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
    profile: {
      personalInfo: "Personal Information",
      code: "Patient Code",
      name: "Full Name",
      birthDate: "Birth Date",
      gender: "Sex at Birth",
      document: "Identity Document",
      address: "Address",
      preferredLocale: "Preferred Language",
      contacts: "Emergency Contacts",
      noContacts: "No emergency contacts registered.",
    },
    results: {
      abnormal: "Abnormal",
      normal: "Normal",
      analyte: "Analyte",
      value: "Result",
      range: "Reference Range",
      releasedAt: "Released on",
    },
    appointments: {
      date: "Date & Time",
      branch: "Branch",
      doctor: "Doctor",
      status: "Status",
    },
    orders: {
      orderId: "Order ID",
      date: "Date",
      status: "Order Status",
      tests: "Tests",
    },
    imaging: {
      studyId: "Study",
      format: "Format",
      status: "Status",
      findings: "Findings",
      impression: "Impression",
    },
  },
};
