/**
 * es-MX message catalog (default locale per the enterprise-product-foundation-standard
 * `localization_and_i18n` foundation: `default_locale: es-MX`, `fallback_locale: en-US`).
 *
 * The flat, top-level keys are the original HOP-QA-ALIGN-005 / MVP-MOD-007-FE-001 baseline
 * (previously the sole content of `src/i18n/messages.ts`, English-only) translated to Spanish.
 * The `appShell` keys are new: they demonstrate the locale-switching mechanism end-to-end by
 * driving the AppShell header and navigation tab labels.
 *
 * This file is the source of truth for `MessageCatalog` — `en-US.ts` is type-checked against it
 * so the two locales can never drift out of key parity.
 */
export const esMX = {
  selectDoctorFirst: "Selecciona un doctor primero.",
  selectPatientFirst: "Selecciona un paciente primero.",
  unexpectedError: "Error inesperado. Inténtalo de nuevo.",
  selectReceptionVisitFirst: "Selecciona una visita de recepción primero.",
  selectOrderFirst: "Selecciona una orden diagnóstica primero.",
  selectCashSessionFirst: "Selecciona una sesión de caja primero.",
  selectSaleFirst: "Selecciona una venta primero.",
  selectBillingRequestFirst: "Selecciona una solicitud de facturación primero.",
  // -- Results and Digital Delivery (MVP-MOD-007) --
  selectResultFirst: "Selecciona un resultado primero.",
  selectEscalationFirst: "Selecciona una escalación crítica primero.",
  noResultsPendingRelease: "No se encontraron resultados liberados para este tenant.",
  noEscalationsOpen: "No hay escalaciones críticas abiertas para este tenant.",
  noReportsGenerated: "No se generaron reportes para este resultado.",
  noNotificationsFound: "No se encontraron registros de notificación para este resultado.",
  reportRegenerated: "Regeneración de reporte iniciada.",
  escalationAcknowledged: "Escalación confirmada.",
  escalationEscalated: "Escalación remitida al siguiente nivel.",
  escalationClosed: "Escalación cerrada.",
  appShell: {
    title: "HOP Portal de Pacientes",
    subtitle:
      "Acceso autoservicio para pacientes: consulta tu historial clínico, citas, órdenes y resultados autorizados.",
    navAriaLabel: "Pantallas del portal",
    languageSwitcherLabel: "Idioma",
    tabs: {
      profile: "Mi Perfil",
      results: "Resultados Médicos",
      appointments: "Mis Citas",
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
    profile: {
      personalInfo: "Información Personal",
      code: "Código de Paciente",
      name: "Nombre Completo",
      birthDate: "Fecha de Nacimiento",
      gender: "Sexo al Nacer",
      document: "Documento de Identidad",
      address: "Dirección",
      preferredLocale: "Idioma Preferido",
      contacts: "Contactos de Emergencia",
      noContacts: "No se registraron contactos de emergencia.",
    },
    results: {
      abnormal: "Abnormal",
      normal: "Normal",
      analyte: "Analito",
      value: "Resultado",
      range: "Rango de Referencia",
      releasedAt: "Liberado el",
    },
    appointments: {
      date: "Fecha y Hora",
      branch: "Sucursal",
      doctor: "Médico",
      status: "Estado",
    },
    orders: {
      orderId: "ID de Orden",
      date: "Fecha",
      status: "Estado de Orden",
      tests: "Pruebas",
    },
  },
} as const;

/** Recursively widens the `esMX` literal string types to `string` so other locales (en-US) can
 * hold different text while TypeScript still enforces identical key structure. */
type Widen<T> = T extends string ? string : { [K in keyof T]: Widen<T[K]> };

export type MessageCatalog = Widen<typeof esMX>;
