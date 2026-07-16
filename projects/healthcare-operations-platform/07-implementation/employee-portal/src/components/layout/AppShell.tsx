import type { ReactNode } from "react";

export type ScreenKey =
  | "tenants"
  | "laboratories"
  | "branches"
  | "users"
  | "role-assignments"
  | "audit-events"
  | "diagnostic-catalog"
  | "person-search"
  | "patients"
  | "doctors"
  | "patient-registrations"
  | "reception"
  | "diagnostic-orders";

interface ScreenTab {
  key: ScreenKey;
  label: string;
}

const TABS: ScreenTab[] = [
  { key: "tenants", label: "Tenants" },
  { key: "laboratories", label: "Laboratories" },
  { key: "branches", label: "Branches" },
  { key: "users", label: "Users" },
  { key: "role-assignments", label: "Role Assignments" },
  { key: "audit-events", label: "Audit Events" },
  { key: "diagnostic-catalog", label: "Diagnostic Catalog" },
  { key: "person-search", label: "People Search" },
  { key: "patients", label: "Patients" },
  { key: "doctors", label: "Doctors" },
  { key: "patient-registrations", label: "Patient Registrations" },
  { key: "reception", label: "Front Desk" },
  { key: "diagnostic-orders", label: "Diagnostic Orders" },
];

interface AppShellProps {
  activeScreen: ScreenKey;
  onSelectScreen: (screen: ScreenKey) => void;
  children: ReactNode;
}

/**
 * Base navigation shell for the employee portal administration screens.
 */
export function AppShell({ activeScreen, onSelectScreen, children }: AppShellProps) {
  return (
    <div className="app-shell">
      <header className="app-shell__header">
        <h1>Healthcare Operations Platform - Employee Portal Administration</h1>
        <p>
          Platform Foundation, Diagnostic Catalog, People and Clinical Master Data, and Front Desk
          and Care Delivery: administration, audit, catalog configuration, patient/doctor records
          and diagnostic order intake.
        </p>
      </header>
      <nav className="app-shell__nav" aria-label="Administration screens">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            type="button"
            className={
              tab.key === activeScreen ? "app-shell__tab app-shell__tab--active" : "app-shell__tab"
            }
            aria-current={tab.key === activeScreen ? "page" : undefined}
            onClick={() => onSelectScreen(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </nav>
      <main className="app-shell__content">{children}</main>
    </div>
  );
}
