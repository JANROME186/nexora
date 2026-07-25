# Patient Information Model

## Objetivo

Definir la información del paciente como modelo de información empresarial antes de convertirla en entidades físicas.

## Agrupaciones de información

| Grupo | Descripción | Sensibilidad |
|---|---|---|
| Identidad | Nombre, fecha de nacimiento, sexo, identificadores | Sensitive Personal |
| Contacto | Teléfono, correo, dirección | Sensitive Personal |
| Datos clínicos base | Alergias, condiciones declaradas, notas relevantes | Clinical Sensitive |
| Tutor/Responsable | Datos del tutor para menores o dependientes | Sensitive Personal |
| Consentimientos | Autorizaciones de tratamiento de datos y servicios | Clinical Sensitive |
| Documentos | Identificaciones, consentimientos, archivos | Sensitive Personal |
| Historial operativo | órdenes, citas, pagos, resultados | Clinical/Financial Sensitive |
| Preferencias | idioma, canal de contacto, notificaciones | Confidential |

## Reglas de información

- Un paciente menor de edad debe tener tutor/responsable asociado.
- El idioma preferido del paciente debe usarse para portal, notificaciones y reportes cuando esté disponible.
- Los datos clínicos del paciente no deben mostrarse a usuarios sin permiso explícito.
- Todo acceso al historial clínico debe ser auditable.

## Relación con metamodelo

Este modelo se relaciona con:

- `CAP-001 Patient Management`
- `DOM-001 Patient Domain`
- `ENT-001 Patient`
- `BR-001 Minor Patient Requires Guardian`
- `US-001 Register Patient`
- `API-001 Patients API`

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: INFO-001
name: Patient Information Model
version: 0.16.0
status: Draft
owner: Data Architecture
related_artifacts:
  capability: CAP-001
  domain: DOM-001
  entity: ENT-001
  rule: BR-001
  user_story: US-001
  api: API-001
information_groups:
  identity:
    classification: Sensitive Personal
    fields:
    - fullName
    - birthDate
    - gender
    - externalIdentifiers
  contact:
    classification: Sensitive Personal
    fields:
    - phone
    - email
    - address
  clinical_baseline:
    classification: Clinical Sensitive
    fields:
    - allergies
    - declaredConditions
    - clinicalNotes
  guardian:
    classification: Sensitive Personal
    fields:
    - guardianName
    - guardianRelationship
    - guardianContact
  consents:
    classification: Clinical Sensitive
    fields:
    - privacyConsent
    - serviceConsent
    - dataProcessingConsent
  documents:
    classification: Sensitive Personal
    fields:
    - identificationDocument
    - consentForm
    - attachments
  operational_history:
    classification: Clinical/Financial Sensitive
    fields:
    - orders
    - appointments
    - payments
    - results
  preferences:
    classification: Confidential
    fields:
    - preferredLanguage
    - notificationChannel
    - accessibilityPreferences
rules:
- id: BR-001
  description: Minor patients require guardian or responsible adult.
- id: DATA-RULE-001
  description: Clinical history access must be audited.
- id: DATA-RULE-002
  description: Preferred language must drive patient communications when available.
```
