# Node ID Convention

All product artifacts must use stable identifiers.

| Prefix | Meaning |
|---|---|
| `CAP` | Business Capability |
| `VAL` | Value Stream |
| `BPMN` | Business Process Model |
| `DOM` | Domain / Bounded Context |
| `UL` | Ubiquitous Language Term |
| `BR` | Business Rule |
| `US` | User Story |
| `UC` | Use Case |
| `API` | API Contract |
| `END` | API Endpoint |
| `ENT` | Entity |
| `VO` | Value Object |
| `EVT` | Domain Event |
| `CMD` | Command |
| `QRY` | Query |
| `UI` | Web Screen |
| `MOB` | Mobile Screen |
| `DS` | Design System Component |
| `TEST` | Test Artifact |
| `SEC` | Security Control |
| `OBS` | Observability Artifact |
| `ADR` | Architecture Decision Record |
| `RFC` | Request for Comments |
| `PB` | Playbook |
| `AGT` | Agent |

## Format

```text
<PREFIX>-<3 digit sequence>
```

Example:

```text
CAP-001
US-001
API-001
```

IDs must never be reused, even if a node is deprecated.
