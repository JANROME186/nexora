/**
 * es-MX message catalog (default locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `default_locale: es-MX`, `fallback_locale: en-US`).
 *
 * Doctor-portal domain catalog (COM-MOD-009-PORTAL-002). Replaces a stale copy of the
 * employee-portal admin-screen catalog that had been left in this scaffold.
 *
 * This file is the source of truth for `MessageCatalog` — `en-US.ts` is type-checked against it
 * so the two locales can never drift out of key parity.
 */
export const esMX = {
  unexpectedError: "Error inesperado. Inténtalo de nuevo.",
  selectPatientFirst: "Selecciona un paciente primero.",
  sessionExpiredRetry: "Tu sesión ha expirado. Inicia sesión de nuevo para continuar.",
  appShell: {
    title: "HOP Portal Médico",
    subtitle:
      "Acceso para médicos referentes: consulta a tus pacientes referidos, resultados liberados autorizados, órdenes diagnósticas y notificaciones.",
    navAriaLabel: "Pantallas del portal médico",
    languageSwitcherLabel: "Idioma",
    tabs: {
      patients: "Mis Pacientes",
      results: "Resultados",
      orders: "Mis Órdenes",
      notifications: "Notificaciones",
    },
    login: {
      title: "Iniciar Sesión",
      tenantId: "ID de Organización",
      username: "Nombre de Usuario",
      // eslint-disable-next-line sonarjs/no-hardcoded-passwords -- UI label text, not a credential
      passwordLabel: "Contraseña",
      submit: "Ingresar",
      loggingIn: "Ingresando...",
      errorInvalid: "Credenciales incorrectas.",
      errorLocked: "Cuenta bloqueada temporalmente por intentos fallidos.",
      errorSuspended: "Cuenta suspendida. Contacta a soporte.",
    },
    states: {
      loading: "Cargando información...",
      empty: "No se encontraron registros.",
      error: "Ocurrió un error al cargar la información.",
      noPermission: "No tienes permiso para acceder a esta sección.",
      sessionExpired: "Tu sesión ha expirado. Por favor inicia sesión de nuevo.",
      logout: "Cerrar Sesión",
      welcome: "Bienvenido/a",
    },
    patients: {
      name: "Nombre",
      document: "Documento",
      birthDate: "Fecha de Nacimiento",
      referredOrders: "Órdenes Referidas",
      viewResults: "Ver Resultados",
      emptyHint: "Aún no tienes pacientes referidos en esta organización.",
    },
    results: {
      selectPatient: "Selecciona un paciente",
      selectPatientPlaceholder: "-- Selecciona un paciente referido --",
      abnormal: "Anormal",
      normal: "Normal",
      analyte: "Analito",
      value: "Resultado",
      range: "Rango de Referencia",
      releasedAt: "Liberado el",
    },
    orders: {
      orderId: "ID de Orden",
      patient: "Paciente",
      branch: "Sucursal",
      status: "Estado",
      createdAt: "Fecha de Creación",
    },
    notifications: {
      resultId: "ID de Resultado",
      channel: "Canal",
      recipient: "Destinatario",
      status: "Estado",
      dispatchedAt: "Enviado el",
      failureReason: "Motivo de Falla",
    },
  },
} as const;

/** Recursively widens the `esMX` literal string types to `string` so other locales (en-US) can
 * hold different text while TypeScript still enforces identical key structure. */
type Widen<T> = T extends string ? string : { [K in keyof T]: Widen<T[K]> };

export type MessageCatalog = Widen<typeof esMX>;
