const POSITIVE_STATUS_KEYWORDS = [
  "active",
  "acknowledged",
  "approved",
  "available",
  "in_control",
  "pass",
  "completed",
  "received",
];

const NEGATIVE_STATUS_KEYWORDS = [
  "dead",
  "failed",
  "retired",
  "out_of_control",
  "disposed",
  "cancelled",
  "expired",
];

/**
 * Maps a backend status string to one of the three existing `catalog-status--*` CSS modifiers
 * (published/retired/draft) shared across every administration screen's status badge.
 */
export function statusClass(status: string): string {
  const normalized = status.toLowerCase();
  if (POSITIVE_STATUS_KEYWORDS.some((keyword) => normalized.includes(keyword))) {
    return "catalog-status catalog-status--published";
  }
  if (NEGATIVE_STATUS_KEYWORDS.some((keyword) => normalized.includes(keyword))) {
    return "catalog-status catalog-status--retired";
  }
  return "catalog-status catalog-status--draft";
}
