/**
 * Baseline message catalog (HOP-QA-ALIGN-005). Centralizes the employee-portal validation and
 * fallback-error strings that were duplicated verbatim across screens before this baseline, so a
 * future locale switch has a single place to start. Single-occurrence screen copy (headings,
 * button labels, field hints) remains inline pending the broader i18n library adoption tracked by
 * TD-I18N-001 — see 08-qa/qa/quality-alignment/HOP-QA-ALIGN-005-message-externalization-inventory.md.
 */
export const MESSAGES = {
  selectDoctorFirst: "Select a doctor first.",
  selectPatientFirst: "Select a patient first.",
  unexpectedError: "Unexpected error. Please try again.",
} as const;
