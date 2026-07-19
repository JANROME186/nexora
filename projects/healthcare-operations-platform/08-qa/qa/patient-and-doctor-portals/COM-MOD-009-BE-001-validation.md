# COM-MOD-009-BE-001 Validation — Patient and Doctor Portals Backend Structures and Schemas Compilation

**Status:** passed
**Backlog item:** COM-MOD-009-BE-001
**Module:** COM-MOD-009 Patient and Doctor Portals
**Code implemented:** Yes

## Scope

Implemented backend access structures, credentials store schemas, BCrypt password hashing, session/login context resolution, granular permissions catalog, audit logging hooks, and support-assisted impersonation flow for Patient and Doctor Portals.

| Component / File | Description |
| --- | --- |
| [schema.sql](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/resources/db/platform-foundation/schema.sql) | Upgraded `identity.user_accounts` table schema to store username, hashed passwords, failed login counts, lockout time, and last login timestamps. |
| [UserAccount.java](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/UserAccount.java) | Expanded Domain model to support authentication status, lockout state tracking, and backwards compatibility. |
| [PasswordHashingService.java](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/PasswordHashingService.java) | Secure password hashing wrapper service implementing BCrypt. |
| [IdentityAccessService.java](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/IdentityAccessService.java) | Implemented login credentials validation, lockout logic (5 attempts, 15 minutes lockout), session termination audit, and impersonation session generation. |
| [AuthController.java](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/adapter/in/web/AuthController.java) | Auth endpoints handler (`/login`, `/logout`, `/assistance`). |
| [HopAuthenticationResolver.java](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthenticationResolver.java) | Dynamic token parsing to load credentials and map roles, including sandboxed support context resolution. |
| [messages_es_MX.properties](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/07-implementation/backend/src/main/resources/i18n/messages_es_MX.properties) | Localized error messages for Mexican Spanish. |

## Key Technical Decisions

- **Impersonation Sandboxing:** Token prefix `assistance-session:tenantId:assistedUserId:actorUserId` maps strictly to the restricted `SUPPORT` role. High-risk actions requiring administrative or clinical privileges are blocked, keeping the user sandbox safe during support sessions.
- **Lockout Timing:** Account lockouts enforce a 5-failed-attempts threshold and a 15-minute cooldown period.
- **Granular Permissions:** Specific roles (`PATIENT`, `REFERRING_DOCTOR`, `SUPPORT`) have been defined in `RolePermissionCatalog` with granular action permissions to eliminate screen-level authorization coupling.

## Validations

All backend verification checks were successfully executed:
- **Build and compilation check:** Maven clean packaging passed successfully.
- **Testing suite:** 269 test cases executed successfully, including new `AuthControllerTest` and `PasswordHashingServiceTest` cases.
- **Coverage baseline:** Backend coverage was verified at 80.49% with zero regression.
- **Agent-agnostic check:** No runtime agent dependencies or vendor-locked terms.
- **Secrets scan:** Verified that password fields use BCrypt hashes; no credentials or keys are hardcoded in test/production code.

## Debt-first review

- **TD-IAM-002 (IAM Permission Granularity):** Resolved by modeling and enforcing granular action permissions (`PORTAL_PATIENT_PROFILE_VIEW`, `PORTAL_PATIENT_RESULTS_VIEW`, `PORTAL_DOCTOR_PATIENTS_VIEW`, etc.) for portals rather than basic screen checks.

## Readiness

- COM-MOD-009-BE-001: **closed**
- Next backlog item: **COM-MOD-009-PORTAL-001** (Compile patient portal commercial workflow)
- HOP commercially complete / GA-ready: **No**
- Coverage baselines: backend 80.49%, employee portal 86.47%, mobile 98.87%, patient portal 41.93%, doctor portal 40.62%.
