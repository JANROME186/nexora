# Enterprise Product Foundation Standard

This standard defines the enterprise foundations every Nexora product must have before functional development continues toward commercial readiness.

The required foundations are:

- Multilanguage support through localization resources, starting with `es-MX` and `en-US`.
- IAM-driven permissions for every feature, menu item, API command, query and sensitive action.
- Dynamic menus and action availability based on the logged-in user's roles, permissions, tenant, branch and entitlements.
- Login, logout, session expiration, authenticated user context and permission loading.
- Database deliverables independent from infrastructure: schema, migrations, initialization, seed data, data dictionary and normalization review.
- UX/UI baseline for web and app: design tokens, layout rules, component inventory and accessibility expectations.
- Stack-appropriate code documentation such as Javadoc for Java public contracts and documentation for shared frontend/app contracts.
- Decoupled persistence architecture, with ORM/JPA/Hibernate or an explicitly justified alternative for Java persistence.
- Contract-first/OpenAPI-first API delivery and generation where practical.
- Active technical-debt burn-down and meaningful coverage improvement when a stack is below 80 percent.

Functional development must stop when these foundations are missing in a way that affects security, localization, persistence, session management, contract-first development or product-wide UX consistency.

Coverage below 80 percent must improve intentionally. A 0.01 percentage point increase is not enough when the stack remains below target. The expected improvement is 3 to 5 percentage points per relevant iteration unless the agent documents why the current slice cannot reasonably achieve it and registers immediate coverage debt.
