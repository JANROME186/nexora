import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../../test/renderWithProviders";
import { Footer } from "./Footer";

describe("Footer", () => {
  it("renders a privacy link and the current year", () => {
    renderWithProviders(<Footer />);
    expect(screen.getByRole("link", { name: "Aviso de privacidad" })).toHaveAttribute(
      "href",
      "/privacy",
    );
    const year = String(new Date().getFullYear());
    expect(screen.getByText((content) => content.includes(year))).toBeInTheDocument();
  });
});
