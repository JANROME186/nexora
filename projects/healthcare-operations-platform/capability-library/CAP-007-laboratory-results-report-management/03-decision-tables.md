# Decision Tables

## DT-RES-001 Result Publication

| Order Status | Sample Status | Result Status | Validation Required | Clinical Validation | Publish? |
|---|---|---|---|---|---|
| Cancelled | Any | Any | Any | Any | No |
| Active | Rejected | Any | Any | Any | No |
| Active | Accepted | Draft | Any | Any | No |
| Active | Accepted | Technical Validated | Yes | No | No |
| Active | Accepted | Technical Validated | No | N/A | Yes |
| Active | Accepted | Clinical Validated | Yes | Yes | Yes |

## DT-RES-002 Critical Value Alert

| Result Value | Reference Range | Critical Range | Action |
|---|---|---|---|
| Normal | Within range | Not critical | No alert |
| Abnormal | Outside range | Not critical | Mark abnormal |
| Critical | Outside critical threshold | Critical | Alert validator and configured recipients |

## DT-RES-003 Report Delivery Channel

| Patient Portal Enabled | Doctor Portal Enabled | WhatsApp Enabled | Email Enabled | Delivery Action |
|---|---|---|---|---|
| Yes | Yes | Optional | Optional | Publish to portals and notify |
| Yes | No | Optional | Optional | Publish patient portal only |
| No | Yes | Optional | Optional | Publish doctor portal only |
| No | No | Yes | Optional | Send notification link if allowed |
| No | No | No | Yes | Email report if allowed |
