import { beforeEach, describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { LocaleProvider, useLocale } from "../i18n/LocaleContext";

function LocaleProbe() {
  const { locale, setLocale, t } = useLocale();
  return (
    <div>
      <span data-testid="locale">{locale}</span>
      <span data-testid="title">{t.appShell.title}</span>
      <button type="button" onClick={() => setLocale("en-US")}>
        Switch to English
      </button>
      <button type="button" onClick={() => setLocale("es-MX")}>
        Switch to Spanish
      </button>
    </div>
  );
}

describe("LocaleContext", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("defaults to es-MX and changes rendered catalog text when the locale switches", async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <LocaleProbe />
      </LocaleProvider>,
    );

    expect(screen.getByTestId("locale")).toHaveTextContent("es-MX");
    expect(screen.getByTestId("title")).toHaveTextContent(
      "Plataforma de Operaciones de Salud - Administración del Portal de Empleados",
    );

    await user.click(screen.getByRole("button", { name: "Switch to English" }));

    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
    expect(screen.getByTestId("title")).toHaveTextContent(
      "Healthcare Operations Platform - Employee Portal Administration",
    );
  });

  it("persists the selected locale in localStorage and restores it on a fresh render", async () => {
    const user = userEvent.setup();
    const { unmount } = render(
      <LocaleProvider>
        <LocaleProbe />
      </LocaleProvider>,
    );

    await user.click(screen.getByRole("button", { name: "Switch to English" }));
    expect(window.localStorage.getItem("hop.locale")).toBe("en-US");

    unmount();

    render(
      <LocaleProvider>
        <LocaleProbe />
      </LocaleProvider>,
    );

    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
  });

  it("falls back to en-US when localStorage holds a value that is not a supported locale", () => {
    window.localStorage.setItem("hop.locale", "fr-FR");

    render(
      <LocaleProvider>
        <LocaleProbe />
      </LocaleProvider>,
    );

    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
  });
});
