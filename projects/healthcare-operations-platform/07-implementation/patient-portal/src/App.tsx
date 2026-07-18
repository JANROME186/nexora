import { useState } from "react";
import { SessionProvider, useSession } from "./state/SessionContext";
import { LocaleProvider } from "./i18n/LocaleContext";
import { getPatientHistory, type PatientResultHistoryView } from "./api/patientResultHistoryApi";

function PatientHistoryScreen() {
  const { session } = useSession();
  const [history, setHistory] = useState<PatientResultHistoryView | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function loadHistory() {
    if (!session || !session.patientId) return;
    setLoading(true);
    setError("");
    try {
      const data = await getPatientHistory(session.patientId);
      setHistory(data);
    } catch (e: unknown) {
      if (e instanceof Error) {
        setError(e.message || "Failed to load history");
      } else {
        setError("Failed to load history");
      }
    } finally {
      setLoading(false);
    }
  }

  if (!session || session.roleCode !== "PATIENT") {
    return <p>Access denied. Please log in as a patient.</p>;
  }

  return (
    <div className="container">
      <h2>Patient Portal - My Results</h2>
      <button onClick={loadHistory} disabled={loading}>
        {loading ? "Loading..." : "Load My Results"}
      </button>
      {error && <p style={{ color: "red" }}>{error}</p>}

      {history && (
        <table style={{ marginTop: "1rem", width: "100%", borderCollapse: "collapse" }} border={1}>
          <thead>
            <tr>
              <th>Result ID</th>
              <th>Test</th>
              <th>Value</th>
              <th>Reference Range</th>
              <th>Status</th>
              <th>Released At</th>
            </tr>
          </thead>
          <tbody>
            {history.entries.map((entry) => (
              <tr key={entry.resultId}>
                <td>{entry.resultId}</td>
                <td>{entry.analyteName}</td>
                <td>{entry.stringValue}</td>
                <td>{entry.referenceRange}</td>
                <td style={{ color: entry.isAbnormal ? "red" : "green" }}>
                  {entry.isAbnormal ? "Abnormal" : "Normal"}
                </td>
                <td>{new Date(entry.releasedAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function AppContent() {
  return (
    <div>
      <header>
        <h1>HOP Patient Portal</h1>
      </header>
      <main>
        <PatientHistoryScreen />
      </main>
    </div>
  );
}

export default function App() {
  return (
    <LocaleProvider>
      <SessionProvider>
        <AppContent />
      </SessionProvider>
    </LocaleProvider>
  );
}
