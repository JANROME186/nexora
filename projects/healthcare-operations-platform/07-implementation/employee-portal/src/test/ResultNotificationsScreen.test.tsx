import { useEffect, useRef, type ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ResultNotificationsScreen } from "../components/screens/ResultNotificationsScreen";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/resultsDeliveryApi";
import type { ResultNotificationRequest } from "../api/types";

function ScopedHarness({ children }: { children: ReactNode }) {
  return (
    <AdminScopeProvider>
      <ScopeSetter />
      {children}
    </AdminScopeProvider>
  );
}

function ScopeSetter() {
  const { setTenantId, setLaboratoryId, setBranchId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
    setLaboratoryId("lab-1");
    setBranchId("branch-1");
  }, [setBranchId, setLaboratoryId, setTenantId]);
  return null;
}

const mockNotification: ResultNotificationRequest = {
  notificationRequestId: "notif-1",
  resultId: "res-1",
  tenantId: "tenant-1",
  recipientType: "patient",
  recipientId: "pat-1",
  channel: "sms",
  status: "delivered",
  dispatchedAt: "2026-07-17T10:01:00Z",
  deliveredAt: "2026-07-17T10:02:00Z",
};

describe("ResultNotificationsScreen", () => {
  it("renders notification history heading", () => {
    render(
      <ScopedHarness>
        <ResultNotificationsScreen />
      </ScopedHarness>,
    );
    expect(
      screen.getByRole("heading", { name: "Result Notification History" }),
    ).toBeInTheDocument();
  });

  it("loads notifications for a result", async () => {
    vi.spyOn(api, "listResultNotifications").mockResolvedValue([mockNotification]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultNotificationsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Load Notifications" }));
    expect(await screen.findByText("Notifications loaded.")).toBeInTheDocument();
    expect(screen.getByText("notif-1")).toBeInTheDocument();
    expect(screen.getByText("patient")).toBeInTheDocument();
    expect(screen.getByText("sms")).toBeInTheDocument();
  });

  it("shows empty state when no notifications", async () => {
    vi.spyOn(api, "listResultNotifications").mockResolvedValue([]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultNotificationsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Load Notifications" }));
    expect(
      await screen.findByText(/No se encontraron registros de notificación/),
    ).toBeInTheDocument();
  });

  it("shows dispatch status badge", async () => {
    vi.spyOn(api, "listResultNotifications").mockResolvedValue([mockNotification]);
    const user = userEvent.setup();
    render(
      <ScopedHarness>
        <ResultNotificationsScreen />
      </ScopedHarness>,
    );
    await user.type(screen.getByLabelText("Result ID"), "res-1");
    await user.click(screen.getByRole("button", { name: "Load Notifications" }));
    expect(await screen.findByText("delivered")).toBeInTheDocument();
  });
});
