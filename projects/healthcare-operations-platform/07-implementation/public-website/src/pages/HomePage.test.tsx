import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../test/renderWithProviders";
import { HomePage } from "./HomePage";

describe("HomePage", () => {
  it("renders the hero, catalog overview links and configured branch", () => {
    renderWithProviders(<HomePage />);

    expect(screen.getByRole("heading", { level: 1 })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Solicitar una cita/ })).toHaveAttribute(
      "href",
      "/appointment-request",
    );
    expect(screen.getByRole("link", { name: /Solicitar una cotización/ })).toHaveAttribute(
      "href",
      "/quotation-request",
    );
    expect(screen.getByText("Sucursal Principal del Laboratorio")).toBeInTheDocument();
    expect(document.title).toContain("Inicio");
  });
});
