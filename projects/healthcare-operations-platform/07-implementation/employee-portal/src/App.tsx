import { useState } from "react";
import { AppShell, type ScreenKey } from "./components/layout/AppShell";
import { TenantsScreen } from "./components/screens/TenantsScreen";
import { LaboratoriesScreen } from "./components/screens/LaboratoriesScreen";
import { BranchesScreen } from "./components/screens/BranchesScreen";
import { UsersScreen } from "./components/screens/UsersScreen";
import { RoleAssignmentsScreen } from "./components/screens/RoleAssignmentsScreen";
import { AuditEventsScreen } from "./components/screens/AuditEventsScreen";
import { DiagnosticCatalogScreen } from "./components/screens/DiagnosticCatalogScreen";
import { PersonSearchScreen } from "./components/screens/PersonSearchScreen";
import { PatientsScreen } from "./components/screens/PatientsScreen";
import { DoctorsScreen } from "./components/screens/DoctorsScreen";
import { PatientRegistrationsScreen } from "./components/screens/PatientRegistrationsScreen";
import { AdminScopeProvider } from "./state/AdminScopeContext";

function renderScreen(screen: ScreenKey) {
  switch (screen) {
    case "tenants":
      return <TenantsScreen />;
    case "laboratories":
      return <LaboratoriesScreen />;
    case "branches":
      return <BranchesScreen />;
    case "users":
      return <UsersScreen />;
    case "role-assignments":
      return <RoleAssignmentsScreen />;
    case "audit-events":
      return <AuditEventsScreen />;
    case "diagnostic-catalog":
      return <DiagnosticCatalogScreen />;
    case "person-search":
      return <PersonSearchScreen />;
    case "patients":
      return <PatientsScreen />;
    case "doctors":
      return <DoctorsScreen />;
    case "patient-registrations":
      return <PatientRegistrationsScreen />;
    default:
      return null;
  }
}

export function App() {
  const [activeScreen, setActiveScreen] = useState<ScreenKey>("tenants");

  return (
    <AdminScopeProvider>
      <AppShell activeScreen={activeScreen} onSelectScreen={setActiveScreen}>
        {renderScreen(activeScreen)}
      </AppShell>
    </AdminScopeProvider>
  );
}
