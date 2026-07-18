import type { ResultHistoryResponse } from "../api/types";
import type { ResultsApi } from "../api/resultsApi";
import type { MobileSession } from "../auth/sessionStore";

export type ResultHistoryScreenState = {
  isLoading: boolean;
  error: string | null;
  history: ResultHistoryResponse[];
};

export type ResultHistoryScreenModel = {
  getState: () => ResultHistoryScreenState;
  loadHistory: (testId?: string) => Promise<void>;
};

export function createResultHistoryScreenModel(
  api: ResultsApi,
  session: MobileSession,
  onStateChange?: () => void,
): ResultHistoryScreenModel {
  let state: ResultHistoryScreenState = {
    isLoading: false,
    error: null,
    history: [],
  };

  function setState(newState: Partial<ResultHistoryScreenState>) {
    state = { ...state, ...newState };
    onStateChange?.();
  }

  return {
    getState: () => state,
    loadHistory: async (testId?: string) => {
      setState({ isLoading: true, error: null });
      try {
        const history = await api.getResultHistory(session.userId, testId);
        setState({ isLoading: false, history, error: null });
      } catch (err) {
        setState({
          isLoading: false,
          error: err instanceof Error ? err.message : String(err),
        });
      }
    },
  };
}
