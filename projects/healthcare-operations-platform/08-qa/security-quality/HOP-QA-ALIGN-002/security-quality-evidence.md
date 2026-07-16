# HOP-QA-ALIGN-002 Security Quality Evidence

Backend security and quality gates passed with residual P1 debt.

SpotBugs, Checkstyle, CPD, Dependency-Check and Trivy reported 0 blocking findings. JaCoCo measured 65.82% line coverage, below the 80% final-closure target, and PMD still reports 124 maintainability findings. Those residuals remain tracked in `TD-BE-002`, `TD-BE-003` and `TD-BE-004`. The next backend-touching iteration must not drop below 65.82%.
