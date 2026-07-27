---
artifact:
  id: HOP-SOT-IMPLEMENTATION
  type: source-registry-shard
  status: active
  optimization: atomic_context
---

# Source Registry Implementation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
group: implementation
entry_count: 59
sources:
  com_mod_015_fe_001_employee_portal_ai_overlay_api: 07-implementation/employee-portal/src/api/aiOverlayApi.ts
  com_mod_015_fe_001_employee_portal_ai_overlay_screen: 07-implementation/employee-portal/src/components/screens/AiAssistantReviewScreen.tsx
  com_mod_015_fe_001_employee_portal_ai_overlay_tests: 07-implementation/employee-portal/src/test/AiAssistantReviewScreen.test.tsx,
    07-implementation/employee-portal/src/test/aiOverlayApi.test.ts
  com_mod_015_be_001_backend_ai_overlay: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/aioverlay/
  com_mod_015_be_001_schema: 07-implementation/backend/src/main/resources/db/ai-overlay/schema.sql
  com_mod_010_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/inventoryquality/
  com_mod_010_be_001_schema: 07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql
  com_mod_010_fe_001_employee_portal_api: 07-implementation/employee-portal/src/api/inventoryQualityApi.ts
  com_mod_010_fe_001_employee_portal_screens: 07-implementation/employee-portal/src/components/screens/
  com_mod_011_be_001_backend_publicweb: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/publicweb/
  com_mod_011_be_001_catalog_public_read_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/publicreads/
  com_mod_011_be_001_frontdesk_public_intake_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/publicintake/
  com_mod_011_be_001_public_rate_limit_interceptor: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/apimanagement/adapter/in/web/PublicApiRateLimitInterceptor.java
  com_mod_011_be_001_public_web_api_test: 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/publicweb/PublicWebApiTest.java
  com_mod_011_fe_001_accessibility_test: 07-implementation/employee-portal/src/test/accessibility.test.tsx
  com_mod_011_fe_001_appointment_requests_screen: 07-implementation/employee-portal/src/components/screens/PublicAppointmentRequestsScreen.tsx
  com_mod_011_fe_001_content_review_screen: 07-implementation/employee-portal/src/components/screens/PublicContentReviewScreen.tsx
  com_mod_011_fe_001_public_content_api: 07-implementation/employee-portal/src/api/publicContentApi.ts
  com_mod_011_fe_001_public_requests_api: 07-implementation/employee-portal/src/api/publicRequestsApi.ts
  com_mod_011_fe_001_quotation_requests_screen: 07-implementation/employee-portal/src/components/screens/PublicQuotationRequestsScreen.tsx
  com_mod_011_web_001_accessibility_test: 07-implementation/public-website/src/test/accessibility.test.tsx
  com_mod_011_web_001_api_client: 07-implementation/public-website/src/api/
  com_mod_011_web_001_public_website: 07-implementation/public-website/
  com_mod_011_web_001_site_config: 07-implementation/public-website/src/config/siteConfig.ts
  com_mod_012_be_001_backend_observability: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/observability/
  com_mod_012_be_001_backend_organizationmanagement: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/
  com_mod_012_be_001_backend_platformconfiguration: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/platformconfiguration/
  com_mod_012_be_001_schema: 07-implementation/backend/src/main/resources/db/platform-hardening-and-saas-operations/schema.sql
  mvp_mod_001_backend_implementation: 07-implementation/backend/
  mvp_mod_001_frontend_implementation: 07-implementation/employee-portal/
  mvp_mod_001_local_runtime_compose: 07-implementation/compose.local.json
  mvp_mod_001_local_runtime_env_example: 07-implementation/.env.example
  mvp_mod_001_mobile_implementation: 07-implementation/mobile-app/
  mvp_mod_002_backend_pom: 07-implementation/backend/pom.xml
  mvp_mod_002_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
  mvp_mod_002_be_001_schema: 07-implementation/backend/src/main/resources/db/catalog-test-configuration/schema.sql
  mvp_mod_002_be_002_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
  mvp_mod_002_fe_001_catalog_api: 07-implementation/employee-portal/src/api/catalogApi.ts
  mvp_mod_002_fe_001_catalog_types: 07-implementation/employee-portal/src/api/types.ts
  mvp_mod_002_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/DiagnosticCatalogScreen.tsx
  mvp_mod_003_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/
  mvp_mod_003_be_001_schema: 07-implementation/backend/src/main/resources/db/people-and-clinical-master-data/schema.sql
  mvp_mod_003_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/
  mvp_mod_003_fe_001_people_api: 07-implementation/employee-portal/src/api/peopleApi.ts
  mvp_mod_003_fe_001_people_types: 07-implementation/employee-portal/src/api/types.ts
  mvp_mod_004_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/
  mvp_mod_004_be_001_schema: 07-implementation/backend/src/main/resources/db/front-desk-care-delivery/schema.sql
  mvp_mod_005_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/
  mvp_mod_005_be_001_schema: 07-implementation/backend/src/main/resources/db/cash-sales/schema.sql
  mvp_mod_005_be_002_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/
  mvp_mod_005_be_002_fiscal_adapter_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/billingrequestmanagement/domain/FiscalAdapterPort.java
  mvp_mod_005_be_002_frontdesk_sale_source_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/application/FrontDeskSaleSourcePort.java
  mvp_mod_005_fe_001_cash_sales_api: 07-implementation/employee-portal/src/api/cashSalesApi.ts
  mvp_mod_005_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/CashSessionsScreen.tsx,
    07-implementation/employee-portal/src/components/screens/SalesScreen.tsx, 07-implementation/employee-portal/src/components/screens/BillingRequestsScreen.tsx
  mvp_mod_008_be_001_backend_implementation_api_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/apimanagement/
  mvp_mod_008_be_001_backend_implementation_integration_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/integrationmanagement/
  mvp_mod_008_be_001_backend_implementation_migration_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/datamigrationportability/migrationmanagement/
  mvp_mod_008_be_001_schema_integration: 07-implementation/backend/src/main/resources/db/integration-interoperability/schema.sql
  mvp_mod_008_be_001_schema_migration: 07-implementation/backend/src/main/resources/db/data-migration-portability/schema.sql
```
