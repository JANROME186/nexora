# Impact Analysis Example: BR-001 Minor Patient Requires Guardian

## Changed node

`BR-001 Minor Patient Requires Guardian`

## Directly affected nodes

- `US-001 Register Patient`
- `API-001 Patients API`
- `END-001 Create Patient Endpoint`
- `ENT-001 Patient`
- `UI-001 Patient Registration Web Screen`
- `MOB-001 Patient Registration Mobile Screen`
- `TEST-001 Register Patient Contract Test`

## Impact classification

High.

Reason: The rule affects validation, API schema, frontend/mobile forms and contract tests.

## Required actions

- Update business rule definition.
- Update OpenAPI schema if required.
- Update backend validation.
- Update web/mobile validation messages.
- Update contract tests.
- Update country pack configuration if adult age changes by country.
