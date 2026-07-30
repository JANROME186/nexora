import { render, screen, waitFor } from "@testing-library/react";
import { axe } from "jest-axe";
import { describe, expect, it, vi } from "vitest";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AppointmentRequestPage } from "../pages/AppointmentRequestPage";
import { HomePage } from "../pages/HomePage";
import { MarketplacePage } from "../pages/MarketplacePage";
import { PrivacyPage } from "../pages/PrivacyPage";
import { ServicesPage } from "../pages/ServicesPage";
import { RouterProvider } from "../router/Router";

const mockFetch = vi.fn();
window.fetch = mockFetch;

/**
 * Automated accessibility regression check (axe-core via jest-axe), wired into the normal
 * `npm run test`/`npm run quality` chain rather than a separate opt-in script, so a future
 * screen cannot silently ship with a WCAG violation. This is the reference implementation for
 * TD-UX-002's "automated accessibility check wired into npm run quality" acceptance criterion.
 */
function renderPage(ui: React.ReactElement) {
  return render(
    <LocaleProvider>
      <RouterProvider>{ui}</RouterProvider>
    </LocaleProvider>,
  );
}

describe("accessibility", () => {
  it("HomePage has no detectable axe violations", async () => {
    const { container } = renderPage(<HomePage />);
    expect(await axe(container)).toHaveNoViolations();
  });

  it("ServicesPage has no detectable axe violations once loaded", async () => {
    mockFetch.mockResolvedValue({ ok: true, status: 200, json: async () => [] });
    const { container } = renderPage(<ServicesPage />);
    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it("MarketplacePage has no detectable axe violations once loaded", async () => {
    mockFetch.mockResolvedValue({ ok: true, status: 200, json: async () => [] });
    const { container } = renderPage(<MarketplacePage />);
    await waitFor(() =>
      expect(screen.getByText(/No hay información disponible/)).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it("AppointmentRequestPage has no detectable axe violations", async () => {
    mockFetch.mockResolvedValue({ ok: true, status: 200, json: async () => [] });
    const { container } = renderPage(<AppointmentRequestPage />);
    await waitFor(() =>
      expect(screen.getByRole("button", { name: "Enviar solicitud" })).toBeInTheDocument(),
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it("PrivacyPage has no detectable axe violations", async () => {
    const { container } = renderPage(<PrivacyPage />);
    expect(await axe(container)).toHaveNoViolations();
  });
});
