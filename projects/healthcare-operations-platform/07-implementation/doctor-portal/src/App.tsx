import { useState, useEffect, useCallback, type FormEvent } from "react";
import {
  SessionProvider,
  useSession,
  mockSessions,
  type SessionUser,
} from "./state/SessionContext";
import { LocaleProvider, useLocale } from "./i18n/LocaleContext";
import { permissionsForRoles, SCREEN_TO_PERMISSION, type ScreenKey } from "./state/permissions";
import { resolveApiErrorMessage } from "./state/errorMessages";
import { listReferredOrders } from "./api/diagnosticOrdersApi";
import { getPatientHistoryAsDoctor } from "./api/patientResultHistoryApi";
import { getResultNotifications } from "./api/resultNotificationsApi";
import { getPatientImagingDeliveryPackagesAsDoctor } from "./api/imagingDeliveryApi";
import type { DiagnosticOrder, ResultNotificationRequest } from "./api/types";
import type { ResultHistoryEntry } from "./api/patientResultHistoryApi";
import type { ImagingDeliveryPackage } from "./api/imagingDeliveryApi";
import "./App.css";

const ORDERED_SCREENS: ScreenKey[] = ["patients", "results", "orders", "notifications", "imaging"];

interface ReferredPatient {
  patientId: string;
  fullName: string;
  documentType: string;
  documentNumberMasked: string;
  birthDate?: string;
  orderCount: number;
}

function deriveReferredPatients(orders: DiagnosticOrder[]): ReferredPatient[] {
  const byPatient = new Map<string, ReferredPatient>();
  for (const order of orders) {
    const snapshot = order.patientSnapshot;
    if (!snapshot) continue;
    const existing = byPatient.get(snapshot.patientId);
    if (existing) {
      existing.orderCount += 1;
    } else {
      byPatient.set(snapshot.patientId, {
        patientId: snapshot.patientId,
        fullName: snapshot.fullName,
        documentType: snapshot.documentType,
        documentNumberMasked: snapshot.documentNumberMasked,
        birthDate: snapshot.birthDate,
        orderCount: 1,
      });
    }
  }
  return Array.from(byPatient.values()).sort((a, b) => a.fullName.localeCompare(b.fullName));
}

/** Fetches every diagnostic order referred by the current doctor once per session. Real
 * server-side filtered data (see api/diagnosticOrdersApi.ts) shared by the Patients and Orders
 * tabs so both derive from a single source of truth instead of two separate requests. */
function useReferredOrders(session: SessionUser | null, onSessionExpired: () => void) {
  const { t } = useLocale();
  const [orders, setOrders] = useState<DiagnosticOrder[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const reload = useCallback(async () => {
    if (!session) return;
    setLoading(true);
    setError("");
    try {
      const data = await listReferredOrders(session.tenantId, session.doctorId);
      setOrders(data);
    } catch (e: unknown) {
      setOrders([]);
      setError(resolveApiErrorMessage(e, t, onSessionExpired));
    } finally {
      setLoading(false);
    }
  }, [session]);

  useEffect(() => {
    reload();
  }, [reload]);

  return { orders, loading, error };
}

function LanguageSwitcher() {
  const { locale, setLocale, t } = useLocale();
  return (
    <div className="language-switcher">
      <span className="language-label">{t.appShell.languageSwitcherLabel}: </span>
      <button
        className={`lang-btn ${locale === "es-MX" ? "active" : ""}`}
        onClick={() => setLocale("es-MX")}
      >
        ES
      </button>
      <button
        className={`lang-btn ${locale === "en-US" ? "active" : ""}`}
        onClick={() => setLocale("en-US")}
      >
        EN
      </button>
    </div>
  );
}

function LoginFormView() {
  const { login, loginMock, isLoading } = useSession();
  const { t } = useLocale();
  const [tenantId, setTenantId] = useState("tenant-local");
  const [username, setUsername] = useState("portaluser");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState("");

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    if (!tenantId || !username || !password) {
      setError(t.unexpectedError);
      return;
    }
    const res = await login(tenantId, username, password);
    if (!res.ok) {
      if (res.error === "Account locked or suspended") {
        setError(t.appShell.login.errorSuspended);
      } else {
        setError(t.appShell.login.errorInvalid);
      }
    }
  };

  return (
    <div className="login-card">
      <div className="login-header">
        <h2>{t.appShell.login.title}</h2>
        <p className="login-subtitle">{t.appShell.title}</p>
      </div>
      <form onSubmit={handleSubmit} className="login-form">
        <div className="form-group">
          <label htmlFor="tenantId">{t.appShell.login.tenantId}</label>
          <input
            id="tenantId"
            type="text"
            value={tenantId}
            onChange={(e) => setTenantId(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="username">{t.appShell.login.username}</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">{t.appShell.login.passwordLabel}</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <div className="error-alert">{error}</div>}
        <button type="submit" className="btn-primary" disabled={isLoading}>
          {isLoading ? t.appShell.login.loggingIn : t.appShell.login.submit}
        </button>
      </form>

      <div className="mock-login-section">
        <hr className="divider" />
        <p className="mock-title">Demo Quick-Access / Mock Login</p>
        <div className="mock-buttons">
          {mockSessions.map((mock) => (
            <button
              key={mock.userId}
              className="btn-secondary"
              onClick={() => loginMock(mock.userId)}
            >
              {mock.name}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

function PatientsTab({
  patients,
  loading,
  error,
  onViewResults,
}: {
  patients: ReferredPatient[];
  loading: boolean;
  error: string;
  onViewResults: (patientId: string) => void;
}) {
  const { t } = useLocale();

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (error) return <div className="error-alert">{error}</div>;
  if (patients.length === 0) {
    return (
      <div className="empty-alert">
        {t.appShell.states.empty}
        <p>{t.appShell.patients.emptyHint}</p>
      </div>
    );
  }

  return (
    <div className="card">
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.patients.name}</th>
            <th>{t.appShell.patients.document}</th>
            <th>{t.appShell.patients.birthDate}</th>
            <th>{t.appShell.patients.referredOrders}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {patients.map((patient) => (
            <tr key={patient.patientId}>
              <td>
                <strong>{patient.fullName}</strong>
              </td>
              <td>
                [{patient.documentType}] {patient.documentNumberMasked}
              </td>
              <td>{patient.birthDate ?? "-"}</td>
              <td>{patient.orderCount}</td>
              <td>
                <button className="btn-secondary" onClick={() => onViewResults(patient.patientId)}>
                  {t.appShell.patients.viewResults}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function OrdersTab({
  orders,
  loading,
  error,
}: {
  orders: DiagnosticOrder[];
  loading: boolean;
  error: string;
}) {
  const { t } = useLocale();

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (error) return <div className="error-alert">{error}</div>;
  if (orders.length === 0) return <div className="empty-alert">{t.appShell.states.empty}</div>;

  return (
    <div className="card">
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.orders.orderId}</th>
            <th>{t.appShell.orders.patient}</th>
            <th>{t.appShell.orders.branch}</th>
            <th>{t.appShell.orders.status}</th>
            <th>{t.appShell.orders.createdAt}</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.orderId}>
              <td>
                <code>{order.orderId}</code>
              </td>
              <td>{order.patientSnapshot.fullName}</td>
              <td>{order.branchSnapshot.name}</td>
              <td>
                <span className={`badge badge--${order.status}`}>
                  {String(order.status).toUpperCase()}
                </span>
              </td>
              <td>{order.createdAt ? new Date(order.createdAt).toLocaleString() : "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PatientSelector({
  patients,
  selectedPatientId,
  onSelectPatient,
}: {
  patients: ReferredPatient[];
  selectedPatientId: string;
  onSelectPatient: (patientId: string) => void;
}) {
  const { t } = useLocale();
  return (
    <div className="form-group">
      <label htmlFor="patient-selector">{t.appShell.results.selectPatient}</label>
      <select
        id="patient-selector"
        value={selectedPatientId}
        onChange={(e) => onSelectPatient(e.target.value)}
      >
        <option value="">{t.appShell.results.selectPatientPlaceholder}</option>
        {patients.map((patient) => (
          <option key={patient.patientId} value={patient.patientId}>
            {patient.fullName}
          </option>
        ))}
      </select>
    </div>
  );
}

function ResultsTab({
  session,
  patients,
  selectedPatientId,
  onSelectPatient,
  onSessionExpired,
}: {
  session: SessionUser;
  patients: ReferredPatient[];
  selectedPatientId: string;
  onSelectPatient: (patientId: string) => void;
  onSessionExpired: () => void;
}) {
  const { t } = useLocale();
  const [entries, setEntries] = useState<ResultHistoryEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadHistory() {
      if (!selectedPatientId) {
        setEntries([]);
        return;
      }
      setLoading(true);
      setError("");
      try {
        const data = await getPatientHistoryAsDoctor(
          selectedPatientId,
          session.tenantId,
          session.doctorId,
        );
        setEntries(data.entries);
      } catch (e: unknown) {
        setEntries([]);
        setError(resolveApiErrorMessage(e, t, onSessionExpired));
      } finally {
        setLoading(false);
      }
    }
    loadHistory();
  }, [selectedPatientId, session.tenantId, session.doctorId]);

  function renderBody() {
    if (!selectedPatientId) return <div className="empty-alert">{t.selectPatientFirst}</div>;
    if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
    if (error) return <div className="error-alert">{error}</div>;
    if (entries.length === 0) return <div className="empty-alert">{t.appShell.states.empty}</div>;
    return (
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.results.analyte}</th>
            <th>{t.appShell.results.value}</th>
            <th>{t.appShell.results.range}</th>
            <th>{t.appShell.results.releasedAt}</th>
          </tr>
        </thead>
        <tbody>
          {entries.map((entry) => (
            <tr key={entry.resultId}>
              <td>
                <strong>{entry.analyteName}</strong>
              </td>
              <td>
                <span className={`value-badge ${entry.isAbnormal ? "abnormal" : "normal"}`}>
                  {entry.stringValue} {entry.isAbnormal ? `(${t.appShell.results.abnormal})` : ""}
                </span>
              </td>
              <td>{entry.referenceRange}</td>
              <td>{new Date(entry.releasedAt).toLocaleDateString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  return (
    <div className="card">
      <PatientSelector
        patients={patients}
        selectedPatientId={selectedPatientId}
        onSelectPatient={onSelectPatient}
      />
      {renderBody()}
    </div>
  );
}

function NotificationsTab({
  session,
  patients,
  selectedPatientId,
  onSelectPatient,
  onSessionExpired,
}: {
  session: SessionUser;
  patients: ReferredPatient[];
  selectedPatientId: string;
  onSelectPatient: (patientId: string) => void;
  onSessionExpired: () => void;
}) {
  const { t } = useLocale();
  const [notifications, setNotifications] = useState<ResultNotificationRequest[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadNotifications() {
      if (!selectedPatientId) {
        setNotifications([]);
        return;
      }
      setLoading(true);
      setError("");
      try {
        const history = await getPatientHistoryAsDoctor(
          selectedPatientId,
          session.tenantId,
          session.doctorId,
        );
        const allNotifications: ResultNotificationRequest[] = [];
        for (const entry of history.entries) {
          const forResult = await getResultNotifications(entry.resultId, session.tenantId);
          allNotifications.push(...forResult);
        }
        setNotifications(allNotifications);
      } catch (e: unknown) {
        setNotifications([]);
        setError(resolveApiErrorMessage(e, t, onSessionExpired));
      } finally {
        setLoading(false);
      }
    }
    loadNotifications();
  }, [selectedPatientId, session.tenantId, session.doctorId]);

  function renderBody() {
    if (!selectedPatientId) return <div className="empty-alert">{t.selectPatientFirst}</div>;
    if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
    if (error) return <div className="error-alert">{error}</div>;
    if (notifications.length === 0)
      return <div className="empty-alert">{t.appShell.states.empty}</div>;
    return (
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.notifications.resultId}</th>
            <th>{t.appShell.notifications.channel}</th>
            <th>{t.appShell.notifications.status}</th>
            <th>{t.appShell.notifications.dispatchedAt}</th>
            <th>{t.appShell.notifications.failureReason}</th>
          </tr>
        </thead>
        <tbody>
          {notifications.map((notif) => (
            <tr key={notif.notificationRequestId}>
              <td>
                <code>{notif.resultId}</code>
              </td>
              <td>
                <strong>{notif.channel.toUpperCase()}</strong>
              </td>
              <td>
                <span className={`badge badge--${notif.status}`}>
                  {String(notif.status).toUpperCase()}
                </span>
              </td>
              <td>{notif.dispatchedAt ? new Date(notif.dispatchedAt).toLocaleString() : "-"}</td>
              <td>{notif.failureReason ?? "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  return (
    <div className="card">
      <PatientSelector
        patients={patients}
        selectedPatientId={selectedPatientId}
        onSelectPatient={onSelectPatient}
      />
      {renderBody()}
    </div>
  );
}

function ImagingTab({
  session,
  patients,
  selectedPatientId,
  onSelectPatient,
  onSessionExpired,
}: {
  session: SessionUser;
  patients: ReferredPatient[];
  selectedPatientId: string;
  onSelectPatient: (patientId: string) => void;
  onSessionExpired: () => void;
}) {
  const { t } = useLocale();
  const [packages, setPackages] = useState<ImagingDeliveryPackage[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadImaging() {
      if (!selectedPatientId) {
        setPackages([]);
        return;
      }
      setLoading(true);
      setError("");
      try {
        const data = await getPatientImagingDeliveryPackagesAsDoctor(
          selectedPatientId,
          session.doctorId,
        );
        setPackages(data);
      } catch (e: unknown) {
        setPackages([]);
        setError(resolveApiErrorMessage(e, t, onSessionExpired));
      } finally {
        setLoading(false);
      }
    }
    loadImaging();
  }, [selectedPatientId, session.doctorId]);

  function renderBody() {
    if (!selectedPatientId) return <div className="empty-alert">{t.selectPatientFirst}</div>;
    if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
    if (error) return <div className="error-alert">{error}</div>;
    if (packages.length === 0) return <div className="empty-alert">{t.appShell.states.empty}</div>;
    return (
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.imaging.studyId}</th>
            <th>{t.appShell.imaging.format}</th>
            <th>{t.appShell.imaging.status}</th>
          </tr>
        </thead>
        <tbody>
          {packages.map((pkg) => (
            <tr key={pkg.packageId}>
              <td>
                <code>{pkg.studyId}</code>
              </td>
              <td>{pkg.deliveryFormat}</td>
              <td>
                <span className={`badge badge--${pkg.deliveryStatus.toLowerCase()}`}>
                  {pkg.deliveryStatus}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  return (
    <div className="card">
      <PatientSelector
        patients={patients}
        selectedPatientId={selectedPatientId}
        onSelectPatient={onSelectPatient}
      />
      {renderBody()}
    </div>
  );
}

function TabContent({
  visibleScreens,
  activeTab,
  session,
  patients,
  orders,
  loading,
  error,
  selectedPatientId,
  onSelectPatient,
  onSessionExpired,
  onViewResults,
}: {
  visibleScreens: ScreenKey[];
  activeTab: ScreenKey | "";
  session: SessionUser;
  patients: ReferredPatient[];
  orders: DiagnosticOrder[];
  loading: boolean;
  error: string;
  selectedPatientId: string;
  onSelectPatient: (patientId: string) => void;
  onSessionExpired: () => void;
  onViewResults: (patientId: string) => void;
}) {
  const { t } = useLocale();

  if (visibleScreens.length === 0) {
    return <div className="error-alert">{t.appShell.states.noPermission}</div>;
  }
  switch (activeTab) {
    case "patients":
      return (
        <PatientsTab
          patients={patients}
          loading={loading}
          error={error}
          onViewResults={onViewResults}
        />
      );
    case "orders":
      return <OrdersTab orders={orders} loading={loading} error={error} />;
    case "results":
      return (
        <ResultsTab
          session={session}
          patients={patients}
          selectedPatientId={selectedPatientId}
          onSelectPatient={onSelectPatient}
          onSessionExpired={onSessionExpired}
        />
      );
    case "notifications":
      return (
        <NotificationsTab
          session={session}
          patients={patients}
          selectedPatientId={selectedPatientId}
          onSelectPatient={onSelectPatient}
          onSessionExpired={onSessionExpired}
        />
      );
    case "imaging":
      return (
        <ImagingTab
          session={session}
          patients={patients}
          selectedPatientId={selectedPatientId}
          onSelectPatient={onSelectPatient}
          onSessionExpired={onSessionExpired}
        />
      );
    default:
      return <div className="empty-alert">{t.appShell.states.empty}</div>;
  }
}

function AppContent() {
  const { session, logout, expireSession } = useSession();
  const { t } = useLocale();
  const [selectedPatientId, setSelectedPatientId] = useState("");

  const onSessionExpired = useCallback(() => {
    expireSession();
  }, [expireSession]);

  const { orders, loading, error } = useReferredOrders(session, onSessionExpired);
  const patients = deriveReferredPatients(orders);

  const permissions = session ? permissionsForRoles([session.roleCode]) : new Set();
  const visibleScreens = ORDERED_SCREENS.filter((screen) =>
    permissions.has(SCREEN_TO_PERMISSION[screen]),
  );
  const [activeTab, setActiveTab] = useState<ScreenKey | "">("");

  useEffect(() => {
    if (visibleScreens.length > 0 && !visibleScreens.includes(activeTab as ScreenKey)) {
      setActiveTab(visibleScreens[0]);
    }
  }, [session?.roleCode]);

  if (!session) {
    return (
      <div className="login-wrapper">
        <header className="portal-header">
          <LanguageSwitcher />
        </header>
        <main className="login-main">
          <LoginFormView />
        </main>
      </div>
    );
  }

  const handleViewResults = (patientId: string) => {
    setSelectedPatientId(patientId);
    setActiveTab("results");
  };

  const tabLabels: Record<ScreenKey, string> = {
    patients: t.appShell.tabs.patients,
    results: t.appShell.tabs.results,
    orders: t.appShell.tabs.orders,
    notifications: t.appShell.tabs.notifications,
    imaging: t.appShell.tabs.imaging,
  };

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <div className="header-brand">
          <h1>{t.appShell.title}</h1>
          <p className="welcome-tag">
            {t.appShell.states.welcome}, <strong>{session.name}</strong>
          </p>
        </div>
        <div className="header-actions">
          <LanguageSwitcher />
          <button className="btn-logout" onClick={logout}>
            {t.appShell.states.logout}
          </button>
        </div>
      </header>

      <div className="dashboard-body">
        <aside className="dashboard-sidebar">
          <nav className="sidebar-nav" aria-label={t.appShell.navAriaLabel}>
            {visibleScreens.map((screen) => (
              <button
                key={screen}
                className={`nav-item ${activeTab === screen ? "active" : ""}`}
                onClick={() => setActiveTab(screen)}
              >
                {tabLabels[screen]}
              </button>
            ))}
          </nav>
        </aside>

        <main className="dashboard-content">
          <TabContent
            visibleScreens={visibleScreens}
            activeTab={activeTab}
            session={session}
            patients={patients}
            orders={orders}
            loading={loading}
            error={error}
            selectedPatientId={selectedPatientId}
            onSelectPatient={setSelectedPatientId}
            onSessionExpired={onSessionExpired}
            onViewResults={handleViewResults}
          />
        </main>
      </div>
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
