import { describe, it, expect, vi, beforeEach } from "vitest";
import { getResultNotifications } from "./resultNotificationsApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("getResultNotifications", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    localStorage.clear();
  });

  it("requests notifications for a result scoped to tenant", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [{ notificationRequestId: "notif-1" }],
    });

    const notifications = await getResultNotifications("res-1", "tenant-a");

    expect(notifications).toEqual([{ notificationRequestId: "notif-1" }]);
    const [calledUrl] = mockFetch.mock.calls[0];
    expect(calledUrl).toBe(
      "/api/clinical-operations/laboratory-results/res-1/notifications?tenantId=tenant-a",
    );
  });
});
