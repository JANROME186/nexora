# Playbook: Create IAM-Protected Endpoint

1. Read `PROJECT_MANIFEST.yaml`.
2. Read `SOURCE_OF_TRUTH.yaml`.
3. Identify the capability and operation.
4. Define or update the OpenAPI contract.
5. Assign permission code using `resource.action.scope` convention.
6. Add authorization rule to the capability rule set.
7. Add contract test.
8. Add backend guard/policy requirement.
9. Add UI/mobile visibility rule only as a convenience, never as the only control.
10. Update Knowledge Graph relations.
