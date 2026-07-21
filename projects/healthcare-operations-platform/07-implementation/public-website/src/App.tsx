import type { ReactNode } from "react";
import { Footer } from "./components/layout/Footer";
import { Header } from "./components/layout/Header";
import { SkipLink } from "./components/layout/SkipLink";
import { LocaleProvider } from "./i18n/LocaleContext";
import { AppointmentRequestPage } from "./pages/AppointmentRequestPage";
import { HomePage } from "./pages/HomePage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { PanelDetailPage } from "./pages/PanelDetailPage";
import { PanelsPage } from "./pages/PanelsPage";
import { PreparationDetailPage } from "./pages/PreparationDetailPage";
import { PreparationsPage } from "./pages/PreparationsPage";
import { PrivacyPage } from "./pages/PrivacyPage";
import { QuotationRequestPage } from "./pages/QuotationRequestPage";
import { ServiceDetailPage } from "./pages/ServiceDetailPage";
import { ServicesPage } from "./pages/ServicesPage";
import { TestDetailPage } from "./pages/TestDetailPage";
import { TestsPage } from "./pages/TestsPage";
import { RouterProvider, useRouter } from "./router/Router";
import { ROUTES, matchPath } from "./router/routes";
import "./styles.css";

interface RouteEntry {
  pattern: string;
  render: (params: Record<string, string>) => ReactNode;
}

const routeEntries: RouteEntry[] = [
  { pattern: ROUTES.home, render: () => <HomePage /> },
  { pattern: ROUTES.services, render: () => <ServicesPage /> },
  {
    pattern: ROUTES.serviceDetail,
    render: (params) => <ServiceDetailPage serviceId={params.id} />,
  },
  { pattern: ROUTES.tests, render: () => <TestsPage /> },
  { pattern: ROUTES.testDetail, render: (params) => <TestDetailPage testId={params.id} /> },
  { pattern: ROUTES.panels, render: () => <PanelsPage /> },
  { pattern: ROUTES.panelDetail, render: (params) => <PanelDetailPage panelId={params.id} /> },
  { pattern: ROUTES.preparations, render: () => <PreparationsPage /> },
  {
    pattern: ROUTES.preparationDetail,
    render: (params) => <PreparationDetailPage preparationId={params.id} />,
  },
  { pattern: ROUTES.appointmentRequest, render: () => <AppointmentRequestPage /> },
  { pattern: ROUTES.quotationRequest, render: () => <QuotationRequestPage /> },
  { pattern: ROUTES.privacy, render: () => <PrivacyPage /> },
];

function RouteView() {
  const { pathname } = useRouter();
  for (const entry of routeEntries) {
    const params = matchPath(entry.pattern, pathname);
    if (params) {
      return <>{entry.render(params)}</>;
    }
  }
  return <NotFoundPage />;
}

function AppShell() {
  return (
    <div className="app-shell">
      <SkipLink />
      <Header />
      <main id="main-content" className="app-shell__main" tabIndex={-1}>
        <RouteView />
      </main>
      <Footer />
    </div>
  );
}

export default function App() {
  return (
    <LocaleProvider>
      <RouterProvider>
        <AppShell />
      </RouterProvider>
    </LocaleProvider>
  );
}
