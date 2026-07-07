# Test Specification

## Test Categories
- Unit tests for state transitions.
- Unit tests for reference range evaluation.
- Contract tests for Results API.
- Authorization tests for validation and release.
- Audit tests for report delivery.
- Regression tests for amendments/versioning.
- Performance tests for result worklists.

## Critical Test Scenarios
- Cannot release non-validated result when validation required.
- Critical value generates alert.
- Amended result preserves previous released version.
- Patient cannot access unreleased result.
- Doctor cannot access unauthorized patient result.
