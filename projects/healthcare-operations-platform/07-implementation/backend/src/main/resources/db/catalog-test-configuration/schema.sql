-- Generated/model-derived schema for MVP-MOD-002 Diagnostic Catalog (catalog-test-configuration
-- bounded context). Source models: 01-product-definition/business-capabilities/packages/
-- bcm-svc-001..007,009 business-model.yaml. Compiled for backlog item MVP-MOD-002-BE-001.

CREATE SCHEMA IF NOT EXISTS catalog;

-- BCM-SVC-001 Diagnostic Service Catalog
CREATE TABLE IF NOT EXISTS catalog.diagnostic_services (
    service_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    category_id varchar(36),
    service_type varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.diagnostic_service_component_links (
    link_id varchar(36) PRIMARY KEY,
    service_id varchar(36) NOT NULL REFERENCES catalog.diagnostic_services (service_id),
    component_type varchar(20) NOT NULL,
    component_ref_id varchar(36) NOT NULL,
    display_order integer
);

-- BCM-SVC-002 Test Catalog
CREATE TABLE IF NOT EXISTS catalog.test_definitions (
    test_definition_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    methodology varchar(240),
    measurement_unit varchar(40),
    result_type varchar(30) NOT NULL,
    turnaround_time_hours integer,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.test_analyte_links (
    link_id varchar(36) PRIMARY KEY,
    test_definition_id varchar(36) NOT NULL REFERENCES catalog.test_definitions (test_definition_id),
    analyte_ref_id varchar(36) NOT NULL,
    display_order integer
);

CREATE TABLE IF NOT EXISTS catalog.test_sample_requirement_links (
    link_id varchar(36) PRIMARY KEY,
    test_definition_id varchar(36) NOT NULL REFERENCES catalog.test_definitions (test_definition_id),
    sample_requirement_ref_id varchar(36) NOT NULL
);

-- BCM-SVC-003 Panel Catalog
CREATE TABLE IF NOT EXISTS catalog.panel_definitions (
    panel_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.panel_members (
    member_id varchar(36) PRIMARY KEY,
    panel_id varchar(36) NOT NULL REFERENCES catalog.panel_definitions (panel_id),
    test_ref_id varchar(36) NOT NULL,
    display_order integer,
    mandatory boolean NOT NULL
);

-- BCM-SVC-004 Analyte Catalog
CREATE TABLE IF NOT EXISTS catalog.analyte_definitions (
    analyte_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    loinc_code varchar(40),
    result_data_type varchar(30) NOT NULL,
    measurement_unit varchar(40),
    decimal_precision integer,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.analyte_result_constraints (
    constraint_id varchar(36) PRIMARY KEY,
    analyte_id varchar(36) NOT NULL UNIQUE REFERENCES catalog.analyte_definitions (analyte_id),
    min_value numeric(18,6),
    max_value numeric(18,6)
);

CREATE TABLE IF NOT EXISTS catalog.analyte_coded_values (
    coded_value_id varchar(36) PRIMARY KEY,
    analyte_id varchar(36) NOT NULL REFERENCES catalog.analyte_definitions (analyte_id),
    code varchar(80) NOT NULL,
    display_en varchar(240) NOT NULL,
    display_es varchar(240) NOT NULL
);

-- BCM-SVC-005 Patient Preparation Management
CREATE TABLE IF NOT EXISTS catalog.preparation_instructions (
    preparation_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    title_en varchar(240) NOT NULL,
    title_es varchar(240) NOT NULL,
    instruction_text_en text NOT NULL,
    instruction_text_es text NOT NULL,
    category varchar(20) NOT NULL,
    duration_hours integer,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.preparation_assignments (
    assignment_id varchar(36) PRIMARY KEY,
    preparation_id varchar(36) NOT NULL REFERENCES catalog.preparation_instructions (preparation_id),
    target_type varchar(20) NOT NULL,
    target_ref_id varchar(36) NOT NULL
);

-- BCM-SVC-006 Reference Range Management
CREATE TABLE IF NOT EXISTS catalog.reference_ranges (
    range_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    analyte_ref_id varchar(36) NOT NULL,
    version integer NOT NULL,
    status varchar(20) NOT NULL,
    effective_from date NOT NULL,
    effective_to date,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS catalog.reference_range_segments (
    segment_id varchar(36) PRIMARY KEY,
    range_id varchar(36) NOT NULL REFERENCES catalog.reference_ranges (range_id),
    sex varchar(10) NOT NULL,
    age_min_days integer,
    age_max_days integer,
    condition varchar(120),
    normal_low numeric(18,6),
    normal_high numeric(18,6),
    critical_low numeric(18,6),
    critical_high numeric(18,6),
    unit varchar(40)
);

-- BCM-SVC-007 Sample Catalog
CREATE TABLE IF NOT EXISTS catalog.sample_types (
    sample_type_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    matrix varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

-- Model gap: business-model.yaml ENT-SMP-002 does not declare tenant_id/laboratory_id on
-- SampleRequirement. Added here for tenant/laboratory scoping consistency (minimum compatible
-- option); see MVP-MOD-002-BE-001 QA evidence gap notes.
CREATE TABLE IF NOT EXISTS catalog.sample_requirements (
    requirement_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    sample_type_ref_id varchar(36) NOT NULL,
    min_volume_ml numeric(10,2),
    container_ref_id varchar(36),
    handling_instructions_en text,
    handling_instructions_es text,
    storage_temperature varchar(20),
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

-- BCM-SVC-009 Price List Management
CREATE TABLE IF NOT EXISTS catalog.price_lists (
    price_list_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    code varchar(80) NOT NULL,
    name_en varchar(240) NOT NULL,
    name_es varchar(240) NOT NULL,
    currency varchar(3) NOT NULL,
    agreement_ref_id varchar(36),
    effective_from date NOT NULL,
    effective_to date,
    status varchar(20) NOT NULL,
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (laboratory_id, code)
);

CREATE TABLE IF NOT EXISTS catalog.price_entries (
    entry_id varchar(36) PRIMARY KEY,
    price_list_id varchar(36) NOT NULL REFERENCES catalog.price_lists (price_list_id),
    item_type varchar(20) NOT NULL,
    item_ref_id varchar(36) NOT NULL,
    currency varchar(3) NOT NULL,
    amount numeric(18,2) NOT NULL
);

-- Enterprise foundation seed: minimal commercial diagnostic catalog for local MVP review.
-- The tenant/laboratory ids align with the local security fixture and may be remapped by
-- migration/import jobs for real customers.
INSERT INTO catalog.analyte_definitions (
    analyte_id, tenant_id, laboratory_id, code, name_en, name_es, loinc_code,
    result_data_type, measurement_unit, decimal_precision, status, version, created_at, updated_at
) VALUES
    ('seed-analyte-glucose', 'tenant-local', 'lab-local', 'GLU', 'Glucose', 'Glucosa',
     '2345-7', 'NUMERIC', 'mg/dL', 1, 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-analyte-hemoglobin', 'tenant-local', 'lab-local', 'HGB', 'Hemoglobin', 'Hemoglobina',
     '718-7', 'NUMERIC', 'g/dL', 1, 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-analyte-leukocytes', 'tenant-local', 'lab-local', 'WBC', 'Leukocytes', 'Leucocitos',
     '6690-2', 'NUMERIC', '10^3/uL', 2, 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (laboratory_id, code) DO NOTHING;

INSERT INTO catalog.sample_types (
    sample_type_id, tenant_id, laboratory_id, code, name_en, name_es, matrix, status,
    version, created_at, updated_at
) VALUES
    ('seed-sample-serum', 'tenant-local', 'lab-local', 'SERUM', 'Serum', 'Suero',
     'BLOOD', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-sample-whole-blood', 'tenant-local', 'lab-local', 'WHOLE_BLOOD', 'Whole blood',
     'Sangre total', 'BLOOD', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (laboratory_id, code) DO NOTHING;

INSERT INTO catalog.sample_requirements (
    requirement_id, tenant_id, laboratory_id, sample_type_ref_id, min_volume_ml, container_ref_id,
    handling_instructions_en, handling_instructions_es, storage_temperature, status, version,
    created_at, updated_at
) VALUES
    ('seed-req-serum-chemistry', 'tenant-local', 'lab-local', 'seed-sample-serum', 1.00,
     'SST', 'Centrifuge and separate serum within two hours.',
     'Centrifugar y separar suero dentro de dos horas.', 'ROOM', 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-req-edta-hematology', 'tenant-local', 'lab-local', 'seed-sample-whole-blood', 2.00,
     'EDTA', 'Mix gently after collection and avoid clotting.',
     'Mezclar suavemente despues de la toma y evitar coagulos.', 'ROOM', 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (requirement_id) DO NOTHING;

INSERT INTO catalog.test_definitions (
    test_definition_id, tenant_id, laboratory_id, code, name_en, name_es, methodology,
    measurement_unit, result_type, turnaround_time_hours, status, version, created_at, updated_at
) VALUES
    ('seed-test-glucose', 'tenant-local', 'lab-local', 'GLU_FASTING', 'Fasting glucose',
     'Glucosa en ayuno', 'Enzymatic colorimetric', 'mg/dL', 'NUMERIC', 4, 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-test-cbc', 'tenant-local', 'lab-local', 'CBC', 'Complete blood count',
     'Biometria hematica', 'Automated hematology', null, 'PANEL', 8, 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (laboratory_id, code) DO NOTHING;

INSERT INTO catalog.test_analyte_links (
    link_id, test_definition_id, analyte_ref_id, display_order
) VALUES
    ('seed-link-glucose', 'seed-test-glucose', 'seed-analyte-glucose', 1),
    ('seed-link-cbc-hgb', 'seed-test-cbc', 'seed-analyte-hemoglobin', 1),
    ('seed-link-cbc-wbc', 'seed-test-cbc', 'seed-analyte-leukocytes', 2)
ON CONFLICT (link_id) DO NOTHING;

INSERT INTO catalog.test_sample_requirement_links (
    link_id, test_definition_id, sample_requirement_ref_id
) VALUES
    ('seed-link-glucose-sample', 'seed-test-glucose', 'seed-req-serum-chemistry'),
    ('seed-link-cbc-sample', 'seed-test-cbc', 'seed-req-edta-hematology')
ON CONFLICT (link_id) DO NOTHING;

INSERT INTO catalog.diagnostic_services (
    service_id, tenant_id, laboratory_id, code, name_en, name_es, category_id, service_type,
    status, version, created_at, updated_at
) VALUES
    ('seed-service-glucose', 'tenant-local', 'lab-local', 'SVC_GLU_FASTING', 'Fasting glucose',
     'Glucosa en ayuno', 'chemistry', 'LAB_TEST', 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('seed-service-cbc', 'tenant-local', 'lab-local', 'SVC_CBC', 'Complete blood count',
     'Biometria hematica', 'hematology', 'LAB_TEST', 'PUBLISHED', 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (laboratory_id, code) DO NOTHING;

INSERT INTO catalog.diagnostic_service_component_links (
    link_id, service_id, component_type, component_ref_id, display_order
) VALUES
    ('seed-svc-link-glucose', 'seed-service-glucose', 'TEST', 'seed-test-glucose', 1),
    ('seed-svc-link-cbc', 'seed-service-cbc', 'TEST', 'seed-test-cbc', 1)
ON CONFLICT (link_id) DO NOTHING;
