# Patient → Order → Sample → Result Application Flow

**ID:** APP-FLOW-001  
**Estado:** Draft  
**Versión:** 0.17.0

## Flujo lógico

```mermaid
sequenceDiagram
    actor Receptionist
    participant AdminPortal
    participant API
    participant PatientSvc
    participant OrderSvc
    participant SampleSvc
    participant ResultSvc
    participant NotificationSvc
    participant PatientPortal

    Receptionist->>AdminPortal: Registra o busca paciente
    AdminPortal->>API: POST /patients or GET /patients
    API->>PatientSvc: execute use case
    PatientSvc-->>API: patientId
    Receptionist->>AdminPortal: Crea orden
    AdminPortal->>API: POST /orders
    API->>OrderSvc: create order
    OrderSvc-->>API: orderId
    OrderSvc->>SampleSvc: publish OrderCreated
    SampleSvc-->>AdminPortal: muestras y etiquetas
    ResultSvc->>NotificationSvc: ResultPublished
    NotificationSvc->>PatientPortal: aviso de resultado disponible
```

## Eventos involucrados

- PatientRegistered
- PatientUpdated
- OrderCreated
- SampleLabelGenerated
- SampleCollected
- SampleRejected
- ResultCaptured
- ResultValidated
- ResultPublished
- PatientNotified

## APIs involucradas

- Patients API
- Orders API
- Samples API
- Results API
- Notifications API

## Reglas

- Una orden no puede crearse sin paciente válido.
- Las muestras deben tener trazabilidad desde su generación hasta el resultado.
- Un resultado no puede publicarse sin validación/firma cuando la prueba lo requiera.
- La notificación no debe revelar datos clínicos sensibles fuera de canales seguros.
