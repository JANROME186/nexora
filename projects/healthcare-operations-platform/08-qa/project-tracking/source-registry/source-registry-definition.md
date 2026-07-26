---
artifact:
  id: HOP-SOT-DEFINITION
  type: source-registry-shard
  status: active
  optimization: atomic_context
---

# Source Registry Definition

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
group: definition
entry_count: 84
sources:
  business_capability_map: 01-product-definition/business-capabilities/bcm-001/business-capability-map.md
  capability_dependency_map: 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
  capability_package_index: 01-product-definition/business-capabilities/packages/capability-package-index.md
  capability_packages_root: 01-product-definition/business-capabilities/packages/
  com_mod_009_pkg_bcm_plt_001: 01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/
  com_mod_010_pkg_bcm_inv_001: 01-product-definition/business-capabilities/packages/bcm-inv-001-product-catalog/
  com_mod_010_pkg_bcm_inv_002: 01-product-definition/business-capabilities/packages/bcm-inv-002-reagent-management/
  com_mod_010_pkg_bcm_inv_003: 01-product-definition/business-capabilities/packages/bcm-inv-003-lot-management/
  com_mod_010_pkg_bcm_inv_004: 01-product-definition/business-capabilities/packages/bcm-inv-004-procurement-management/
  com_mod_010_pkg_bcm_inv_005: 01-product-definition/business-capabilities/packages/bcm-inv-005-stock-entries/
  com_mod_010_pkg_bcm_inv_006: 01-product-definition/business-capabilities/packages/bcm-inv-006-stock-exits/
  com_mod_010_pkg_bcm_inv_007: 01-product-definition/business-capabilities/packages/bcm-inv-007-consumption-tracking/
  com_mod_010_pkg_bcm_inv_008: 01-product-definition/business-capabilities/packages/bcm-inv-008-inventory-adjustments/
  com_mod_010_pkg_bcm_inv_009: 01-product-definition/business-capabilities/packages/bcm-inv-009-waste-management/
  com_mod_010_pkg_bcm_qlt_001: 01-product-definition/business-capabilities/packages/bcm-qlt-001-internal-quality-controls/
  com_mod_010_pkg_bcm_qlt_003: 01-product-definition/business-capabilities/packages/bcm-qlt-003-calibration-management/
  com_mod_010_pkg_bcm_qlt_004: 01-product-definition/business-capabilities/packages/bcm-qlt-004-equipment-management/
  com_mod_010_pkg_bcm_qlt_005: 01-product-definition/business-capabilities/packages/bcm-qlt-005-maintenance-management/
  com_mod_011_pkg_bcm_att_001: 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/
  com_mod_011_pkg_bcm_att_006: 01-product-definition/business-capabilities/packages/bcm-att-006-quotation-management/
  com_mod_011_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  com_mod_011_pkg_bcm_svc_001: 01-product-definition/business-capabilities/packages/bcm-svc-001-diagnostic-service-catalog/
  com_mod_011_pkg_bcm_svc_002: 01-product-definition/business-capabilities/packages/bcm-svc-002-test-catalog/
  com_mod_011_pkg_bcm_svc_003: 01-product-definition/business-capabilities/packages/bcm-svc-003-panel-catalog/
  com_mod_011_pkg_bcm_svc_005: 01-product-definition/business-capabilities/packages/bcm-svc-005-patient-preparation-management/
  com_mod_012_pkg_bcm_org_001: 01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/
  com_mod_012_pkg_bcm_plt_001: 01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/
  com_mod_012_pkg_bcm_plt_002: 01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/
  com_mod_012_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  com_mod_012_pkg_bcm_plt_006: 01-product-definition/business-capabilities/packages/bcm-plt-006-observability/
  com_mod_012_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_012_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  com_mod_012_pkg_bcm_plt_009: 01-product-definition/business-capabilities/packages/bcm-plt-009-workflow-engine/
  com_mod_013_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_013_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  com_mod_013_pkg_bcm_qlt_002: 01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/
  com_mod_013_pkg_bcm_qlt_006: 01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/
  com_mod_013_pkg_bcm_qlt_007: 01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/
  com_mod_016_pkg_bcm_org_001: 01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/
  com_mod_016_pkg_bcm_org_002: 01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/
  com_mod_016_pkg_bcm_org_003: 01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/
  com_mod_016_pkg_bcm_plt_002: 01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/
  com_mod_016_pkg_bcm_plt_006: 01-product-definition/business-capabilities/packages/bcm-plt-006-observability/
  com_mod_016_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_016_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  com_mod_017_pkg_bcm_plt_011: 01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/
  mvp_mod_002_pkg_bcm_svc_001: 01-product-definition/business-capabilities/packages/bcm-svc-001-diagnostic-service-catalog/
  mvp_mod_002_pkg_bcm_svc_002: 01-product-definition/business-capabilities/packages/bcm-svc-002-test-catalog/
  mvp_mod_002_pkg_bcm_svc_003: 01-product-definition/business-capabilities/packages/bcm-svc-003-panel-catalog/
  mvp_mod_002_pkg_bcm_svc_004: 01-product-definition/business-capabilities/packages/bcm-svc-004-analyte-catalog/
  mvp_mod_002_pkg_bcm_svc_005: 01-product-definition/business-capabilities/packages/bcm-svc-005-patient-preparation-management/
  mvp_mod_002_pkg_bcm_svc_006: 01-product-definition/business-capabilities/packages/bcm-svc-006-reference-range-management/
  mvp_mod_002_pkg_bcm_svc_007: 01-product-definition/business-capabilities/packages/bcm-svc-007-sample-catalog/
  mvp_mod_002_pkg_bcm_svc_009: 01-product-definition/business-capabilities/packages/bcm-svc-009-price-list-management/
  mvp_mod_003_pkg_bcm_att_002: 01-product-definition/business-capabilities/packages/bcm-att-002-patient-registration/
  mvp_mod_003_pkg_bcm_per_001: 01-product-definition/business-capabilities/packages/bcm-per-001-person-management/
  mvp_mod_003_pkg_bcm_per_002: 01-product-definition/business-capabilities/packages/bcm-per-002-patient-management/
  mvp_mod_003_pkg_bcm_per_003: 01-product-definition/business-capabilities/packages/bcm-per-003-doctor-management/
  mvp_mod_004_pkg_bcm_att_001: 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/
  mvp_mod_004_pkg_bcm_att_003: 01-product-definition/business-capabilities/packages/bcm-att-003-reception-management/
  mvp_mod_004_pkg_bcm_att_004: 01-product-definition/business-capabilities/packages/bcm-att-004-admission-management/
  mvp_mod_004_pkg_bcm_att_006: 01-product-definition/business-capabilities/packages/bcm-att-006-quotation-management/
  mvp_mod_004_pkg_bcm_lab_001: 01-product-definition/business-capabilities/packages/bcm-lab-001-diagnostic-order-management/
  mvp_mod_005_pkg_bcm_att_005: 01-product-definition/business-capabilities/packages/bcm-att-005-cashier-operations/
  mvp_mod_005_pkg_bcm_att_008: 01-product-definition/business-capabilities/packages/bcm-att-008-billing-request-management/
  mvp_mod_006_pkg_bcm_lab_002: 01-product-definition/business-capabilities/packages/bcm-lab-002-sample-collection/
  mvp_mod_006_pkg_bcm_lab_003: 01-product-definition/business-capabilities/packages/bcm-lab-003-sample-labeling/
  mvp_mod_006_pkg_bcm_lab_005: 01-product-definition/business-capabilities/packages/bcm-lab-005-sample-reception/
  mvp_mod_006_pkg_bcm_lab_006: 01-product-definition/business-capabilities/packages/bcm-lab-006-laboratory-processing/
  mvp_mod_006_pkg_bcm_lab_008: 01-product-definition/business-capabilities/packages/bcm-lab-008-technical-validation/
  mvp_mod_006_pkg_bcm_lab_009: 01-product-definition/business-capabilities/packages/bcm-lab-009-medical-validation/
  mvp_mod_006_pkg_bcm_lab_010: 01-product-definition/business-capabilities/packages/bcm-lab-010-result-release/
  mvp_mod_007_pkg_bcm_plt_003: 01-product-definition/business-capabilities/packages/bcm-plt-003-notification-management/
  mvp_mod_007_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  mvp_mod_007_pkg_bcm_res_001: 01-product-definition/business-capabilities/packages/bcm-res-001-result-management/
  mvp_mod_007_pkg_bcm_res_002: 01-product-definition/business-capabilities/packages/bcm-res-002-pdf-report-generation/
  mvp_mod_007_pkg_bcm_res_004: 01-product-definition/business-capabilities/packages/bcm-res-004-digital-delivery/
  mvp_mod_007_pkg_bcm_res_005: 01-product-definition/business-capabilities/packages/bcm-res-005-result-history/
  mvp_mod_007_pkg_bcm_res_006: 01-product-definition/business-capabilities/packages/bcm-res-006-critical-results/
  mvp_mod_007_pkg_bcm_res_007: 01-product-definition/business-capabilities/packages/bcm-res-007-result-notifications/
  mvp_mod_008_pkg_bcm_plt_004: 01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/
  mvp_mod_008_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  mvp_mod_008_pkg_bcm_plt_010: 01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/
  product_definition: 01-product-definition/products/healthcare-operations-platform/product.md
```
