# MOB-001 Register Patient Mobile Screen

Mobile patient registration flow designed for low, mid and high-range Android/iOS devices.

Core features must remain usable on modest devices. AI capabilities are progressive enhancements.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MOB-001
type: mobileScreen
name: Register Patient Mobile Screen
status: draft
version: 0.15.0
owner: Mobile UX
channel: employee-mobile
capability: CAP-001
story: US-001
minimumExperience:
- manual registration
- basic validation
- offline draft support
progressiveCapabilities:
  intelligent:
  - AI-assisted OCR when device and plan support it
relations:
- type: represents
  target: US-001
- type: consumes
  target: API-001
```
