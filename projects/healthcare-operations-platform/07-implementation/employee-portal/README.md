# Employee Portal Administration

Frontend implementation of the `PF-FE-001` employee portal administration shell for `MVP-MOD-001 Platform Foundation`.

## Scope

A React + TypeScript single-page application with administration screens for:

- Tenants
- Laboratories
- Branches
- Users
- Role Assignments
- Audit Events

The application consumes the Platform Foundation backend REST API only. It does not invent endpoints beyond what the backend already implements: creation and lookup-by-id for tenant, laboratory, branch and user, scoped role assignment, and audit event search.

## Structure

```
src/
  api/            HTTP client, request/response types and the Platform Foundation API client
  components/
    layout/       Application shell and navigation
    common/       Shared UI (status banners, confirmation dialog, scope indicator)
    screens/      One screen per administration area
  state/          UI state hooks (async action state, shared administration scope)
  test/           Vitest + Testing Library specs
```

Components, the API client, types and UI state are kept in separate folders so screens stay declarative and the API layer can be tested independently of React.

## Install

```bash
npm install
```

## Run

The dev server proxies `/api` requests to `http://localhost:8080`, so start the backend (see `../backend/README.md`) before or while running the frontend.

```bash
npm run dev
```

## Build

```bash
npm run build
```

## Test

```bash
npm test
```

## Type Check

```bash
npm run typecheck
```

## Backend Endpoints Used

- `POST /api/platform/tenants`
- `GET /api/platform/tenants/{tenantId}`
- `POST /api/organization/laboratories`
- `GET /api/organization/laboratories/{laboratoryId}`
- `POST /api/organization/branches`
- `GET /api/organization/branches/{branchId}`
- `POST /api/identity/users`
- `GET /api/identity/users/{userId}`
- `POST /api/identity/users/{userId}/role-assignments`
- `GET /api/audit/events`

## Notes

- Tenant, laboratory, branch and user lists are session-scoped: the backend only exposes create and get-by-id, so each screen keeps a local list of created or looked-up records for the current browser session.
- Role assignment requires an explicit confirmation dialog before submitting, per the UI screen map UX requirement for access-changing actions.
- The current tenant/laboratory/branch/user scope selected in one screen is shared with the others through `AdminScopeContext` and shown via the scope indicator on every screen.
