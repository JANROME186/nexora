import type { DeliveryTicketResponse } from "../api/types";
import type { ResultsApi } from "../api/resultsApi";
import type { MobileSession } from "../auth/sessionStore";

export type ResultsScreenState = {
  isLoading: boolean;
  error: string | null;
  tickets: DeliveryTicketResponse[];
};

export type ResultsScreenModel = {
  getState: () => ResultsScreenState;
  loadTickets: () => Promise<void>;
};

export function createResultsScreenModel(
  api: ResultsApi,
  session: MobileSession,
  onStateChange?: () => void,
): ResultsScreenModel {
  let state: ResultsScreenState = {
    isLoading: false,
    error: null,
    tickets: [],
  };

  function setState(newState: Partial<ResultsScreenState>) {
    state = { ...state, ...newState };
    onStateChange?.();
  }

  return {
    getState: () => state,
    loadTickets: async () => {
      setState({ isLoading: true, error: null });
      try {
        // Assume patientId is mapped from session (in reality, via identity claim)
        // We'll use session.userId as the patientId for the mobile baseline.
        const tickets = await api.listResults(session.userId);
        setState({ isLoading: false, tickets, error: null });
      } catch (err) {
        setState({
          isLoading: false,
          error: err instanceof Error ? err.message : String(err),
        });
      }
    },
  };
}
