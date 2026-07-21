import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../test/renderWithProviders";
import { NotFoundPage } from "./NotFoundPage";

describe("NotFoundPage", () => {
  it("renders a link back home", () => {
    renderWithProviders(<NotFoundPage />);
    expect(screen.getByRole("heading", { level: 1 })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Volver al inicio/ })).toHaveAttribute("href", "/");
  });
});
