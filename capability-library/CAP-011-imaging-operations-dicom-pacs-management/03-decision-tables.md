# Decision Tables

## Imaging Appointment Eligibility

| Conditions | Outcome |
|---|---|
| Branch open, room available, modality active, patient prepared | Confirm appointment |
| Branch open, room available, modality active, preparation missing | Confirm with preparation warning |
| Room unavailable | Offer alternative slot |
| Modality inactive or under maintenance | Block appointment |
| Study requires authorization and authorization missing | Hold appointment pending authorization |

## DICOM Reconciliation Decision

| Conditions | Outcome |
|---|---|
| Patient ID, accession number and order match | Auto-link DICOM study |
| Accession matches but demographic mismatch exists | Require reconciliation review |
| No order/accession match | Place in unmatched worklist |
| Duplicate StudyInstanceUID exists | Ignore duplicate or merge metadata according to policy |
| Study belongs to another tenant | Reject and raise security event |

## Report Release Decision

| Conditions | Outcome |
|---|---|
| Report signed, payment policy satisfied, publication enabled | Release to portal and physician |
| Report signed, publication disabled | Keep internal; allow manual delivery |
| Report unsigned | Block release |
| Report amended | Release amended version and preserve prior version |
| Critical finding configured | Release and trigger alert workflow |

## Viewer Access Decision

| Conditions | Outcome |
|---|---|
| User is patient owner and study released | Allow limited viewer access |
| User is referring physician and linked to order | Allow physician viewer access |
| User is radiologist assigned to study | Allow diagnostic viewer access |
| Link expired | Deny and request new link |
| Tenant mismatch | Deny and raise security event |
