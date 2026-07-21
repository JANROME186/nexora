import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../../test/renderWithProviders";
import { Header } from "./Header";

describe("Header", () => {
  it("marks the current route with aria-current", () => {
    renderWithProviders(<Header />, "/services");
    expect(screen.getByRole("link", { name: "Servicios" })).toHaveAttribute("aria-current", "page");
    expect(screen.getByRole("link", { name: "Pruebas" })).not.toHaveAttribute("aria-current");
  });

  it("toggles the mobile navigation menu", async () => {
    const user = userEvent.setup();
    renderWithProviders(<Header />);
    const toggle = screen.getByRole("button", { expanded: false });
    await user.click(toggle);
    expect(screen.getByRole("button", { expanded: true })).toBeInTheDocument();
  });

  it("closes the menu after choosing a nav link", async () => {
    const user = userEvent.setup();
    renderWithProviders(<Header />);
    await user.click(screen.getByRole("button", { expanded: false }));
    await user.click(screen.getByRole("link", { name: "Servicios" }));
    expect(screen.getByRole("button", { expanded: false })).toBeInTheDocument();
  });
});
