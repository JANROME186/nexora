import type { PatientMobileApi } from "../api/patientMobileApi";
import type {
  DeliveryTicketResponse,
  PatientAppointmentResponse,
  PatientNotificationResponse,
  PatientOrderResponse,
  PatientProfileResponse,
} from "../api/types";
import { permissionsForRoles, type PermissionCode } from "../auth/permissions";
import type { MobileSession } from "../auth/sessionStore";
import { DEFAULT_LOCALE, getMessages, type Locale } from "../i18n/locale";
import type { MobileRoute } from "../navigation/routes";

type LoadStatus = "idle" | "loading" | "ready" | "empty" | "forbidden" | "error";

export type PatientMobileWorkflowState = {
  status: LoadStatus;
  title: string;
  emptyMessage: string;
  error: string | null;
  visibleRoutes: MobileRoute[];
  profile: PatientProfileResponse | null;
  appointments: PatientAppointmentResponse[];
  orders: PatientOrderResponse[];
  results: DeliveryTicketResponse[];
  notifications: PatientNotificationResponse[];
};

export type PatientMobileWorkflowModel = {
  getState: () => PatientMobileWorkflowState;
  load: () => Promise<void>;
  canAccess: (route: MobileRoute) => boolean;
};

const PATIENT_ROUTE_PERMISSION: Partial<Record<MobileRoute, PermissionCode>> = {
  "patient-profile": "PORTAL_PATIENT_PROFILE_VIEW",
  "patient-appointments": "PORTAL_PATIENT_APPOINTMENTS_VIEW",
  "patient-orders": "PORTAL_PATIENT_ORDERS_VIEW",
  "patient-results": "PORTAL_PATIENT_RESULTS_VIEW",
  "patient-notifications": "PORTAL_PATIENT_NOTIFICATIONS_VIEW",
};

const PATIENT_ROUTES = Object.keys(PATIENT_ROUTE_PERMISSION) as MobileRoute[];

export function createPatientMobileWorkflowModel(
  api: PatientMobileApi,
  session: MobileSession,
  locale: Locale = DEFAULT_LOCALE,
  onStateChange?: () => void,
): PatientMobileWorkflowModel {
  const messages = getMessages(locale);
  const permissions = permissionsForRoles(session.roleCodes);
  const visibleRoutes = PATIENT_ROUTES.filter((route) => {
    const requiredPermission = PATIENT_ROUTE_PERMISSION[route];
    return requiredPermission !== undefined && permissions.has(requiredPermission);
  });

  let state: PatientMobileWorkflowState = {
    status: "idle",
    title: messages.patientMobileTitle,
    emptyMessage: messages.patientMobileEmpty,
    error: null,
    visibleRoutes,
    profile: null,
    appointments: [],
    orders: [],
    results: [],
    notifications: [],
  };

  function setState(newState: Partial<PatientMobileWorkflowState>) {
    state = { ...state, ...newState };
    onStateChange?.();
  }

  function canAccess(route: MobileRoute) {
    return visibleRoutes.includes(route);
  }

  async function load() {
    if (visibleRoutes.length === 0) {
      setState({ status: "forbidden", error: messages.patientMobileForbidden });
      return;
    }

    setState({ status: "loading", error: null });
    try {
      const [profile, appointments, orders, results, notifications] = await Promise.all([
        canAccess("patient-profile") ? api.getProfile(session.userId) : Promise.resolve(null),
        canAccess("patient-appointments")
          ? api.listAppointments(session.userId)
          : Promise.resolve([]),
        canAccess("patient-orders") ? api.listOrders(session.userId) : Promise.resolve([]),
        canAccess("patient-results") ? api.listResults(session.userId) : Promise.resolve([]),
        canAccess("patient-notifications")
          ? api.listNotifications(session.userId)
          : Promise.resolve([]),
      ]);
      const hasAnyContent =
        profile !== null ||
        appointments.length > 0 ||
        orders.length > 0 ||
        results.length > 0 ||
        notifications.length > 0;
      setState({
        status: hasAnyContent ? "ready" : "empty",
        profile,
        appointments,
        orders,
        results,
        notifications,
      });
    } catch (err) {
      setState({
        status: "error",
        error: err instanceof Error ? err.message : String(err),
      });
    }
  }

  return {
    getState: () => state,
    load,
    canAccess,
  };
}
