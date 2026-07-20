import { render, screen, fireEvent } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import { AdminScopeProvider, useAdminScope } from "./AdminScopeContext";

function TestComponent() {
  const { scope, setTenantId, setLaboratoryId, setBranchId, setUserId } = useAdminScope();
  return (
    <div>
      <span data-testid="tenant">{scope.tenantId || "none"}</span>
      <span data-testid="lab">{scope.laboratoryId || "none"}</span>
      <span data-testid="branch">{scope.branchId || "none"}</span>
      <span data-testid="user">{scope.userId || "none"}</span>
      <button onClick={() => setTenantId("t-1")}>Set Tenant</button>
      <button onClick={() => setLaboratoryId("l-1")}>Set Lab</button>
      <button onClick={() => setBranchId("b-1")}>Set Branch</button>
      <button onClick={() => setUserId("u-1")}>Set User</button>
    </div>
  );
}

describe("AdminScopeContext", () => {
  it("manages scope state correctly", () => {
    render(
      <AdminScopeProvider>
        <TestComponent />
      </AdminScopeProvider>,
    );

    expect(screen.getByTestId("tenant").textContent).toBe("none");
    fireEvent.click(screen.getByText("Set Tenant"));
    expect(screen.getByTestId("tenant").textContent).toBe("t-1");

    fireEvent.click(screen.getByText("Set Lab"));
    expect(screen.getByTestId("lab").textContent).toBe("l-1");

    fireEvent.click(screen.getByText("Set Branch"));
    expect(screen.getByTestId("branch").textContent).toBe("b-1");

    fireEvent.click(screen.getByText("Set User"));
    expect(screen.getByTestId("user").textContent).toBe("u-1");
  });

  it("throws error when used outside provider", () => {
    const renderWithError = () => render(<TestComponent />);
    expect(renderWithError).toThrow("useAdminScope must be used within an AdminScopeProvider.");
  });
});
