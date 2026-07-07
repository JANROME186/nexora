# Catalog & Test Configuration Entities

| ID | Entity | Type | Notes |
|---|---|---|---|
| ENT-CAT-001 | CatalogDefinition | Master Data | Catalog metadata and governance. |
| ENT-CAT-002 | CatalogValue | Master/Reference Data | Values per catalog, localized. |
| ENT-CAT-003 | StudyConfiguration | Master Data | Draft/current configuration root. |
| ENT-CAT-004 | StudyVersion | Immutable Snapshot | Published version used by orders/results. |
| ENT-CAT-005 | StudyComponent | Configuration Entity | Panel component/analyte relation. |
| ENT-CAT-006 | AnalyteDefinition | Master Data | Result component definition. |
| ENT-CAT-007 | SampleRequirement | Configuration Entity | Sample, tube, container and volume. |
| ENT-CAT-008 | PreparationInstruction | Configuration Entity | Patient-facing preparation text. |
| ENT-CAT-009 | ReferenceRangeSet | Aggregate | Groups ranges for an analyte. |
| ENT-CAT-010 | ReferenceRange | Configuration Entity | Demographic/method range. |
| ENT-CAT-011 | FormulaDefinition | Configuration Entity | Calculated analyte formula. |
| ENT-CAT-012 | ResultFieldDefinition | Configuration Entity | Reportable field schema. |
| ENT-CAT-013 | ReportTemplateDefinition | Master Data | Report template metadata. |
| ENT-CAT-014 | TemplateSection | Configuration Entity | Template layout block. |
| ENT-CAT-015 | LocalizationEntry | Supporting Entity | Locale text for names/instructions. |
| ENT-CAT-016 | BranchOverride | Configuration Entity | Controlled branch-specific override. |
| ENT-CAT-017 | PriceListReference | Link Entity | Study to price list/version. |
| ENT-CAT-018 | CatalogImportBatch | Transaction Data | Import and validation tracking. |

## Persistence Rules

- Published StudyVersion records are immutable.
- Orders must reference a StudyVersion, not only a StudyConfiguration.
- Reference ranges must be effective-dated.
- Localization entries must include locale and fallback behavior.
- Branch overrides must never delete the inherited tenant/global baseline.
