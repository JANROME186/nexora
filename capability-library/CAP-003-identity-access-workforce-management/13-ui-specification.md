# 13 UI Specification

## Web Areas

### Admin Portal

Screens:

- Employee list.
- Employee profile.
- Employee onboarding wizard.
- User account list.
- User invitation screen.
- Role list.
- Role editor.
- Permission matrix.
- Branch access assignment.
- Access review dashboard.
- Audit view for permission changes.

## UX Principles

- Permission assignment must be understandable and searchable.
- High-risk permissions must be visually identified.
- Use progressive disclosure for advanced ABAC rules.
- Never hide backend authorization errors; explain safe, actionable messages.
- Support low-resource devices by avoiding massive permission grids on initial load.

## Accessibility

- Keyboard navigation for permission tables.
- Clear contrast for enabled/disabled access.
- Screen-reader friendly permission labels.
