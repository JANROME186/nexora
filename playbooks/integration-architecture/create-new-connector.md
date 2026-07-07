# Playbook: Create a New Connector

**Playbook ID:** PB-INT-001  
**Version:** 0.20.0  

## Steps

1. Read `PROJECT_MANIFEST.yaml`.
2. Read `SOURCE_OF_TRUTH.yaml`.
3. Identify the business capability that needs the connector.
4. Define connector metadata.
5. Define protocols and supported versions.
6. Define input/output/canonical messages.
7. Define security requirements.
8. Define failure behavior: retry, dead-letter, reconciliation.
9. Define observability requirements.
10. Create `.md` and `.yaml` connector artifacts.
11. Add or update OpenAPI/webhook contracts if required.
12. Add integration tests.
13. Update Knowledge Graph relationships.
14. Update ADR/RFC if the connector introduces a new architectural decision.
