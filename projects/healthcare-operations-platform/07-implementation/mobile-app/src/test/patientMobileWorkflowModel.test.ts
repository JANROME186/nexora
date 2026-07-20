import { describe, expect, it } from "vitest";

import type { PatientMobileApi } from "../api/patientMobileApi";
import { createPatientMobileWorkflowModel } from "../screens/patientMobileWorkflowModel";
import type { MobileSession } from "../auth/sessionStore";

const patientSession: MobileSession = {
  token: "token",
  tenantId: "tenant-1",
  userId: "patient-1",
  displayName: "Patient One",
  email: "patient.one@example.test",
  roleCodes: ["PATIENT"],
  createdAt: "2026-07-19T00:00:00.000Z",
};

function createApi(overrides: Partial<PatientMobileApi> = {}): PatientMobileApi {
  return {
    getProfile: async () => ({
      patientId: "patient-1",
      displayName: "Patient One",
      email: "patient.one@example.test",
      preferredLanguage: "es-MX",
    }),
    listAppointments: async () => [
      {
        appointmentId: "appointment-1",
        scheduledAt: "2026-07-20T09:00:00.000Z",
        serviceName: "Biometría hemática",
        branchName: "Centro",
        status: "scheduled",
      },
    ],
    listOrders: async () => [
      {
        orderId: "order-1",
        orderNumber: "ORD-1",
        requestedAt: "2026-07-19T09:00:00.000Z",
        status: "accepted",
        serviceNames: ["Biometría hemática"],
      },
    ],
    listResults: async () => [
      {
        deliveryTicketId: "ticket-1",
        patientId: "patient-1",
        orderId: "order-1",
        status: "delivered",
        releasedAt: "2026-07-19T12:00:00.000Z",
      },
    ],
    listNotifications: async () => [
      {
        notificationId: "notification-1",
        category: "result",
        title: "Resultado liberado",
        body: "Tu resultado está disponible.",
        createdAt: "2026-07-19T12:00:00.000Z",
      },
    ],
    ...overrides,
  };
}

describe("patientMobileWorkflowModel", () => {
  it("loads the complete patient mobile workflow", async () => {
    let changes = 0;
    const model = createPatientMobileWorkflowModel(createApi(), patientSession, "en-US", () => {
      changes++;
    });

    expect(model.getState().title).toBe("Patient mobile portal");
    expect(model.canAccess("patient-results")).toBe(true);

    await model.load();

    expect(changes).toBe(2);
    expect(model.getState().status).toBe("ready");
    expect(model.getState().profile?.patientId).toBe("patient-1");
    expect(model.getState().appointments).toHaveLength(1);
    expect(model.getState().orders).toHaveLength(1);
    expect(model.getState().results).toHaveLength(1);
    expect(model.getState().notifications).toHaveLength(1);
  });

  it("marks the workflow empty when every allowed section is empty", async () => {
    const model = createPatientMobileWorkflowModel(
      createApi({
        getProfile: async () => null as never,
        listAppointments: async () => [],
        listOrders: async () => [],
        listResults: async () => [],
        listNotifications: async () => [],
      }),
      patientSession,
    );

    await model.load();

    expect(model.getState().status).toBe("empty");
    expect(model.getState().emptyMessage).toBe("No hay información disponible para tu perfil.");
  });

  it("blocks sessions without patient portal permissions", async () => {
    const model = createPatientMobileWorkflowModel(createApi(), {
      ...patientSession,
      roleCodes: ["FRONT_DESK"],
    });

    await model.load();

    expect(model.getState().status).toBe("forbidden");
    expect(model.getState().error).toContain("no tiene permisos");
  });

  it("records API errors without throwing", async () => {
    const model = createPatientMobileWorkflowModel(
      createApi({
        listResults: async () => {
          throw new Error("backend unavailable");
        },
      }),
      patientSession,
    );

    await model.load();

    expect(model.getState().status).toBe("error");
    expect(model.getState().error).toBe("backend unavailable");
  });
});
