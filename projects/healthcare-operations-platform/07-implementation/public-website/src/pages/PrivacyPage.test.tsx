import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../test/renderWithProviders";
import { PrivacyPage } from "./PrivacyPage";

describe("PrivacyPage", () => {
  it("renders the privacy notice sections", () => {
    renderWithProviders(<PrivacyPage />);
    expect(
      screen.getByRole("heading", { level: 1, name: "Aviso de privacidad" }),
    ).toBeInTheDocument();
    expect(screen.getByText(/Datos que recopilamos/)).toBeInTheDocument();
    expect(document.title).toContain("Aviso de privacidad");
  });
});
