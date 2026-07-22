import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PublicContentReviewScreen } from "../components/screens/PublicContentReviewScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import { ApiError } from "../api/httpClient";
import * as api from "../api/publicContentApi";
import type { PublicTestSnapshot } from "../api/types";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

function ScopedHarness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>
        <ScopeSetter />
        {children}
      </AdminScopeProvider>
    </LocaleProvider>
  );
}

function ScopeSetter() {
  const { setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setLaboratoryId("lab-1");
  }, [setLaboratoryId]);

  return null;
}

const publishedTest: PublicTestSnapshot = {
  testDefinitionId: "test-1",
  code: "T-001",
  nameEn: "Complete Blood Count",
  nameEs: "Biometría Hemática",
  methodology: "Flow cytometry",
  measurementUnit: null,
  resultType: "quantitative",
  turnaroundTimeHours: 24,
  version: 3,
};

describe("PublicContentReviewScreen", () => {
  it("requires a laboratory scope before loading", () => {
    render(
      <Harness>
        <PublicContentReviewScreen />
      </Harness>,
    );

    expect(screen.getByText("Selecciona un laboratorio antes de continuar.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Cargar" })).toBeDisabled();
  });

  it("loads and displays published tests for the selected area", async () => {
    vi.spyOn(api, "listPublishedTests").mockResolvedValue([publishedTest]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicContentReviewScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Pruebas" }));
    await user.click(screen.getByRole("button", { name: "Cargar" }));

    expect(await screen.findByText("Contenido publicado cargado.")).toBeInTheDocument();
    expect(screen.getByText("Complete Blood Count")).toBeInTheDocument();
    expect(api.listPublishedTests).toHaveBeenCalledWith("lab-1");
  });

  it("shows the empty state when no content is published for the area", async () => {
    vi.spyOn(api, "listPublishedDiagnosticServices").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicContentReviewScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar" }));

    expect(
      await screen.findByText("No hay contenido publicado para esta área."),
    ).toBeInTheDocument();
  });

  it("surfaces an error when the published-content request fails", async () => {
    vi.spyOn(api, "listPublishedPanels").mockRejectedValue(
      new ApiError(500, "Unexpected failure while loading published panels."),
    );

    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <PublicContentReviewScreen />
      </ScopedHarness>,
    );

    await user.click(screen.getByRole("button", { name: "Paneles" }));
    await user.click(screen.getByRole("button", { name: "Cargar" }));

    expect(
      await screen.findByText("Unexpected failure while loading published panels."),
    ).toBeInTheDocument();
  });
});
