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
    title: "Plataforma de Operaciones de Salud - Administración del Portal de Empleados",
    subtitle:
      "Fundamento de Plataforma, Catálogo Diagnóstico, Datos Maestros de Personas y Clínicos, " +
      "Recepción y Atención al Paciente, Caja y Facturación, Flujo de Trabajo de Laboratorio, y " +
      "Resultados y Entrega Digital, Integraciones y Migraciones: administración, auditoría, catálogo, registros de " +
      "pacientes/doctores, órdenes diagnósticas, sesiones de caja, flujo de trabajo de " +
      "laboratorio, gestión de entrega de resultados, gobierno de APIs e ingesta de datos.",
    navAriaLabel: "Pantallas de administración",
    languageSwitcherLabel: "Idioma",
    tabs: {
      tenants: "Organizaciones",
      laboratories: "Laboratorios",
      branches: "Sucursales",
      users: "Usuarios",
      roleAssignments: "Asignación de Roles",
      auditEvents: "Eventos de Auditoría",
      diagnosticCatalog: "Catálogo Diagnóstico",
      personSearch: "Búsqueda de Personas",
      patients: "Pacientes",
      doctors: "Doctores",
      patientRegistrations: "Registros de Pacientes",
      reception: "Recepción",
      diagnosticOrders: "Órdenes Diagnósticas",
      cashSessions: "Sesiones de Caja",
      sales: "Ventas",
      billingRequests: "Solicitudes de Facturación",
      sampleCollection: "Recolección de Muestras",
      sampleLabeling: "Etiquetado de Muestras",
      sampleReception: "Recepción de Muestras",
      laboratoryProcessing: "Procesamiento de Laboratorio",
      technicalValidation: "Validación Técnica",
      medicalValidation: "Validación Médica",
      resultRelease: "Liberación de Resultados",
      resultSearch: "Búsqueda de Resultados",
      resultReports: "Reportes de Resultados",
      criticalEscalations: "Escalaciones Críticas",
      resultNotifications: "Notificaciones de Resultados",
      integrationEndpoints: "Integraciones",
      apiManagement: "Gobierno de APIs",
      migrationJobs: "Migraciones",
    },
  },
  integrationMigration: {
    shared: {
      actorId: "ID del actor",
      tenantRequired: "Selecciona un tenant antes de continuar.",
      laboratoryRequired: "Selecciona un laboratorio antes de continuar.",
      loaded: "Registros cargados.",
      selectFirst: "Selecciona un registro primero.",
      noRecords: "No hay registros para el alcance seleccionado.",
      status: "Estado",
      actions: "Acciones",
      load: "Cargar",
      create: "Crear",
      retire: "Retirar",
      retry: "Reintentar",
      approve: "Aprobar",
      commit: "Confirmar",
      runDryRun: "Ejecutar validación previa",
    },
    integrationEndpoints: {
      heading: "Administración de Integraciones",
      description:
        "Gestiona endpoints, mensajes normalizados, reintentos y referencias de dead-letter para interoperabilidad.",
      endpointName: "Nombre del endpoint",
      protocol: "Protocolo",
      direction: "Dirección",
      endpointId: "ID del endpoint",
      externalMessageId: "ID externo del mensaje",
      rawPayload: "Payload crudo",
      messageId: "ID del mensaje",
      normalizationStatus: "Normalización",
      retryCount: "Reintentos",
      registerSuccess: "Endpoint registrado.",
      retireSuccess: "Endpoint retirado.",
      receiveSuccess: "Mensaje recibido.",
      retrySuccess: "Mensaje reintentado.",
      loadEndpoints: "Cargar endpoints",
      sendMessage: "Recibir mensaje",
      loadMessage: "Cargar detalle del mensaje",
    },
    apiManagement: {
      heading: "Gobierno de APIs",
      description:
        "Clasifica operaciones, administra ventanas de deprecación, llaves de partner y límites de consumo.",
      operationId: "ID de operación",
      ownerCapability: "Capability propietaria",
      classification: "Clasificación",
      apiVersion: "Versión API",
      deprecationFrom: "Inicio de deprecación",
      deprecationTo: "Fin de deprecación",
      migrationNote: "Nota de migración",
      consumerName: "Consumidor",
      grantedScopes: "Scopes otorgados",
      keyId: "ID de llave",
      requestsPerMinute: "Solicitudes por minuto",
      classifySuccess: "Operación clasificada.",
      deprecationSuccess: "Deprecación programada.",
      retirementSuccess: "Operación retirada.",
      keySuccess: "Llave emitida.",
      revokeSuccess: "Llave revocada.",
      rateLimitSuccess: "Política de límite actualizada.",
      loadOperations: "Cargar operaciones",
      loadKeys: "Cargar llaves",
      scheduleDeprecation: "Programar deprecación",
      issueKey: "Emitir llave",
      revokeKey: "Revocar llave",
      updateRateLimit: "Actualizar límite",
    },
    migrationJobs: {
      heading: "Administración de Migraciones",
      description:
        "Gestiona trabajos, paquetes simples de ingesta, validación previa, aprobación, ejecución y reconciliación.",
      sourceSystemName: "Sistema origen",
      migrationJobId: "ID del trabajo",
      importBatchId: "ID del lote",
      manifest: "Manifest",
      packageFile: "Paquete",
      zipBundle: "ZIP",
      entityCounts: "Conteos",
      checkpoint: "Checkpoint",
      executionId: "ID de ejecución",
      phase: "Fase",
      imported: "Importados",
      rejected: "Rechazados",
      skipped: "Omitidos",
      warnings: "Advertencias",
      createSuccess: "Trabajo de migración creado.",
      uploadSuccess: "Paquete recibido.",
      dryRunSuccess: "Validación previa ejecutada.",
      approveSuccess: "Importación aprobada.",
      commitSuccess: "Importación confirmada.",
      retrySuccess: "Ejecución reintentada.",
      reconciliationSuccess: "Reconciliación cargada.",
      loadJobs: "Cargar trabajos",
      uploadPackage: "Subir paquete",
      loadReconciliation: "Cargar reconciliación",
    },
  },
} as const;

/** Recursively widens the `esMX` literal string types to `string` so other locales (en-US) can
 * hold different text while TypeScript still enforces identical key structure. */
type Widen<T> = T extends string ? string : { [K in keyof T]: Widen<T[K]> };

export type MessageCatalog = Widen<typeof esMX>;
