import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("App", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    mockFetch.mockResolvedValue({ ok: true, status: 200, json: async () => [] });
    window.history.pushState({}, "", "/");
  });

  it("renders the home page by default with skip link and landmarks", () => {
    render(<App />);
    expect(screen.getByText("Saltar al contenido principal")).toBeInTheDocument();
    expect(screen.getByRole("banner")).toBeInTheDocument();
    expect(screen.getByRole("main")).toBeInTheDocument();
    expect(screen.getByRole("contentinfo")).toBeInTheDocument();
  });

  it("navigates to the services page via the header nav", async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getByRole("link", { name: "Servicios" }));
    await waitFor(() =>
      expect(
        screen.getByRole("heading", { level: 1, name: "Servicios diagnósticos" }),
      ).toBeInTheDocument(),
    );
  });

  it("renders the not-found page for an unknown route", () => {
    window.history.pushState({}, "", "/this-route-does-not-exist");
    render(<App />);
    expect(
      screen.getByRole("heading", { level: 1, name: "Página no encontrada" }),
    ).toBeInTheDocument();
  });

  it("renders a catalog detail page for a parameterized route", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({
        serviceId: "svc-1",
        code: "SVC_GLU",
        nameEn: "Fasting glucose",
        nameEs: "Glucosa en ayuno",
        serviceType: "LAB_TEST",
        version: 1,
      }),
    });
    window.history.pushState({}, "", "/services/svc-1");
    render(<App />);
    await waitFor(() => expect(screen.getByText("Glucosa en ayuno")).toBeInTheDocument());
  });
});
