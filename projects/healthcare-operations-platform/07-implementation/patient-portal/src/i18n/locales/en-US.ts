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
  noEscalationsOpen: "No open critical escalations for this tenant.",
  noReportsGenerated: "No reports generated for this result.",
  noNotificationsFound: "No notification records found for this result.",
  reportRegenerated: "Report regeneration triggered.",
  escalationAcknowledged: "Escalation acknowledged.",
  escalationEscalated: "Escalation escalated to next tier.",
  escalationClosed: "Escalation closed.",
  appShell: {
    title: "Healthcare Operations Platform - Employee Portal Administration",
    subtitle:
      "Platform Foundation, Diagnostic Catalog, People and Clinical Master Data, Front Desk and " +
      "Care Delivery, Cashier and Billing, Laboratory Workflow, and Results and Digital Delivery: " +
      "administration, audit, catalog, patient/doctor records, diagnostic orders, cash sessions, " +
      "laboratory workflow, and result delivery management.",
    navAriaLabel: "Administration screens",
    languageSwitcherLabel: "Language",
    tabs: {
      tenants: "Tenants",
      laboratories: "Laboratories",
      branches: "Branches",
      users: "Users",
      roleAssignments: "Role Assignments",
      auditEvents: "Audit Events",
      diagnosticCatalog: "Diagnostic Catalog",
      personSearch: "People Search",
      patients: "Patients",
      doctors: "Doctors",
      patientRegistrations: "Patient Registrations",
      reception: "Front Desk",
      diagnosticOrders: "Diagnostic Orders",
      cashSessions: "Cash Sessions",
      sales: "Sales",
      billingRequests: "Billing Requests",
      sampleCollection: "Sample Collection",
      sampleLabeling: "Sample Labeling",
      sampleReception: "Sample Reception",
      laboratoryProcessing: "Lab Processing",
      technicalValidation: "Tech Validation",
      medicalValidation: "Med Validation",
      resultRelease: "Result Release",
      resultSearch: "Result Search",
      resultReports: "Result Reports",
      criticalEscalations: "Critical Escalations",
      resultNotifications: "Result Notifications",
    },
  },
};
