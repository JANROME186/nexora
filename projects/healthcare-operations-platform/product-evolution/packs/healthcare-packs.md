# Healthcare Pack Architecture

**Artifact ID:** HPK-001  
**Status:** Draft  
**Version:** 0.22.0

## Purpose

Healthcare Packs allow Nexora to extend beyond clinical laboratories while reusing the same platform foundation.

## Initial Healthcare Packs

| Pack | Scope |
|---|---|
| Clinical Lab | Laboratory tests, samples, results, reference values. |
| Imaging | RIS, PACS, DICOM, radiology reports. |
| Pathology | Specimens, slides, pathology workflow, structured reports. |
| Blood Bank | Donor, units, compatibility, transfusion traceability. |
| Veterinary | Species, breeds, veterinary patients and owners. |

## Design Rules

1. A healthcare pack may add domain concepts.
2. A healthcare pack may extend workflows.
3. A healthcare pack may add UI surfaces.
4. A healthcare pack must not modify core platform contracts without versioning.
5. A healthcare pack must declare required capabilities and permissions.
