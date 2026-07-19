import { beforeEach, describe, expect, it } from "vitest";
import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AppShell } from "../components/layout/AppShell";
import { LocaleProvider } from "../i18n/LocaleContext";
import { readSessionHeaders, SessionProvider, useSession } from "../state/SessionContext";

function RoleSwitcher({ roleCodes }: { roleCodes: string[] }) {
  const { setRoleCodes } = useSession();
  return (
    <button type="button" onClick={() => setRoleCodes(roleCodes)}>
      switch-role
    </button>
  );
}

function Harness({ roleCodes }: { roleCodes: string[] }) {
  return (
    <LocaleProvider>
      <SessionProvider>
        <RoleSwitcher roleCodes={roleCodes} />
        <AppShell activeScreen="tenants" onSelectScreen={() => {}}>
          <div>content</div>
        </AppShell>
      </SessionProvider>
    </LocaleProvider>
  );
}

describe("SessionContext permission-filtered navigation", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it("shows all 30 tabs for the default ADMIN local-dev fixture session", () => {
    render(<Harness roleCodes={["ADMIN"]} />);

    const nav = screen.getByRole("navigation", { name: "Pantallas de administración" });
    expect(within(nav).getAllByRole("button")).toHaveLength(30);
  });

  it("renders only FRONT_DESK-permitted tabs and hides Tenants when the role changes", async () => {
    const user = userEvent.setup();
    render(<Harness roleCodes={["FRONT_DESK"]} />);

    await user.click(screen.getByRole("button", { name: "switch-role" }));

    const nav = screen.getByRole("navigation", { name: "Pantallas de administración" });
    expect(within(nav).queryByRole("button", { name: "Organizaciones" })).not.toBeInTheDocument();
    expect(within(nav).getByRole("button", { name: "Recepción" })).toBeInTheDocument();
    expect(within(nav).getByRole("button", { name: "Pacientes" })).toBeInTheDocument();
    expect(within(nav).getByRole("button", { name: "Doctores" })).toBeInTheDocument();
    expect(within(nav).getAllByRole("button")).toHaveLength(7);
  });

  it("persists session headers used by backend request authorization", async () => {
    const user = userEvent.setup();
    render(<Harness roleCodes={["CASHIER"]} />);

    await user.click(screen.getByRole("button", { name: "switch-role" }));

    expect(readSessionHeaders()).toMatchObject({
      "X-HOP-AUTH-TOKEN": "local-dev-token",
      "X-HOP-USER-ID": "local-dev-fixture-user",
      "X-HOP-ROLES": "CASHIER",
    });
  });
});
