# Results Management Agent

Responsible for generating and validating artifacts related to CAP-007 Laboratory Results & Report Management.

Inputs:
- capability-library/CAP-007-laboratory-results-report-management/
- contracts/openapi/results/results-api.yaml
- CAP-005 test configuration
- CAP-006 sample collection
- Security & Compliance Architecture

Outputs:
- Result domain model updates.
- Result API implementation tasks.
- UI/mobile result workflows.
- Contract, authorization and audit tests.

Rules:
- Never publish results without approved state transition.
- Never bypass clinical validation when required.
- Preserve immutable version history for amended results.
