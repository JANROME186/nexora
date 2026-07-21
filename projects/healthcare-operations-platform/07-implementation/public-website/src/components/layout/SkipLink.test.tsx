import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../../test/renderWithProviders";
import { SkipLink } from "./SkipLink";

describe("SkipLink", () => {
  it("links to the main content landmark", () => {
    renderWithProviders(<SkipLink />);
    expect(screen.getByText("Saltar al contenido principal")).toHaveAttribute(
      "href",
      "#main-content",
    );
  });
});
