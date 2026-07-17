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
import { ReceptionScreen } from "./components/screens/ReceptionScreen";
import { DiagnosticOrdersScreen } from "./components/screens/DiagnosticOrdersScreen";
import { CashSessionsScreen } from "./components/screens/CashSessionsScreen";
import { SalesScreen } from "./components/screens/SalesScreen";
import { BillingRequestsScreen } from "./components/screens/BillingRequestsScreen";
import { SampleCollectionScreen } from "./components/screens/SampleCollectionScreen";
import { SampleLabelingScreen } from "./components/screens/SampleLabelingScreen";
import { SampleReceptionScreen } from "./components/screens/SampleReceptionScreen";
import { LaboratoryProcessingScreen } from "./components/screens/LaboratoryProcessingScreen";
import { TechnicalValidationScreen } from "./components/screens/TechnicalValidationScreen";
import { MedicalValidationScreen } from "./components/screens/MedicalValidationScreen";
import { ResultReleaseScreen } from "./components/screens/ResultReleaseScreen";
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
    case "reception":
      return <ReceptionScreen />;
    case "diagnostic-orders":
      return <DiagnosticOrdersScreen />;
    case "cash-sessions":
      return <CashSessionsScreen />;
    case "sales":
      return <SalesScreen />;
    case "billing-requests":
      return <BillingRequestsScreen />;
    case "sample-collection":
      return <SampleCollectionScreen />;
    case "sample-labeling":
      return <SampleLabelingScreen />;
    case "sample-reception":
      return <SampleReceptionScreen />;
    case "laboratory-processing":
      return <LaboratoryProcessingScreen />;
    case "technical-validation":
      return <TechnicalValidationScreen />;
    case "medical-validation":
      return <MedicalValidationScreen />;
    case "result-release":
      return <ResultReleaseScreen />;
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
