import { describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { App } from "../App";

describe("Employee portal app smoke", () => {
  it("renders the administration shell and navigates across Platform Foundation screens", async () => {
    const user = userEvent.setup();
    render(<App />);

    expect(
      screen.getByRole("heading", {
        name: "Healthcare Operations Platform - Employee Portal Administration",
      }),
    ).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Platform Tenant List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Laboratories" }));
    expect(screen.getByRole("heading", { name: "Laboratory List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Branches" }));
    expect(screen.getByRole("heading", { name: "Branch List" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Users" }));
    expect(screen.getByRole("heading", { name: "User Management" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Role Assignments" }));
    expect(screen.getByRole("heading", { name: "Role Assignment" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Audit Events" }));
    expect(screen.getByRole("heading", { name: "Audit Search" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Diagnostic Catalog" }));
    expect(screen.getByRole("heading", { name: "Diagnostic Catalog" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "People Search" }));
    expect(
      screen.getByRole("heading", { name: "People Search and Duplicate Resolution" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Patients" }));
    expect(screen.getByRole("heading", { name: "Patients" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Doctors" }));
    expect(screen.getByRole("heading", { name: "Doctors" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Patient Registrations" }));
    expect(screen.getByRole("heading", { name: "Patient Registrations" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Front Desk" }));
    expect(screen.getByRole("heading", { name: "Front Desk Worklist" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Diagnostic Orders" }));
    expect(screen.getByRole("heading", { name: "Diagnostic Orders" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Cash Sessions" }));
    expect(screen.getByRole("heading", { name: "Cash Sessions" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Sales" }));
    expect(screen.getByRole("heading", { name: "Sales" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Billing Requests" }));
    expect(screen.getByRole("heading", { name: "Billing Requests" })).toBeInTheDocument();
  });

  it("navigates to Results and Digital Delivery screens (MVP-MOD-007)", async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole("button", { name: "Result Search" }));
    expect(screen.getByRole("heading", { name: "Result Search and Worklist" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Result Reports" }));
    expect(screen.getByRole("heading", { name: "Result Report History" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Critical Escalations" }));
    expect(
      screen.getByRole("heading", { name: "Critical Result Escalation Worklist" }),
    ).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Result Notifications" }));
    expect(
      screen.getByRole("heading", { name: "Result Notification History" }),
    ).toBeInTheDocument();
  });
});
