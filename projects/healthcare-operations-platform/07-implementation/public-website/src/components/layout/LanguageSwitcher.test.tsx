import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import { renderWithProviders } from "../../test/renderWithProviders";
import { LanguageSwitcher } from "./LanguageSwitcher";

describe("LanguageSwitcher", () => {
  it("marks the active locale as pressed and switches on click", async () => {
    const user = userEvent.setup();
    renderWithProviders(<LanguageSwitcher />);

    expect(screen.getByRole("button", { name: "ES" })).toHaveAttribute("aria-pressed", "true");
    await user.click(screen.getByRole("button", { name: "EN" }));
    expect(screen.getByRole("button", { name: "EN" })).toHaveAttribute("aria-pressed", "true");
    expect(screen.getByRole("button", { name: "ES" })).toHaveAttribute("aria-pressed", "false");
  });
});
