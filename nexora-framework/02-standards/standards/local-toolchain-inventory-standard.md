# Local Toolchain Inventory Standard

**Artifact ID:** `NXF-LOCAL-TOOLCHAIN-001`  
**Status:** Approved  
**Machine-readable source:** `local-toolchain-inventory-standard.yaml`  
**Version:** `1.0.0`

Every Nexora project must maintain a machine-readable local toolchain inventory once its stack is
selected. The inventory tells agents where the required tools are installed on the development
machine, which versions are available and which generic commands should be used.

The project artifact lives at:

`03-architecture/technology-architecture/local-toolchain-inventory.yaml`

with a human companion:

`03-architecture/technology-architecture/local-toolchain-inventory.md`

## Purpose

Agents should not rediscover Java, Maven, Node, npm, Git, Docker, Trivy, local vulnerability
databases or similar tooling on every backlog item. They should load the inventory first, verify the
listed tools still exist, and then use the project-approved command templates.

## What Belongs Here

- Repository root path on the current machine.
- Operating system and shell assumptions.
- Tool executable paths and versions.
- Tool home paths and settings files when relevant.
- Generic commands for version checks, build, tests, quality gates, security scans and local runtime
  startup/stop.
- Local advisory database paths such as OWASP Dependency-Check data directories.
- Maintenance policy and last verification date.

## What Does Not Belong Here

- Secrets, credentials, tokens or passwords.
- Every command executed during a backlog item.
- One-off debugging commands.
- Agent-specific dependencies or instructions.
- Evidence history. Evidence belongs under `08-qa/`.

## Relationship To Other Artifacts

The stack quality toolchain baseline says which tools are required. The local toolchain inventory
says where those tools are installed on this machine. The integrated local solution runbook says how
to start and validate the solution end to end.

If a required tool is missing or the path is stale, the agent must update the inventory when the
correct path is known. If the required tool cannot be made available, the agent must register or
update technical debt before closing the backlog.

## Closure Rule

For code-changing backlog items, agents must load this inventory before running commands. Closure
evidence must record that the inventory was loaded, which tool paths were used, which version checks
were performed and how stale or missing tools were handled.
