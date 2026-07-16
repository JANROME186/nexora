import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { TenantsScreen } from "../components/screens/TenantsScreen";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as api from "../api/platformFoundationApi";

describe("TenantsScreen", () => {
  it("creates a tenant and lists it after a successful submission", async () => {
    vi.spyOn(api, "createTenant").mockResolvedValue({
      tenantId: "tenant-123",
      name: "Nexora Diagnostics",
      status: "active",
    });

    const user = userEvent.setup();
    render(
      <AdminScopeProvider>
        <TenantsScreen />
      </AdminScopeProvider>,
    );

    await user.type(screen.getByLabelText("Tenant name"), "Nexora Diagnostics");
    await user.click(screen.getByRole("button", { name: "Create tenant" }));

    expect(await screen.findByText("Tenant created.")).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "tenant-123" })).toBeInTheDocument();
  });

  it("shows the backend error message when tenant creation fails", async () => {
    vi.spyOn(api, "createTenant").mockRejectedValue(new Error("Organization command is invalid."));

    const user = userEvent.setup();
    render(
      <AdminScopeProvider>
        <TenantsScreen />
      </AdminScopeProvider>,
    );

    await user.type(screen.getByLabelText("Tenant name"), "X");
    await user.click(screen.getByRole("button", { name: "Create tenant" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Unexpected error. Please try again.",
    );
  });
});
