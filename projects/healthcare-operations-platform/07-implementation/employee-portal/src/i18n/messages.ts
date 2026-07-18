/**
 * Baseline message catalog (HOP-QA-ALIGN-005). Centralizes the employee-portal validation and
 * fallback-error strings that were duplicated verbatim across screens before this baseline, so a
 * future locale switch has a single place to start. Single-occurrence screen copy (headings,
 * button labels, field hints) remains inline pending the broader i18n library adoption tracked by
 * TD-I18N-002.
 *
 * MVP-MOD-007-FE-001 (TD-FE-003 material reduction): added results-module selection guards and
 * status labels so they are not scattered as magic strings across the four new screens.
 */
export const MESSAGES = {
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
} as const;
