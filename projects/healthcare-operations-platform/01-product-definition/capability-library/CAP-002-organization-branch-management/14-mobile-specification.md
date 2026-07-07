# 14 Mobile Specification

## Mobile Scope MVP

Organization management is primarily administrative and web-first. Mobile support focuses on operational read-only and limited branch context selection.

Mobile features:

- Select active branch context.
- View branch information.
- View branch schedule.
- View service availability.
- View branch contact details.

## Low-Resource Mobile Rules

- Branch data must be cacheable.
- Avoid complex hierarchy editing on low-end devices.
- Offline read-only branch metadata should be available when previously synced.
- Administrative write operations may redirect to web when complex.
