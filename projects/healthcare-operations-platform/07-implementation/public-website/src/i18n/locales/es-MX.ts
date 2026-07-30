export interface CatalogSectionMessages {
  title: string;
  intro: string;
  detailCta: string;
  backToList: string;
  codeLabel: string;
  versionLabel: string;
}

export interface MarketplaceSectionMessages {
  title: string;
  intro: string;
  searchPlaceholder: string;
  categoryAll: string;
  offersTitle: string;
  noOffers: string;
  offerTypeBasePlan: string;
  offerTypeExpansion: string;
  offerTypeAddon: string;
  offerTypeServices: string;
  trialDaysLabel: string;
  billingRulesLabel: string;
  tierLabel: string;
  contactCta: string;
  capabilitiesTitle: string;
  backToList: string;
  codeLabel: string;
  statusLabel: string;
  contactModalTitle: string;
  contactModalSent: string;
}

export interface MessageCatalog {
  siteName: string;
  skipToContent: string;
  nav: {
    home: string;
    services: string;
    tests: string;
    panels: string;
    preparations: string;
    appointment: string;
    quotation: string;
    marketplace: string;
  };
  languageSwitcherLabel: string;
  footer: {
    rightsReserved: string;
    privacyLink: string;
    tagline: string;
  };
  states: {
    loading: string;
    empty: string;
    error: string;
    retry: string;
  };
  home: {
    heroTitle: string;
    heroSubtitle: string;
    ctaAppointment: string;
    ctaQuotation: string;
    servicesCardTitle: string;
    servicesCardBody: string;
    testsCardTitle: string;
    testsCardBody: string;
    panelsCardTitle: string;
    panelsCardBody: string;
    preparationsCardTitle: string;
    preparationsCardBody: string;
    locationsTitle: string;
    locationsIntro: string;
  };
  catalog: {
    diagnosticServices: CatalogSectionMessages;
    tests: CatalogSectionMessages;
    panels: CatalogSectionMessages;
    preparations: CatalogSectionMessages;
    serviceTypeLabel: string;
    methodologyLabel: string;
    measurementUnitLabel: string;
    resultTypeLabel: string;
    turnaroundTimeLabel: string;
    turnaroundTimeHoursSuffix: string;
    durationLabel: string;
    durationHoursSuffix: string;
    categoryLabel: string;
    instructionsLabel: string;
    notFoundTitle: string;
    notFoundBody: string;
  };
  appointmentForm: {
    title: string;
    intro: string;
    fullNameLabel: string;
    phoneLabel: string;
    emailLabel: string;
    branchLabel: string;
    scheduledStartLabel: string;
    scheduledEndLabel: string;
    itemsLabel: string;
    itemsHelp: string;
    addItem: string;
    removeItem: string;
    contactRequiredHint: string;
    consentLabel: string;
    submit: string;
    submitting: string;
    successTitle: string;
    successBody: string;
    submitAnother: string;
  };
  quotationForm: {
    title: string;
    intro: string;
    fullNameLabel: string;
    phoneLabel: string;
    emailLabel: string;
    branchLabel: string;
    linesLabel: string;
    linesHelp: string;
    quantityLabel: string;
    addLine: string;
    removeLine: string;
    contactRequiredHint: string;
    consentLabel: string;
    submit: string;
    submitting: string;
    successTitle: string;
    successBody: string;
    submitAnother: string;
  };
  itemPicker: {
    kindTest: string;
    kindPanel: string;
    selectPlaceholder: string;
  };
  privacyPage: {
    title: string;
    intro: string;
    dataCollectedTitle: string;
    dataCollectedBody: string;
    dataUseTitle: string;
    dataUseBody: string;
    contactTitle: string;
    contactBody: string;
  };
  notFound: {
    title: string;
    body: string;
    backHome: string;
  };
  errors: {
    generic: string;
    network: string;
    PUBLIC_RATE_LIMIT_EXCEEDED: string;
    PUBLIC_CATALOG_NOT_PUBLISHED: string;
    PUBLIC_APPOINTMENT_REQUEST_INVALID: string;
    PUBLIC_QUOTATION_REQUEST_INVALID: string;
    PUBLIC_PROSPECTIVE_CONTACT_REQUIRED: string;
    validationRequired: string;
    validationContactRequired: string;
    validationSelectAtLeastOneItem: string;
  };
  seo: {
    defaultDescription: string;
    homeTitle: string;
    homeDescription: string;
    servicesTitle: string;
    servicesDescription: string;
    testsTitle: string;
    testsDescription: string;
    panelsTitle: string;
    panelsDescription: string;
    preparationsTitle: string;
    preparationsDescription: string;
    appointmentTitle: string;
    appointmentDescription: string;
    quotationTitle: string;
    quotationDescription: string;
    marketplaceTitle: string;
    marketplaceDescription: string;
    privacyTitle: string;
    privacyDescription: string;
    notFoundTitle: string;
  };
  marketplace: MarketplaceSectionMessages;
}

export const esMX: MessageCatalog = {
  siteName: "Plataforma de Operaciones de Salud",
  skipToContent: "Saltar al contenido principal",
  nav: {
    home: "Inicio",
    services: "Servicios",
    tests: "Pruebas",
    panels: "Paneles",
    preparations: "Preparaciones",
    appointment: "Solicitar cita",
    quotation: "Solicitar cotización",
    marketplace: "Marketplace",
  },
  languageSwitcherLabel: "Idioma",
  footer: {
    rightsReserved: "Todos los derechos reservados.",
    privacyLink: "Aviso de privacidad",
    tagline: "Servicios de laboratorio diagnóstico para tu comunidad.",
  },
  states: {
    loading: "Cargando…",
    empty: "No hay información disponible por el momento.",
    error: "Ocurrió un problema al cargar la información.",
    retry: "Reintentar",
  },
  home: {
    heroTitle: "Servicios de diagnóstico claros, accesibles y a tu ritmo",
    heroSubtitle:
      "Explora nuestro catálogo público de servicios, pruebas, paneles y preparaciones, y solicita una cita o cotización en línea.",
    ctaAppointment: "Solicitar una cita",
    ctaQuotation: "Solicitar una cotización",
    servicesCardTitle: "Servicios diagnósticos",
    servicesCardBody: "Consulta los servicios diagnósticos publicados por nuestro laboratorio.",
    testsCardTitle: "Pruebas de laboratorio",
    testsCardBody: "Explora pruebas individuales, su metodología y tiempo de entrega.",
    panelsCardTitle: "Paneles",
    panelsCardBody: "Conjuntos de pruebas agrupadas para una evaluación integral.",
    preparationsCardTitle: "Preparaciones",
    preparationsCardBody: "Instrucciones para prepararte antes de tu toma de muestra.",
    locationsTitle: "Nuestras sucursales",
    locationsIntro: "Visítanos en cualquiera de nuestras sucursales.",
  },
  catalog: {
    diagnosticServices: {
      title: "Servicios diagnósticos",
      intro: "Servicios diagnósticos publicados y disponibles actualmente.",
      detailCta: "Ver detalle",
      backToList: "Volver a servicios",
      codeLabel: "Código",
      versionLabel: "Versión",
    },
    tests: {
      title: "Pruebas de laboratorio",
      intro: "Pruebas individuales publicadas por nuestro laboratorio.",
      detailCta: "Ver detalle",
      backToList: "Volver a pruebas",
      codeLabel: "Código",
      versionLabel: "Versión",
    },
    panels: {
      title: "Paneles",
      intro: "Paneles de pruebas agrupadas publicados por nuestro laboratorio.",
      detailCta: "Ver detalle",
      backToList: "Volver a paneles",
      codeLabel: "Código",
      versionLabel: "Versión",
    },
    preparations: {
      title: "Preparaciones",
      intro: "Instrucciones de preparación publicadas por nuestro laboratorio.",
      detailCta: "Ver detalle",
      backToList: "Volver a preparaciones",
      codeLabel: "Código",
      versionLabel: "Versión",
    },
    serviceTypeLabel: "Tipo de servicio",
    methodologyLabel: "Metodología",
    measurementUnitLabel: "Unidad de medida",
    resultTypeLabel: "Tipo de resultado",
    turnaroundTimeLabel: "Tiempo de entrega",
    turnaroundTimeHoursSuffix: "horas",
    durationLabel: "Duración",
    durationHoursSuffix: "horas",
    categoryLabel: "Categoría",
    instructionsLabel: "Instrucciones",
    notFoundTitle: "No encontramos este elemento",
    notFoundBody: "Es posible que ya no esté publicado o que el enlace sea incorrecto.",
  },
  appointmentForm: {
    title: "Solicitar una cita",
    intro:
      "Completa el formulario y un miembro de nuestro equipo confirmará tu cita. Esta solicitud no confirma un horario de forma automática.",
    fullNameLabel: "Nombre completo",
    phoneLabel: "Teléfono",
    emailLabel: "Correo electrónico",
    branchLabel: "Sucursal",
    scheduledStartLabel: "Fecha deseada (inicio)",
    scheduledEndLabel: "Fecha deseada (fin)",
    itemsLabel: "Pruebas o paneles de interés",
    itemsHelp: "Selecciona una o más pruebas o paneles para tu cita.",
    addItem: "Agregar prueba o panel",
    removeItem: "Quitar",
    contactRequiredHint: "Proporciona al menos un teléfono o correo electrónico de contacto.",
    consentLabel:
      "Acepto que mis datos de contacto se utilicen únicamente para gestionar esta solicitud.",
    submit: "Enviar solicitud",
    submitting: "Enviando…",
    successTitle: "Solicitud recibida",
    successBody:
      "Hemos recibido tu solicitud de cita. Nuestro equipo se pondrá en contacto contigo para confirmar el horario.",
    submitAnother: "Enviar otra solicitud",
  },
  quotationForm: {
    title: "Solicitar una cotización",
    intro:
      "Selecciona las pruebas o paneles que te interesan y te enviaremos una cotización sin compromiso.",
    fullNameLabel: "Nombre completo",
    phoneLabel: "Teléfono",
    emailLabel: "Correo electrónico",
    branchLabel: "Sucursal",
    linesLabel: "Pruebas o paneles a cotizar",
    linesHelp: "Selecciona una o más pruebas o paneles y la cantidad deseada.",
    quantityLabel: "Cantidad",
    addLine: "Agregar prueba o panel",
    removeLine: "Quitar",
    contactRequiredHint: "Proporciona al menos un teléfono o correo electrónico de contacto.",
    consentLabel:
      "Acepto que mis datos de contacto se utilicen únicamente para gestionar esta solicitud.",
    submit: "Enviar solicitud",
    submitting: "Enviando…",
    successTitle: "Solicitud recibida",
    successBody: "Hemos recibido tu solicitud de cotización. Te la enviaremos en breve.",
    submitAnother: "Enviar otra solicitud",
  },
  itemPicker: {
    kindTest: "Prueba",
    kindPanel: "Panel",
    selectPlaceholder: "Selecciona una prueba o panel",
  },
  privacyPage: {
    title: "Aviso de privacidad",
    intro:
      "Esta página describe, a un nivel general, cómo tratamos los datos de contacto que nos compartes a través de este sitio.",
    dataCollectedTitle: "Datos que recopilamos",
    dataCollectedBody:
      "Al solicitar una cita o cotización, recopilamos únicamente tu nombre, teléfono y/o correo electrónico, junto con las pruebas o paneles de tu interés. No solicitamos información clínica a través de este formulario.",
    dataUseTitle: "Uso de tus datos",
    dataUseBody:
      "Tus datos de contacto se utilizan exclusivamente para dar seguimiento a la solicitud que enviaste. No se comparten con terceros con fines de mercadotecnia.",
    contactTitle: "Contacto",
    contactBody:
      "Si tienes dudas sobre el tratamiento de tus datos, comunícate con la sucursal de tu preferencia.",
  },
  notFound: {
    title: "Página no encontrada",
    body: "La página que buscas no existe o fue movida.",
    backHome: "Volver al inicio",
  },
  errors: {
    generic: "Ocurrió un problema al procesar tu solicitud. Intenta de nuevo más tarde.",
    network: "No se pudo conectar con el servidor. Verifica tu conexión e intenta de nuevo.",
    PUBLIC_RATE_LIMIT_EXCEEDED:
      "Hemos recibido demasiadas solicitudes en poco tiempo. Espera un momento antes de intentar de nuevo.",
    PUBLIC_CATALOG_NOT_PUBLISHED: "Este elemento ya no está publicado o no existe.",
    PUBLIC_APPOINTMENT_REQUEST_INVALID:
      "No pudimos procesar tu solicitud de cita. Revisa los datos ingresados.",
    PUBLIC_QUOTATION_REQUEST_INVALID:
      "No pudimos procesar tu solicitud de cotización. Revisa los datos ingresados.",
    PUBLIC_PROSPECTIVE_CONTACT_REQUIRED:
      "Proporciona al menos un teléfono o correo electrónico de contacto.",
    validationRequired: "Este campo es obligatorio.",
    validationContactRequired: "Proporciona al menos un teléfono o correo electrónico.",
    validationSelectAtLeastOneItem: "Selecciona al menos una prueba o panel.",
  },
  seo: {
    defaultDescription:
      "Descubre servicios diagnósticos, pruebas de laboratorio, paneles e instrucciones de preparación, y solicita una cita o cotización en línea.",
    homeTitle: "Inicio",
    homeDescription:
      "Servicios de diagnóstico claros y accesibles. Explora nuestro catálogo público y solicita una cita o cotización en línea.",
    servicesTitle: "Servicios diagnósticos",
    servicesDescription: "Catálogo público de servicios diagnósticos disponibles.",
    testsTitle: "Pruebas de laboratorio",
    testsDescription: "Catálogo público de pruebas de laboratorio disponibles.",
    panelsTitle: "Paneles",
    panelsDescription: "Catálogo público de paneles de pruebas disponibles.",
    preparationsTitle: "Preparaciones",
    preparationsDescription: "Instrucciones públicas de preparación antes de tu toma de muestra.",
    appointmentTitle: "Solicitar una cita",
    appointmentDescription: "Solicita una cita en línea con nuestro laboratorio.",
    quotationTitle: "Solicitar una cotización",
    quotationDescription: "Solicita una cotización en línea para tus pruebas de laboratorio.",
    marketplaceTitle: "Marketplace de extensiones",
    marketplaceDescription:
      "Descubre paquetes de extensión y ofertas comerciales publicadas para la plataforma.",
    privacyTitle: "Aviso de privacidad",
    privacyDescription: "Cómo tratamos los datos de contacto que nos compartes en este sitio.",
    notFoundTitle: "Página no encontrada",
  },
  marketplace: {
    title: "Marketplace de extensiones y paquetes",
    intro:
      "Explora paquetes de extensión oficiales y ofertas comerciales para potenciar la gestión de tu laboratorio.",
    searchPlaceholder: "Buscar por nombre, código o categoría...",
    categoryAll: "Todas las categorías",
    offersTitle: "Ofertas comerciales disponibles",
    noOffers: "No hay ofertas comerciales asociadas actualmente a este paquete.",
    offerTypeBasePlan: "Plan base",
    offerTypeExpansion: "Paquete de expansión",
    offerTypeAddon: "Módulo adicional de uso",
    offerTypeServices: "Paquete de servicios",
    trialDaysLabel: "Días de prueba gratuita",
    billingRulesLabel: "Reglas de facturación",
    tierLabel: "Nivel de suscripción",
    contactCta: "Solicitar contacto comercial",
    capabilitiesTitle: "Capacidades de la plataforma integradas",
    backToList: "Volver al Marketplace",
    codeLabel: "Código",
    statusLabel: "Estado",
    contactModalTitle: "Contacto comercial",
    contactModalSent:
      "Solicitud de contacto enviada. Un ejecutivo comercial se comunicará contigo pronto.",
  },
};
