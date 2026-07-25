import type { ReactNode } from "react";
import { useLocale, type Locale } from "../../i18n/LocaleContext";
import { useSession } from "../../state/SessionContext";
import { SCREEN_TO_PERMISSION, type ScreenKey } from "../../state/permissions";

export type { ScreenKey };

/** Maps each ScreenKey to its key in the locale catalog's `appShell.tabs` object. */
const SCREEN_TAB_LABEL_KEYS = {
  tenants: "tenants",
  laboratories: "laboratories",
  branches: "branches",
  users: "users",
  "role-assignments": "roleAssignments",
  "audit-events": "auditEvents",
  "diagnostic-catalog": "diagnosticCatalog",
  "person-search": "personSearch",
  patients: "patients",
  doctors: "doctors",
  "patient-registrations": "patientRegistrations",
  reception: "reception",
  "diagnostic-orders": "diagnosticOrders",
  "cash-sessions": "cashSessions",
  sales: "sales",
  "billing-requests": "billingRequests",
  "sample-collection": "sampleCollection",
  "sample-labeling": "sampleLabeling",
  "sample-reception": "sampleReception",
  "laboratory-processing": "laboratoryProcessing",
  "technical-validation": "technicalValidation",
  "medical-validation": "medicalValidation",
  "result-release": "resultRelease",
  "result-search": "resultSearch",
  "result-reports": "resultReports",
  "critical-escalations": "criticalEscalations",
  "result-notifications": "resultNotifications",
  "integration-endpoints": "integrationEndpoints",
  "api-management": "apiManagement",
  "migration-jobs": "migrationJobs",
  "inventory-catalog": "inventoryCatalog",
  "inventory-reagents": "inventoryReagents",
  "inventory-lots": "inventoryLots",
  "inventory-procurement": "inventoryProcurement",
  "inventory-stock-movements": "inventoryStockMovements",
  "inventory-adjustments": "inventoryAdjustments",
  "inventory-waste": "inventoryWaste",
  "internal-quality-controls": "internalQualityControls",
  equipment: "equipment",
  calibrations: "calibrations",
  maintenance: "maintenance",
  "public-content-review": "publicContentReview",
  "public-appointment-requests": "publicAppointmentRequests",
  "public-quotation-requests": "publicQuotationRequests",
  "external-quality-controls": "externalQualityControls",
  "capa-management": "capaManagement",
  "audit-management": "auditManagement",
  "compliance-evidence": "complianceEvidence",
  "quality-event-intake": "qualityEventIntake",
  "marketplace-packages": "marketplacePackages",
  "marketplace-offers": "marketplaceOffers",
  "marketplace-entitlements": "marketplaceEntitlements",
  "marketplace-installations": "marketplaceInstallations",
} as const satisfies Record<ScreenKey, string>;

interface ScreenTab {
  key: ScreenKey;
  labelKey: (typeof SCREEN_TAB_LABEL_KEYS)[ScreenKey];
}

const TABS: ScreenTab[] = (Object.keys(SCREEN_TAB_LABEL_KEYS) as ScreenKey[]).map((key) => ({
  key,
  labelKey: SCREEN_TAB_LABEL_KEYS[key],
}));

const LOCALE_OPTIONS: { locale: Locale; label: string }[] = [
  { locale: "es-MX", label: "ES" },
  { locale: "en-US", label: "EN" },
];

interface AppShellProps {
  activeScreen: ScreenKey;
  onSelectScreen: (screen: ScreenKey) => void;
  children: ReactNode;
}

/**
 * Base navigation shell for the employee portal administration screens. Navigation tabs are
 * filtered to the current session's permissions (enterprise-product-foundation-standard
 * `iam_permission_model`: unauthorized navigation must be hidden, not just disabled) and header
 * text plus tab labels are sourced from the active locale (`localization_and_i18n` foundation).
 */
export function AppShell({ activeScreen, onSelectScreen, children }: AppShellProps) {
  const { locale, setLocale, t } = useLocale();
  const { permissions } = useSession();

  const visibleTabs = TABS.filter((tab) => permissions.has(SCREEN_TO_PERMISSION[tab.key]));

  return (
    <div className="app-shell">
      <header className="app-shell__header">
        <div className="app-shell__header-row">
          <div>
            <h1>{t.appShell.title}</h1>
            <p>{t.appShell.subtitle}</p>
          </div>
          <div
            className="app-shell__locale-switch"
            role="group"
            aria-label={t.appShell.languageSwitcherLabel}
          >
            {LOCALE_OPTIONS.map((option) => (
              <button
                key={option.locale}
                type="button"
                className={
                  option.locale === locale
                    ? "app-shell__locale-button app-shell__locale-button--active"
                    : "app-shell__locale-button"
                }
                aria-pressed={option.locale === locale}
                onClick={() => setLocale(option.locale)}
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>
      </header>
      <nav className="app-shell__nav" aria-label={t.appShell.navAriaLabel}>
        {visibleTabs.map((tab) => (
          <button
            key={tab.key}
            type="button"
            className={
              tab.key === activeScreen ? "app-shell__tab app-shell__tab--active" : "app-shell__tab"
            }
            aria-current={tab.key === activeScreen ? "page" : undefined}
            onClick={() => onSelectScreen(tab.key)}
          >
            {t.appShell.tabs[tab.labelKey]}
          </button>
        ))}
      </nav>
      <main className="app-shell__content">{children}</main>
    </div>
  );
}
