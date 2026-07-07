# 15 AI Use Cases

AI is optional, progressive and always supervised for IAM operations.

| ID | Use Case | Description | Human Oversight |
|---|---|---|---|
| AI-IAM-001 | Role recommendation assistant | Suggest roles based on position and branch responsibilities. | Required |
| AI-IAM-002 | Excess access detector | Identify users with unusual permission combinations. | Required |
| AI-IAM-003 | Access review summarizer | Summarize access review findings. | Required |
| AI-IAM-004 | Permission explanation assistant | Explain what a permission allows in plain language. | Required |

## Guardrails

- AI must never grant or revoke access directly.
- AI must not expose hidden permissions to unauthorized users.
- AI suggestions must include reason and confidence.
- All accepted AI recommendations must be audited as human-approved changes.
