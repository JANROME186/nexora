import type { AsyncStatus } from "../../state/useAsyncAction";

interface StatusBannerProps {
  status: AsyncStatus;
  errorMessage?: string;
  successMessage?: string;
}

/**
 * Shows a plain, non-technical message for loading, success and error states so
 * permission or validation failures stay clear to administration users.
 */
export function StatusBanner({ status, errorMessage, successMessage }: StatusBannerProps) {
  if (status === "loading") {
    return (
      <p role="status" className="status-banner status-banner--loading">
        Working on it...
      </p>
    );
  }

  if (status === "error") {
    return (
      <p role="alert" className="status-banner status-banner--error">
        {errorMessage ?? "The request could not be completed."}
      </p>
    );
  }

  if (status === "success" && successMessage) {
    return (
      <p role="status" className="status-banner status-banner--success">
        {successMessage}
      </p>
    );
  }

  return null;
}
