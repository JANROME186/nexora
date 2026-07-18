# HOP Enterprise Product Foundation Alignment Backlog

**Closed.** `HOP-ENT-FOUND-001` aligned HOP with the updated Nexora Enterprise Product Foundation
Standard; functional development has resumed with `MVP-MOD-007-PORTAL-001`. See
`08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md` and
`03-architecture/enterprise-foundation/enterprise-foundation-alignment.md` for the full closure
record.

This backlog covers:

- Multilanguage support for `es-MX` and `en-US`.
- Removal or classification of hard-coded labels and visible text in frontend and app.
- IAM permission mapping for every feature, menu item, API operation and sensitive action.
- Dynamic menus and actions from the logged-in user's roles and permissions.
- Login, logout, session expiration and authenticated session context.
- Database architecture, initialization scripts, seed data, data dictionary and 3NF normalization review.
- Country, language and currency support for Mexico/United States, Spanish/English and MXN/USD.
- UX/UI look and feel baseline for web and app.
- Code documentation standards, including Javadoc for Java public/shared contracts.
- Persistence review for JPA/Hibernate, repository ports, raw SQL boundaries and migrations.
- OpenAPI/contract-first generation review for backend, frontend and app.
- Stronger technical-debt burn-down and meaningful coverage improvement while backend/mobile remain below 80 percent.

`HOP-ENT-FOUND-001` closed with backend coverage 76.99% -> 77.32%, frontend coverage 83.98% ->
84.42% (both with no regression), `TD-BE-009` closed, `TD-I18N-002` materially reduced, and 12 new
technical-debt items registered. The project has resumed with `MVP-MOD-007-PORTAL-001`.
