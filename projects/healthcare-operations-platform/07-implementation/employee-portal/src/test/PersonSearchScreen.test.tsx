import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PersonSearchScreen } from "../components/screens/PersonSearchScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/peopleApi";

function ScopedPersonSearchHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId, setLaboratoryId } = useAdminScope();
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) {
      return;
    }
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
  }, [setLaboratoryId, setTenantId]);

  return null;
}

describe("PersonSearchScreen", () => {
  it("searches people, detects duplicates, rebuilds the index and loads merge coordination", async () => {
    vi.spyOn(api, "searchPersons").mockResolvedValue([
      {
        personKind: "patient",
        tenantId: "tenant-1",
        laboratoryId: "lab-1",
        sourceAggregateId: "patient-1",
        personCode: "P-001",
        fullName: "Ada Lovelace",
        normalizedFamilyName: "lovelace",
        normalizedGivenName: "ada",
        birthDate: "1990-01-01",
        primaryDocumentType: "national_id",
        primaryDocumentNumberMasked: "****1234",
        status: "active",
      },
    ]);
    vi.spyOn(api, "detectPersonDuplicates").mockResolvedValue([
      {
        personKind: "patient",
        sourceAggregateId: "patient-1",
        fullName: "Ada Lovelace",
        confidence: 0.91,
        matchReason: "Primary document and natural key match",
      },
      {
        personKind: "doctor",
        sourceAggregateId: "doctor-1",
        fullName: "Ada Byron",
        confidence: 0.62,
        matchReason: "Name similarity",
      },
      {
        personKind: "patient",
        sourceAggregateId: "patient-2",
        fullName: "A. Lovelace",
        confidence: 0.34,
        matchReason: "Family name only",
      },
    ]);
    vi.spyOn(api, "rebuildPersonSearchIndex").mockResolvedValue({
      tenantId: "tenant-1",
      patientCount: 8,
      doctorCount: 5,
      rebuiltAt: "2026-07-10T00:00:00Z",
    });
    vi.spyOn(api, "initiatePersonMergeCoordination").mockResolvedValue({
      coordinationId: "merge-1",
      tenantId: "tenant-1",
      sourceKind: "patient",
      sourceRecordId: "patient-2",
      targetKind: "patient",
      targetRecordId: "patient-1",
      status: "APPLIED",
      patientMergeApplied: true,
    });
    vi.spyOn(api, "getPersonMergeCoordination").mockResolvedValue({
      coordinationId: "merge-1",
      tenantId: "tenant-1",
      sourceKind: "patient",
      sourceRecordId: "patient-2",
      targetKind: "patient",
      targetRecordId: "patient-1",
      status: "APPLIED",
      patientMergeApplied: true,
    });

    const user = userEvent.setup();
    render(
      <ScopedPersonSearchHarness>
        <PersonSearchScreen />
      </ScopedPersonSearchHarness>,
    );

    await user.selectOptions(screen.getByLabelText("Person kind"), "patient");
    await user.type(screen.getAllByLabelText("Family name")[0], "Lovelace");
    await user.type(screen.getAllByLabelText("Given name")[0], "Ada");
    await user.click(screen.getByRole("button", { name: "Search" }));

    expect(api.searchPersons).toHaveBeenCalledWith("tenant-1", {
      personKind: "patient",
      familyName: "Lovelace",
      givenName: "Ada",
    });
    expect(await screen.findByText("Search completed.")).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "P-001" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Rebuild search index for this tenant" }));
    expect(await screen.findByText("Index rebuilt: 8 patients, 5 doctors.")).toBeInTheDocument();

    await user.type(screen.getAllByLabelText("Family name")[1], "Lovelace");
    await user.type(screen.getAllByLabelText("Given name")[1], "Ada");
    await user.type(screen.getByLabelText("Birth date"), "1990-01-01");
    await user.click(screen.getByRole("button", { name: "Detect duplicates" }));

    expect(api.detectPersonDuplicates).toHaveBeenCalledWith({
      tenantId: "tenant-1",
      familyName: "Lovelace",
      givenName: "Ada",
      birthDate: "1990-01-01",
    });
    expect(await screen.findByText("Duplicate detection completed.")).toBeInTheDocument();
    const candidatesTable = screen.getByRole("table", { name: "Duplicate candidates" });
    expect(within(candidatesTable).getByText("91%")).toHaveClass("confidence-badge--high");
    expect(within(candidatesTable).getByText("62%")).toHaveClass("confidence-badge--medium");
    expect(within(candidatesTable).getByText("34%")).toHaveClass("confidence-badge--low");

    await user.type(screen.getByLabelText("Source record id (duplicate)"), "patient-2");
    await user.type(screen.getByLabelText("Target record id (survivor)"), "patient-1");
    await user.click(screen.getByRole("button", { name: "Initiate merge coordination" }));
    expect(await screen.findByText("Merge coordination initiated.")).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "merge-1" })).toBeInTheDocument();
    expect(screen.getByRole("cell", { name: "Yes" })).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Look up coordination" }));
    expect(api.getPersonMergeCoordination).toHaveBeenCalledWith("merge-1");
    expect(await screen.findByText("Coordination loaded.")).toBeInTheDocument();
  });

  it("shows tenant-required and empty states", async () => {
    vi.spyOn(api, "searchPersons").mockResolvedValue([]);
    vi.spyOn(api, "detectPersonDuplicates").mockResolvedValue([]);

    const user = userEvent.setup();
    render(
      <AdminScopeProvider>
        <PersonSearchScreen />
      </AdminScopeProvider>,
    );

    expect(screen.getByText("Select a tenant before searching people.")).toBeInTheDocument();

    render(
      <ScopedPersonSearchHarness>
        <PersonSearchScreen />
      </ScopedPersonSearchHarness>,
    );

    await user.click(screen.getAllByRole("button", { name: "Search" })[1]);
    expect(
      await screen.findByText("No patients or doctors matched this search."),
    ).toBeInTheDocument();

    await user.click(screen.getAllByRole("button", { name: "Detect duplicates" })[1]);
    expect(await screen.findByText("No duplicate candidates were found.")).toBeInTheDocument();
  });
});
