import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { axe } from "jest-axe";
import { render, screen, waitFor } from "@testing-library/react";
import { App } from "../App";
import { PublicContentReviewScreen } from "../components/screens/PublicContentReviewScreen";
import { PublicAppointmentRequestsScreen } from "../components/screens/PublicAppointmentRequestsScreen";
import { PublicQuotationRequestsScreen } from "../components/screens/PublicQuotationRequestsScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as publicContentApi from "../api/publicContentApi";
import * as publicRequestsApi from "../api/publicRequestsApi";

/**
 * Automated accessibility regression check (axe-core via jest-axe), wired into the normal
 * `npm run test`/`npm run quality` chain rather than a separate opt-in script — the reference
 * implementation is public-website/src/test/accessibility.test.tsx; this is the employee-portal
 * retrofit closing the remaining scope of TD-UX-002.
 */
function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

describe("accessibility", () => {
  it("the administration shell (default Tenants screen) has no detectable axe violations", async () => {
    const { container } = render(<App />);
    expect(
      await screen.findByRole("heading", { name: "Platform Tenant List" }),
    ).toBeInTheDocument();
    expect(await axe(container)).toHaveNoViolations();
  });

  it("PublicContentReviewScreen has no detectable axe violations", async () => {
    vi.spyOn(publicContentApi, "listPublishedTests").mockResolvedValue([]);
    const { container } = render(
      <Harness>
        <PublicContentReviewScreen />
      </Harness>,
    );
    await waitFor(() =>
      expect(screen.getByText("Revisión de Contenido Público")).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it("PublicAppointmentRequestsScreen has no detectable axe violations", async () => {
    vi.spyOn(publicRequestsApi, "listAppointments").mockResolvedValue([]);
    const { container } = render(
      <Harness>
        <PublicAppointmentRequestsScreen />
      </Harness>,
    );
    await waitFor(() =>
      expect(screen.getByText("Solicitudes Públicas de Cita")).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it("PublicQuotationRequestsScreen has no detectable axe violations", async () => {
    vi.spyOn(publicRequestsApi, "listQuotations").mockResolvedValue([]);
    const { container } = render(
      <Harness>
        <PublicQuotationRequestsScreen />
      </Harness>,
    );
    await waitFor(() =>
      expect(screen.getByText("Solicitudes Públicas de Cotización")).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });
});
