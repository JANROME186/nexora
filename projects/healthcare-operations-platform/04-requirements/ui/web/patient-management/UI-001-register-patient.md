# UI-001 Register Patient Web Screen

Simple, accessible and low-resource web screen for patient registration.

It must work in common commercial browsers and must progressively enhance advanced functions such as AI-assisted capture.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: UI-001
type: uiScreen
name: Register Patient Web Screen
status: draft
version: 0.15.0
owner: UX
channel: employee-web
capability: CAP-001
story: US-001
progressiveCapabilities:
  core:
  - manual patient form
  - duplicate search
  - validation messages
  enhanced:
  - document upload
  - keyboard shortcuts
  intelligent:
  - AI-assisted demographic extraction
relations:
- type: represents
  target: US-001
- type: consumes
  target: API-001
```
