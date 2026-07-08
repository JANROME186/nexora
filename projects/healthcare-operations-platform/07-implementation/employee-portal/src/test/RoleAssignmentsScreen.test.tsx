import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { RoleAssignmentsScreen } from "../components/screens/RoleAssignmentsScreen";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as api from "../api/platformFoundationApi";

describe("RoleAssignmentsScreen", () => {
  it("requires confirmation before assigning a role", async () => {
    const assignRoleMock = vi.spyOn(api, "assignRole").mockResolvedValue(undefined);

    const user = userEvent.setup();
    render(
      <AdminScopeProvider>
        <RoleAssignmentsScreen />
      </AdminScopeProvider>
    );

    await user.type(screen.getByLabelText("User id"), "user-1");
    await user.type(screen.getByLabelText("Role code"), "tenant-admin");
    await user.type(screen.getByLabelText("Scope id"), "tenant-1");
    await user.click(screen.getByRole("button", { name: "Assign role" }));

    expect(screen.getByRole("dialog", { name: "Confirm role assignment" })).toBeInTheDocument();
    expect(assignRoleMock).not.toHaveBeenCalled();
  });

  it("calls the role assignment endpoint only after the dialog is confirmed", async () => {
    const assignRoleMock = vi.spyOn(api, "assignRole").mockResolvedValue(undefined);

    const user = userEvent.setup();
    render(
      <AdminScopeProvider>
        <RoleAssignmentsScreen />
      </AdminScopeProvider>
    );

    await user.type(screen.getByLabelText("User id"), "user-1");
    await user.type(screen.getByLabelText("Role code"), "tenant-admin");
    await user.type(screen.getByLabelText("Scope id"), "tenant-1");
    await user.click(screen.getByRole("button", { name: "Assign role" }));

    const dialog = screen.getByRole("dialog", { name: "Confirm role assignment" });
    const confirmButton = dialog.querySelector("button:last-of-type") as HTMLButtonElement;
    await user.click(confirmButton);

    expect(assignRoleMock).toHaveBeenCalledWith("user-1", {
      roleCode: "tenant-admin",
      scope: { type: "tenant", id: "tenant-1" }
    });
    expect(await screen.findByText("Role assigned.")).toBeInTheDocument();
  });
});
