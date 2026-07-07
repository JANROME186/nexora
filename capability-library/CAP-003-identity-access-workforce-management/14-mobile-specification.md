# 14 Mobile Specification

## Mobile Scope MVP

IAM administration is primarily web-first. Mobile scope focuses on:

- Login.
- Branch context selection.
- Profile and preferences.
- View assigned roles.
- View accessible branches.
- Basic session management.

## Operational Mobile Requirements

- Mobile users must receive only permissions required for their operational flows.
- Authorization must be enforced by API, not by mobile UI alone.
- Branch context must be explicit when performing branch-scoped actions.
- Low-end devices must load profile and branch context quickly.

## Deferred Mobile Administration

- Full role editor.
- Permission matrix.
- Access review dashboard.
