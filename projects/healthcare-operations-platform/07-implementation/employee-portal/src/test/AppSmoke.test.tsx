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
  });
});
