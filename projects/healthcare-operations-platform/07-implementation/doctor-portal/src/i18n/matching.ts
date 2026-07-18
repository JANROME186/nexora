/**
 * Duplicate-match confidence thresholds and badge styling (HOP-QA-ALIGN-005). Previously
 * declared identically in both PersonSearchScreen and PatientRegistrationsScreen; centralized
 * here so the tenant-facing confidence bands stay consistent and are defined once.
 */
export const DUPLICATE_MATCH_CONFIDENCE_HIGH = 0.85;
export const DUPLICATE_MATCH_CONFIDENCE_MEDIUM = 0.5;

export function confidenceClass(confidence: number): string {
  if (confidence >= DUPLICATE_MATCH_CONFIDENCE_HIGH)
    return "confidence-badge confidence-badge--high";
  if (confidence >= DUPLICATE_MATCH_CONFIDENCE_MEDIUM)
    return "confidence-badge confidence-badge--medium";
  return "confidence-badge confidence-badge--low";
}
