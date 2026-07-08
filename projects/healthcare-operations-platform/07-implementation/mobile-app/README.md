# Mobile App Foundation

Mobile implementation foundation for `PF-APP-001` in `MVP-MOD-001 Platform Foundation`.

## Scope

This package establishes a mobile-ready application core for Healthcare Operations Platform:

- Local baseline login.
- Local session handling.
- Authenticated navigation state.
- Initial home screen model.
- Platform Foundation API client prepared for backend integration.
- Structure ready for future clinical and operational modules.

The foundation is intentionally limited to Platform Foundation scope. It does not implement clinical workflows, inventory workflows, billing workflows or external integrations.

## Structure

```
src/
  api/          Platform Foundation API client and request/response types
  auth/         Local login and session storage
  navigation/   Mobile route model and navigation reducer
  screens/      Screen view models for login and authenticated home
  test/         Vitest tests for session, login, navigation and API client behavior
  mobileApp.ts  Composition root for the mobile application core
```

The code is renderer-agnostic TypeScript so it can be connected to React Native or Expo UI components in the next mobile increment without changing authentication, navigation or API contracts.

## Install

This package currently reuses the validated TypeScript and Vitest toolchain from `../employee-portal/node_modules` to avoid duplicating frontend dependencies during the foundation increment.

Install the employee portal dependencies first when needed:

```bash
cd ../employee-portal
npm install
```

## Type Check

```bash
npm run typecheck
```

## Test

```bash
npm test
```

## Build

```bash
npm run build
```

## Backend Integration

The API client is prepared for:

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

Use `createPlatformFoundationApi({ baseUrl })` from `src/api/platformFoundationApi.ts` to point the mobile app at a local or remote backend.
