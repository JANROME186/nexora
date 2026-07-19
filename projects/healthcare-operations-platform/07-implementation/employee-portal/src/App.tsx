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
import { ResultSearchScreen } from "./components/screens/ResultSearchScreen";
import { ResultReportsScreen } from "./components/screens/ResultReportsScreen";
import { CriticalEscalationsScreen } from "./components/screens/CriticalEscalationsScreen";
import { ResultNotificationsScreen } from "./components/screens/ResultNotificationsScreen";
import { IntegrationEndpointsScreen } from "./components/screens/IntegrationEndpointsScreen";
import { ApiManagementScreen } from "./components/screens/ApiManagementScreen";
import { MigrationJobsScreen } from "./components/screens/MigrationJobsScreen";
import { AdminScopeProvider } from "./state/AdminScopeContext";
import { SessionProvider } from "./state/SessionContext";
import { LocaleProvider } from "./i18n/LocaleContext";

const SCREEN_COMPONENTS = {
  tenants: TenantsScreen,
  laboratories: LaboratoriesScreen,
  branches: BranchesScreen,
  users: UsersScreen,
  "role-assignments": RoleAssignmentsScreen,
  "audit-events": AuditEventsScreen,
  "diagnostic-catalog": DiagnosticCatalogScreen,
  "person-search": PersonSearchScreen,
  patients: PatientsScreen,
  doctors: DoctorsScreen,
  "patient-registrations": PatientRegistrationsScreen,
  reception: ReceptionScreen,
  "diagnostic-orders": DiagnosticOrdersScreen,
  "cash-sessions": CashSessionsScreen,
  sales: SalesScreen,
  "billing-requests": BillingRequestsScreen,
  "sample-collection": SampleCollectionScreen,
  "sample-labeling": SampleLabelingScreen,
  "sample-reception": SampleReceptionScreen,
  "laboratory-processing": LaboratoryProcessingScreen,
  "technical-validation": TechnicalValidationScreen,
  "medical-validation": MedicalValidationScreen,
  "result-release": ResultReleaseScreen,
  "result-search": ResultSearchScreen,
  "result-reports": ResultReportsScreen,
  "critical-escalations": CriticalEscalationsScreen,
  "result-notifications": ResultNotificationsScreen,
  "integration-endpoints": IntegrationEndpointsScreen,
  "api-management": ApiManagementScreen,
  "migration-jobs": MigrationJobsScreen,
} as const satisfies Record<ScreenKey, () => JSX.Element>;

function renderScreen(screen: ScreenKey) {
  const ScreenComponent = SCREEN_COMPONENTS[screen];
  return <ScreenComponent />;
}

export function App() {
  const [activeScreen, setActiveScreen] = useState<ScreenKey>("tenants");

  return (
    <LocaleProvider>
      <SessionProvider>
        <AdminScopeProvider>
          <AppShell activeScreen={activeScreen} onSelectScreen={setActiveScreen}>
            {renderScreen(activeScreen)}
          </AppShell>
        </AdminScopeProvider>
      </SessionProvider>
    </LocaleProvider>
  );
}
