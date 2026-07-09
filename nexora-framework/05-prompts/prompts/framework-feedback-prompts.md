# Framework Feedback Prompts

**Artifact ID:** `NXF-FWK-FEEDBACK-PROMPTS-001`  
**Status:** Approved  
**Machine-readable source:** `framework-feedback-prompts.yaml`  
**Version:** `1.0.0`

Use these prompts to capture learning from agent execution and feed the company-owned framework
improvement backlog.

## Capture Execution Feedback

At the end of analysis, validation, backlog implementation, module closeout or release readiness,
review whether the framework caused ambiguity, repeated manual work, missing templates, missing
prompts, contradictory guidance, validation gaps, source-of-truth gaps, handoff gaps or automation
opportunities.

If useful feedback exists, create:

```text
08-qa/framework-feedback/<feedback-id>.yaml
```

and update:

```text
08-qa/framework-feedback/framework-feedback-index.yaml
```

Reusable framework improvements may also create proposed items under:

```text
nexora-framework/07-governance/framework-improvement-backlog/items/
```

Agents do not implement those framework improvements unless Nexora explicitly assigns that work.

## Triage

Nexora can use the triage prompt to group duplicates, review evidence, change priority and schedule
framework improvements.

## Implementation

Framework implementation work starts only from an explicitly assigned central backlog item.
