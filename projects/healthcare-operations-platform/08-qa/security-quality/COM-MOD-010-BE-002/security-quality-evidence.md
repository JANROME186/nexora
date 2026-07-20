# COM-MOD-010-BE-002 Security Quality Evidence

Status: passed.

Security and quality gates completed for the backend QLT compilation:

- `mvn -q test`: 312 tests, 0 failures, 0 errors, 16 skipped.
- `mvn -q verify`: passed; JaCoCo line coverage stayed at 82.94%.
- `mvn -q -Pquality org.owasp:dependency-check-maven:check`: passed; 65 dependencies and 0 vulnerabilities.
- `trivy fs --scanners vuln,misconfig ...`: passed; 0 vulnerabilities.
- Secrets pattern scan: no hardcoded secrets.
- Agent-agnostic scan: no named-agent dependency.

Dependency-Check used the local advisory database available on July 20, 2026 at 13:56:20 -06:00.
Daily refresh of that database remains an operator/manual responsibility.
