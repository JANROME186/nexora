import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import { LocaleProvider, useLocale } from "./LocaleContext";

function Probe() {
  const { locale, setLocale, t } = useLocale();
  return (
    <div>
      <span data-testid="locale">{locale}</span>
      <span data-testid="label">{t.nav.home}</span>
      <button onClick={() => setLocale("en-US")}>to-en</button>
    </div>
  );
}

describe("LocaleContext", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("throws when useLocale is used outside a LocaleProvider", () => {
    const Broken = () => {
      useLocale();
      return null;
    };
    expect(() => render(<Broken />)).toThrow(/LocaleProvider/);
  });

  it("defaults to es-MX", () => {
    render(
      <LocaleProvider>
        <Probe />
      </LocaleProvider>,
    );
    expect(screen.getByTestId("locale")).toHaveTextContent("es-MX");
    expect(screen.getByTestId("label")).toHaveTextContent("Inicio");
  });

  it("switches locale and persists the choice", async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <Probe />
      </LocaleProvider>,
    );

    await user.click(screen.getByText("to-en"));
    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
    expect(screen.getByTestId("label")).toHaveTextContent("Home");
    expect(localStorage.getItem("hop.public-website.locale")).toBe("en-US");
  });

  it("falls back to en-US when a stored value is invalid", () => {
    localStorage.setItem("hop.public-website.locale", "fr-FR");
    render(
      <LocaleProvider>
        <Probe />
      </LocaleProvider>,
    );
    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
  });

  it("reuses a valid stored locale", () => {
    localStorage.setItem("hop.public-website.locale", "en-US");
    render(
      <LocaleProvider>
        <Probe />
      </LocaleProvider>,
    );
    expect(screen.getByTestId("locale")).toHaveTextContent("en-US");
  });
});
