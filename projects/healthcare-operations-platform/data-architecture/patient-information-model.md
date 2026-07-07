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
