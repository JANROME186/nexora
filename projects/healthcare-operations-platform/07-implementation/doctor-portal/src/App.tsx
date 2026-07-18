import { useState } from "react";
import { SessionProvider, useSession } from "./state/SessionContext";
import { LocaleProvider } from "./i18n/LocaleContext";

// Reuse patientResultHistoryApi but from the doctor's perspective (it accepts a patientId)
import { getPatientHistory, type PatientResultHistoryView } from "./api/patientResultHistoryApi";

function DoctorHistoryScreen() {
  const { session } = useSession();
  const [patientIdInput, setPatientIdInput] = useState("");
  const [history, setHistory] = useState<PatientResultHistoryView | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function loadHistory(e: React.FormEvent) {
    e.preventDefault();
    if (!session || session.roleCode !== "DOCTOR") return;
    if (!patientIdInput) return;
    setLoading(true);
    setError("");
    try {
      const data = await getPatientHistory(patientIdInput);
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

  if (!session || session.roleCode !== "DOCTOR") {
    return <p>Access denied. Please log in as a doctor.</p>;
  }

  return (
    <div className="container">
      <h2>Doctor Portal - Patient Results</h2>

      <form onSubmit={loadHistory} style={{ marginBottom: "1rem" }}>
        <label>
          Patient ID:{" "}
          <input
            type="text"
            value={patientIdInput}
            onChange={(e) => setPatientIdInput(e.target.value)}
            placeholder="e.g. Patient-01"
          />
        </label>
        <button type="submit" disabled={loading || !patientIdInput}>
          {loading ? "Loading..." : "Search"}
        </button>
      </form>

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
            {history.entries.length === 0 ? (
              <tr>
                <td colSpan={6}>No history found for this patient.</td>
              </tr>
            ) : (
              history.entries.map((entry) => (
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
              ))
            )}
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
        <h1>HOP Doctor Portal</h1>
      </header>
      <main>
        <DoctorHistoryScreen />
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
