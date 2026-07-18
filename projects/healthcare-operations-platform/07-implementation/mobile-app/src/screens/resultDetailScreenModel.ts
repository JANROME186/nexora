import type { DeliveryTicketResponse } from "../api/types";
import type { ResultsApi } from "../api/resultsApi";

export type ResultDetailScreenState = {
  isLoading: boolean;
  error: string | null;
  ticket: DeliveryTicketResponse | null;
};

export type ResultDetailScreenModel = {
  getState: () => ResultDetailScreenState;
  loadDetail: () => Promise<void>;
  downloadReport: () => Promise<string | null>;
};

export function createResultDetailScreenModel(
  api: ResultsApi,
  deliveryTicketId: string,
  onStateChange?: () => void,
): ResultDetailScreenModel {
  let state: ResultDetailScreenState = {
    isLoading: false,
    error: null,
    ticket: null,
  };

  function setState(newState: Partial<ResultDetailScreenState>) {
    state = { ...state, ...newState };
    onStateChange?.();
  }

  return {
    getState: () => state,
    loadDetail: async () => {
      setState({ isLoading: true, error: null });
      try {
        const ticket = await api.getResultDetail(deliveryTicketId);
        setState({ isLoading: false, ticket, error: null });

        // As per BCM-RES-004: Triggers RecordResultViewed when viewed
        if (ticket.status !== "viewed") {
          await api.recordResultViewed(deliveryTicketId).catch(() => {
            // Failing to record view shouldn't break the UI, but it's audited on the backend
          });
        }
      } catch (err) {
        setState({
          isLoading: false,
          error: err instanceof Error ? err.message : String(err),
        });
      }
    },
    downloadReport: async () => {
      const ticket = state.ticket;
      if (!ticket?.reportUrl) {
        return null;
      }
      // Baseline returning the report URL. A native renderer would trigger a file download.
      return ticket.reportUrl;
    },
  };
}
