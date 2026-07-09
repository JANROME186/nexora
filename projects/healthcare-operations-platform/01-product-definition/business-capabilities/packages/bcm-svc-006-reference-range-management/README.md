# BCM-SVC-006 Reference Range Management Capability Package

Human-readable companion for the Reference Range Management capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-006
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration` (published language consumed by `laboratory-results`)
- Primary aggregate: `TestDefinition` (AGG-006); references `LaboratoryResult` (AGG-009)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: Critical

## Purpose

Defines analyte reference ranges segmented by demographic and clinical criteria (age,
sex, condition) with normal, abnormal and critical thresholds. Ranges are version-aware
and effective-dated, and are the clinical decision data used by technical validation,
medical validation and critical result detection.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard.

## MDPE note

This capability is clinical-decision sensitive: most enforcement is custom (threshold
consistency, segment overlap, analyte publication validation, effective-dated versioning
and effective range resolution). CRUD, DTOs, controllers, repositories, SDKs, Swagger and
repetitive tests remain generated outputs declared in `generation-plan.yaml`.
