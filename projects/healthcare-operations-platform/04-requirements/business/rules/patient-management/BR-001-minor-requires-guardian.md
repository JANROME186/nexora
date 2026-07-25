# BR-001 Minor Patient Requires Guardian

If the patient is a minor according to the configured country/legal age, Nexora must require guardian information before allowing clinical, billing or consent-dependent workflows to continue.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: BR-001
type: businessRule
name: Minor Patient Requires Guardian
status: draft
version: 0.15.0
owner: Business Architecture
severity: mandatory
statement: If a patient is legally a minor, guardian information must be captured
  before continuing workflows that require consent.
condition:
  field: patient.age
  operator: lessThan
  valueSource: countryPack.legalAdultAge
action:
  type: requireData
  fields:
  - guardian.fullName
  - guardian.relationship
  - guardian.contact
relations:
- type: constrains
  target: CAP-001
- type: constrains
  target: US-001
- type: validatedBy
  target: QA-001
```
