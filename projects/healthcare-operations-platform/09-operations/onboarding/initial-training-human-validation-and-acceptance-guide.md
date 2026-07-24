# Initial Training, Human Validation & Customer Acceptance Guide

## Overview

This guide establishes the mandatory training matrix, human verification workflows, parallel run validation protocols, and customer acceptance criteria for onboarding a new commercial customer on **Healthcare Operations Platform (HOP)**.

## Role-Based Training Matrix

| Target Role | Key Capabilities Covered | Standard Duration | Success Criteria |
|---|---|---|---|
| **System Administrators** | Tenant config, user provisioning, RBAC, branch setup, API governance | 4 Hours | Successfully create users, assign permissions, and verify session headers |
| **Front Desk Staff** | Patient registration, walk-in/scheduled orders, cashier sessions, payment recording | 6 Hours | Process 10 simulated walk-in orders from intake to paid sale with zero errors |
| **Lab Technicians** | Sample reception, sample labeling, result entry, technical validation, stock movements | 8 Hours | Receive, process, enter results and technically validate a 20-sample batch |
| **Medical Validators** | Medical review, critical results escalation, PDF report generation, release | 6 Hours | Review, interpret and medically release 15 diagnostic reports |
| **Quality Managers** | Internal QC runs, calibration management, equipment maintenance, CAPA events | 4 Hours | Record daily QC run, equipment calibration, and log a CAPA incident |
| **Referring Doctors** | Doctor Portal navigation, referred patient search, released report download | 2 Hours | Authenticate, search referred patients, and view released PDF report |

## Human-in-the-Loop Clinical & Financial Verification Rules

HOP enforces strict Human-in-the-Loop (HITL) verification policies across all operational modules:

1. **No Autonomous Medical Release**:
   - Diagnostic test results can NEVER be released to patients or external channels without explicit human medical validation (`PERM_RESULT_VAL_MED`) by a credentialed medical director or validator.
2. **Dual Technical & Medical Validation**:
   - Technical validation (`PERM_RESULT_VAL_TECH`) confirms analytical sanity and instrument calibration; medical validation confirms clinical plausibility.
3. **Immutable Released Artifacts**:
   - Once a report is released, it is sealed as an immutable PDF (`BCM-RES-002`). Re-validation or amendment requires a documented human amendment workflow with clear audit trail (`BCM-PLT-007`).
4. **Cashier Variance Authorization**:
   - Closing a cash session with cash variance exceeding tenant-configurable thresholds requires supervisory human override (`PERM_CASH_MANAGE`).

## Parallel Run Protocol (Parallel Testing Phase)

Before cutover to production, the customer MUST execute a 5-day parallel run:
- **Dual Intake**: Process real daily patient volume in parallel with the legacy system.
- **Result Comparison**: Compare released reports, analytical values, and pricing totals between legacy system and HOP for 100% of parallel orders.
- **Discrepancy Threshold**: Zero tolerance for clinical result discrepancies; financial calculation variance must be $0.00.

## Customer Acceptance Protocol & Sign-Off Criteria

Formal customer sign-off requires meeting ALL of the following criteria:

- [ ] 100% of staff roles completed designated training modules.
- [ ] 5-day parallel run completed with zero unresolved clinical discrepancies.
- [ ] Data migration reconciliation verified and signed off by customer project manager.
- [ ] Technical prerequisites and security controls verified (SSL, CORS, backup schedule).
- [ ] Support escalation workflow and hypercare contacts handed over to customer team.
- [ ] Formal Customer Acceptance Document executed by Customer Representative and HOP Enablement Manager.
