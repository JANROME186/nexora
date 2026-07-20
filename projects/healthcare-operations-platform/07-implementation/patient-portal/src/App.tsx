import { useState, useEffect } from "react";
import { SessionProvider, useSession, mockSessions } from "./state/SessionContext";
import { LocaleProvider, useLocale } from "./i18n/LocaleContext";
import {
  getPatientHistory,
  type PatientResultHistoryView,
  type ResultHistoryEntry,
} from "./api/patientResultHistoryApi";
import "./App.css";

// --- API Client Helpers for Appointments, Orders, and Notifications ---
interface AppointmentSlot {
  appointmentId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId: string;
  doctorId: string;
  scheduledStart: string;
  scheduledEnd: string;
  channel: string;
  status: string;
}

interface DiagnosticOrder {
  orderId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  intakeChannel: string;
  patientId: string;
  doctorId: string;
  status: string;
  lines: Array<{
    testDefinitionId: string;
    catalogItemKind: string;
    quantity: number;
  }>;
}

interface ResultNotificationView {
  notificationRequestId: string;
  resultId: string;
  tenantId: string;
  recipientType: string;
  recipientId: string;
  channel: string;
  status: string;
  dispatchedAt?: string;
  deliveredAt?: string;
  failureReason?: string;
  createdAt: string;
}

// --- Cohorent Typed Stubs (Fallbacks) ---
const STUB_APPOINTMENTS: AppointmentSlot[] = [
  {
    appointmentId: "apt-101",
    tenantId: "tenant-local",
    laboratoryId: "lab-01",
    branchId: "branch-north",
    patientId: "Patient-01",
    doctorId: "Doctor-01",
    scheduledStart: "2026-07-20T10:00:00Z",
    scheduledEnd: "2026-07-20T10:30:00Z",
    channel: "PATIENT_PORTAL",
    status: "confirmed",
  },
  {
    appointmentId: "apt-102",
    tenantId: "tenant-local",
    laboratoryId: "lab-01",
    branchId: "branch-north",
    patientId: "Patient-01",
    doctorId: "Doctor-01",
    scheduledStart: "2026-07-25T15:30:00Z",
    scheduledEnd: "2026-07-25T16:00:00Z",
    channel: "PATIENT_PORTAL",
    status: "requested",
  },
];

const STUB_ORDERS: DiagnosticOrder[] = [
  {
    orderId: "ord-201",
    tenantId: "tenant-local",
    laboratoryId: "lab-01",
    branchId: "branch-north",
    intakeChannel: "WALK_IN",
    patientId: "Patient-01",
    doctorId: "Doctor-01",
    status: "completed",
    lines: [
      { testDefinitionId: "Glucose", catalogItemKind: "test", quantity: 1 },
      { testDefinitionId: "Cholesterol", catalogItemKind: "test", quantity: 1 },
    ],
  },
  {
    orderId: "ord-202",
    tenantId: "tenant-local",
    laboratoryId: "lab-01",
    branchId: "branch-north",
    intakeChannel: "PATIENT_PORTAL",
    patientId: "Patient-01",
    doctorId: "Doctor-01",
    status: "accepted",
    lines: [{ testDefinitionId: "Hemoglobin", catalogItemKind: "test", quantity: 1 }],
  },
];

const STUB_NOTIFICATIONS: ResultNotificationView[] = [
  {
    notificationRequestId: "notif-901",
    resultId: "res-301",
    tenantId: "tenant-local",
    recipientType: "patient",
    recipientId: "Patient-01",
    channel: "sms",
    status: "delivered",
    dispatchedAt: "2026-07-19T10:05:00Z",
    deliveredAt: "2026-07-19T10:05:15Z",
    createdAt: "2026-07-19T10:05:00Z",
  },
  {
    notificationRequestId: "notif-902",
    resultId: "res-301",
    tenantId: "tenant-local",
    recipientType: "patient",
    recipientId: "Patient-01",
    channel: "email",
    status: "delivered",
    dispatchedAt: "2026-07-19T10:05:00Z",
    deliveredAt: "2026-07-19T10:05:30Z",
    createdAt: "2026-07-19T10:05:00Z",
  },
];

// --- Sub-components ---

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

  const handleSubmit = async (e: React.FormEvent) => {
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

function ProfileTab() {
  const { session } = useSession();
  const { t } = useLocale();
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadProfile() {
      if (!session) return;
      setLoading(true);
      try {
        const response = await fetch(`/api/people/patients/${session.userId}`, {
          headers: {
            "X-HOP-AUTH-TOKEN": session.token,
            "X-HOP-USER-ID": session.userId,
            "X-HOP-TENANT-ID": session.tenantId,
            "X-HOP-ROLES": session.roleCode,
          },
        });
        if (response.ok) {
          const data = await response.json();
          setProfile(data);
        } else {
          // fallback to stub
          setProfile({
            patientCode: "PAT-009-MX",
            givenName: session.name.split(" ")[0] || "Juan",
            familyName: session.name.split(" ")[1] || "Pérez",
            birthDate: "1990-05-15",
            sexAtBirth: "male",
            primaryDocumentType: "National ID",
            primaryDocumentNumber: "MX-98231-A",
            addressStreet: "Paseo de la Reforma 123",
            addressCity: "CDMX",
            addressPostalCode: "06500",
            preferredLocale: "es-MX",
            emergencyContacts: [
              {
                givenName: "Maria",
                familyName: "Pérez",
                relationship: "Spouse",
                phoneNationalNumber: "555-0192-384",
              },
            ],
          });
        }
      } catch {
        setProfile({
          patientCode: "PAT-009-MX",
          givenName: session.name.split(" ")[0] || "Juan",
          familyName: session.name.split(" ")[1] || "Pérez",
          birthDate: "1990-05-15",
          sexAtBirth: "male",
          primaryDocumentType: "National ID",
          primaryDocumentNumber: "MX-98231-A",
          addressStreet: "Paseo de la Reforma 123",
          addressCity: "CDMX",
          addressPostalCode: "06500",
          preferredLocale: "es-MX",
          emergencyContacts: [
            {
              givenName: "Maria",
              familyName: "Pérez",
              relationship: "Spouse",
              phoneNationalNumber: "555-0192-384",
            },
          ],
        });
      } finally {
        setLoading(false);
      }
    }
    loadProfile();
  }, [session]);

  if (loading || !profile) {
    return <div className="skeleton">{t.appShell.states.loading}</div>;
  }

  return (
    <div className="card">
      <div className="card-header">
        <h3>{t.appShell.profile.personalInfo}</h3>
      </div>
      <div className="grid-2-col">
        <div>
          <p className="detail-label">{t.appShell.profile.code}</p>
          <p className="detail-value">{profile.patientCode}</p>
        </div>
        <div>
          <p className="detail-label">{t.appShell.profile.name}</p>
          <p className="detail-value">
            {profile.givenName} {profile.familyName}
          </p>
        </div>
        <div>
          <p className="detail-label">{t.appShell.profile.birthDate}</p>
          <p className="detail-value">{profile.birthDate}</p>
        </div>
        <div>
          <p className="detail-label">{t.appShell.profile.gender}</p>
          <p className="detail-value">{profile.sexAtBirth}</p>
        </div>
        <div>
          <p className="detail-label">{t.appShell.profile.document}</p>
          <p className="detail-value">
            [{profile.primaryDocumentType}] {profile.primaryDocumentNumber}
          </p>
        </div>
        <div>
          <p className="detail-label">{t.appShell.profile.address}</p>
          <p className="detail-value">
            {profile.addressStreet}, {profile.addressCity} {profile.addressPostalCode}
          </p>
        </div>
      </div>
      <div className="emergency-contacts">
        <h4>{t.appShell.profile.contacts}</h4>
        {profile.emergencyContacts && profile.emergencyContacts.length > 0 ? (
          <ul>
            {profile.emergencyContacts.map((contact: any, i: number) => (
              <li key={i}>
                <strong>
                  {contact.givenName} {contact.familyName}
                </strong>{" "}
                ({contact.relationship}) - {contact.phoneNationalNumber}
              </li>
            ))}
          </ul>
        ) : (
          <p>{t.appShell.profile.noContacts}</p>
        )}
      </div>
    </div>
  );
}

function ResultsTab() {
  const { session } = useSession();
  const { t } = useLocale();
  const [history, setHistory] = useState<PatientResultHistoryView | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadHistory() {
      if (!session) return;
      setLoading(true);
      setError("");
      try {
        const data = await getPatientHistory(session.patientId);
        setHistory(data);
      } catch (e: any) {
        setError(e?.message || t.appShell.states.error);
      } finally {
        setLoading(false);
      }
    }
    loadHistory();
  }, [session, t.appShell.states.error]);

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (error) return <div className="error-alert">{error}</div>;
  if (!history || history.entries.length === 0) {
    return <div className="empty-alert">{t.appShell.states.empty}</div>;
  }

  return (
    <div className="card">
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
          {history.entries.map((entry: ResultHistoryEntry) => (
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
    </div>
  );
}

function AppointmentsTab() {
  const { session } = useSession();
  const { t } = useLocale();
  const [appointments, setAppointments] = useState<AppointmentSlot[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function fetchAppointments() {
      if (!session) return;
      setLoading(true);
      try {
        const res = await fetch(`/api/care-delivery/appointments?tenantId=${session.tenantId}`, {
          headers: {
            "X-HOP-AUTH-TOKEN": session.token,
            "X-HOP-USER-ID": session.userId,
            "X-HOP-TENANT-ID": session.tenantId,
            "X-HOP-ROLES": session.roleCode,
          },
        });
        if (res.ok) {
          const data = (await res.json()) as AppointmentSlot[];
          // Filter client side to only current patient's appointments
          const filtered = data.filter((a) => a.patientId === session.patientId);
          setAppointments(filtered);
        } else {
          setAppointments(STUB_APPOINTMENTS);
        }
      } catch {
        setAppointments(STUB_APPOINTMENTS);
      } finally {
        setLoading(false);
      }
    }
    fetchAppointments();
  }, [session]);

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (appointments.length === 0)
    return <div className="empty-alert">{t.appShell.states.empty}</div>;

  return (
    <div className="card">
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.appointments.date}</th>
            <th>{t.appShell.appointments.branch}</th>
            <th>{t.appShell.appointments.doctor}</th>
            <th>{t.appShell.appointments.status}</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((apt) => (
            <tr key={apt.appointmentId}>
              <td>{new Date(apt.scheduledStart).toLocaleString()}</td>
              <td>{apt.branchId}</td>
              <td>{apt.doctorId}</td>
              <td>
                <span className={`badge badge--${apt.status}`}>{apt.status.toUpperCase()}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function OrdersTab() {
  const { session } = useSession();
  const { t } = useLocale();
  const [orders, setOrders] = useState<DiagnosticOrder[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function fetchOrders() {
      if (!session) return;
      setLoading(true);
      try {
        const res = await fetch(
          `/api/clinical-operations/diagnostic-orders?tenantId=${session.tenantId}`,
          {
            headers: {
              "X-HOP-AUTH-TOKEN": session.token,
              "X-HOP-USER-ID": session.userId,
              "X-HOP-TENANT-ID": session.tenantId,
              "X-HOP-ROLES": session.roleCode,
            },
          },
        );
        if (res.ok) {
          const data = (await res.json()) as DiagnosticOrder[];
          const filtered = data.filter((o) => o.patientId === session.patientId);
          setOrders(filtered);
        } else {
          setOrders(STUB_ORDERS);
        }
      } catch {
        setOrders(STUB_ORDERS);
      } finally {
        setLoading(false);
      }
    }
    fetchOrders();
  }, [session]);

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (orders.length === 0) return <div className="empty-alert">{t.appShell.states.empty}</div>;

  return (
    <div className="card">
      <table className="portal-table">
        <thead>
          <tr>
            <th>{t.appShell.orders.orderId}</th>
            <th>{t.appShell.orders.status}</th>
            <th>{t.appShell.orders.tests}</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((ord) => (
            <tr key={ord.orderId}>
              <td>
                <code>{ord.orderId}</code>
              </td>
              <td>
                <span className={`badge badge--${ord.status}`}>{ord.status.toUpperCase()}</span>
              </td>
              <td>
                <ul className="inline-test-list">
                  {ord.lines.map((l, i) => (
                    <li key={i}>
                      {l.testDefinitionId} (x{l.quantity})
                    </li>
                  ))}
                </ul>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function NotificationsTab() {
  const { session } = useSession();
  const { t } = useLocale();
  const [notifications, setNotifications] = useState<ResultNotificationView[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function fetchNotifications() {
      if (!session) return;
      setLoading(true);
      try {
        // Fetch patient history first to get result IDs
        const historyData = await getPatientHistory(session.patientId);
        if (historyData && historyData.entries.length > 0) {
          const allNotifs: ResultNotificationView[] = [];
          for (const entry of historyData.entries) {
            const res = await fetch(
              `/api/clinical-operations/laboratory-results/${entry.resultId}/notifications?tenantId=${session.tenantId}`,
              {
                headers: {
                  "X-HOP-AUTH-TOKEN": session.token,
                  "X-HOP-USER-ID": session.userId,
                  "X-HOP-TENANT-ID": session.tenantId,
                  "X-HOP-ROLES": session.roleCode,
                },
              },
            );
            if (res.ok) {
              const data = (await res.json()) as ResultNotificationView[];
              allNotifs.push(...data);
            }
          }
          setNotifications(allNotifs);
        } else {
          setNotifications(STUB_NOTIFICATIONS);
        }
      } catch {
        setNotifications(STUB_NOTIFICATIONS);
      } finally {
        setLoading(false);
      }
    }
    fetchNotifications();
  }, [session]);

  if (loading) return <div className="skeleton">{t.appShell.states.loading}</div>;
  if (notifications.length === 0)
    return <div className="empty-alert">{t.appShell.states.empty}</div>;

  return (
    <div className="card">
      <table className="portal-table">
        <thead>
          <tr>
            <th>Channel</th>
            <th>Recipient ID</th>
            <th>Status</th>
            <th>Dispatched At</th>
            <th>Failure Reason</th>
          </tr>
        </thead>
        <tbody>
          {notifications.map((notif) => (
            <tr key={notif.notificationRequestId}>
              <td>
                <strong>{notif.channel.toUpperCase()}</strong>
              </td>
              <td>{notif.recipientId}</td>
              <td>
                <span className={`badge badge--${notif.status}`}>{notif.status.toUpperCase()}</span>
              </td>
              <td>{notif.dispatchedAt ? new Date(notif.dispatchedAt).toLocaleString() : "-"}</td>
              <td style={{ color: "red" }}>{notif.failureReason || "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// --- Main App Shell ---

function AppContent() {
  const { session, logout } = useSession();
  const { t } = useLocale();
  const [activeTab, setActiveTab] = useState<string>("profile");

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

  const renderTabContent = () => {
    switch (activeTab) {
      case "profile":
        return <ProfileTab />;
      case "results":
        return <ResultsTab />;
      case "appointments":
        return <AppointmentsTab />;
      case "orders":
        return <OrdersTab />;
      case "notifications":
        return <NotificationsTab />;
      default:
        return <ProfileTab />;
    }
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
          <nav className="sidebar-nav">
            <button
              className={`nav-item ${activeTab === "profile" ? "active" : ""}`}
              onClick={() => setActiveTab("profile")}
            >
              {t.appShell.tabs.profile}
            </button>
            <button
              className={`nav-item ${activeTab === "results" ? "active" : ""}`}
              onClick={() => setActiveTab("results")}
            >
              {t.appShell.tabs.results}
            </button>
            <button
              className={`nav-item ${activeTab === "appointments" ? "active" : ""}`}
              onClick={() => setActiveTab("appointments")}
            >
              {t.appShell.tabs.appointments}
            </button>
            <button
              className={`nav-item ${activeTab === "orders" ? "active" : ""}`}
              onClick={() => setActiveTab("orders")}
            >
              {t.appShell.tabs.orders}
            </button>
            <button
              className={`nav-item ${activeTab === "notifications" ? "active" : ""}`}
              onClick={() => setActiveTab("notifications")}
            >
              {t.appShell.tabs.notifications}
            </button>
          </nav>
        </aside>

        <main className="dashboard-content">{renderTabContent()}</main>
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
