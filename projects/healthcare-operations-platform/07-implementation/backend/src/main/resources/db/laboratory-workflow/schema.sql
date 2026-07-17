-- Generated/model-derived schema for MVP-MOD-006 Laboratory Workflow.
-- Source models: BCM-LAB-002, BCM-LAB-003, BCM-LAB-005, BCM-LAB-006, BCM-LAB-008, BCM-LAB-009, BCM-LAB-010.

CREATE SCHEMA IF NOT EXISTS orders_samples;

CREATE TABLE IF NOT EXISTS orders_samples.samples (
    sample_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    order_id varchar(36) NOT NULL,
    order_line_id varchar(36) NOT NULL,
    
    -- PatientIdentitySnapshot
    patient_id varchar(36) NOT NULL,
    patient_name varchar(240) NOT NULL,
    patient_birth_date varchar(20) NOT NULL,
    patient_snapshot_captured_at timestamp with time zone NOT NULL,
    
    -- SampleRequirementSnapshot
    requirement_id varchar(36) NOT NULL,
    requirement_version integer NOT NULL,
    container_type varchar(80) NOT NULL,
    minimum_volume varchar(40),
    handling_instructions varchar(500),
    requirement_captured_at timestamp with time zone NOT NULL,
    
    -- SampleCollectionData
    collector_id varchar(80) NOT NULL,
    collection_site varchar(120),
    collection_method varchar(40) NOT NULL,
    container_used varchar(80) NOT NULL,
    collected_at timestamp with time zone NOT NULL,
    patient_condition varchar(40),
    
    -- SpecimenLabelInfo (BCM-LAB-003)
    label_id varchar(36),
    barcode_value varchar(80),
    printed_at timestamp with time zone,
    
    -- SampleReceptionRecord (BCM-LAB-005)
    received_by varchar(80),
    received_at timestamp with time zone,
    condition_at_reception varchar(40),
    
    -- SampleRejectionReason
    rejected_by varchar(80),
    rejected_at timestamp with time zone,
    rejection_stage varchar(40),
    rejection_reason_code varchar(40),
    rejection_notes varchar(500),
    
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_samples_tenant ON orders_samples.samples (tenant_id);
CREATE INDEX IF NOT EXISTS idx_samples_order ON orders_samples.samples (order_id);

CREATE TABLE IF NOT EXISTS orders_samples.chain_of_custody (
    custody_id SERIAL PRIMARY KEY,
    sample_id varchar(36) NOT NULL REFERENCES orders_samples.samples (sample_id),
    event_type varchar(40) NOT NULL,
    actor_id varchar(80) NOT NULL,
    occurred_at timestamp with time zone NOT NULL,
    location_branch_id varchar(36) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_chain_of_custody_sample ON orders_samples.chain_of_custody (sample_id);


CREATE SCHEMA IF NOT EXISTS laboratory_results;

CREATE TABLE IF NOT EXISTS laboratory_results.results (
    result_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    order_id varchar(36) NOT NULL,
    sample_id varchar(36) NOT NULL,
    
    -- AnalyteSnapshot
    test_definition_id varchar(36) NOT NULL,
    analyte_id varchar(36) NOT NULL,
    analyte_version integer NOT NULL,
    analyte_name varchar(240) NOT NULL,
    analyte_unit varchar(40) NOT NULL,
    analyte_method varchar(120),
    analyte_snapshot_at timestamp with time zone NOT NULL,
    
    -- ReferenceRangeSnapshot
    range_id varchar(36) NOT NULL,
    range_version integer NOT NULL,
    range_low varchar(40),
    range_high varchar(40),
    range_critical_low varchar(40),
    range_critical_high varchar(40),
    range_snapshot_at timestamp with time zone NOT NULL,
    
    -- ResultValue
    raw_value varchar(240) NOT NULL,
    numeric_value numeric(18,6),
    unit varchar(40) NOT NULL,
    method varchar(120),
    captured_at timestamp with time zone NOT NULL,
    captured_by varchar(80),
    device_reference varchar(120),
    capture_source varchar(40) NOT NULL,
    
    -- TechnicalValidationRecord (BCM-LAB-008)
    tech_validated_by varchar(80),
    tech_validated_at timestamp with time zone,
    
    -- CriticalResultFlag (BCM-LAB-008)
    critical_flagged_by varchar(80),
    critical_flagged_at timestamp with time zone,
    critical_reason varchar(240),
    
    -- MedicalValidationRecord (BCM-LAB-009)
    med_validated_by varchar(80),
    med_validated_at timestamp with time zone,
    
    -- ResultReleaseRecord (BCM-LAB-010)
    released_by varchar(80),
    released_at timestamp with time zone,
    
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_results_tenant ON laboratory_results.results (tenant_id);
CREATE INDEX IF NOT EXISTS idx_results_sample ON laboratory_results.results (sample_id);

CREATE TABLE IF NOT EXISTS laboratory_results.processing_incidents (
    incident_id SERIAL PRIMARY KEY,
    result_id varchar(36) NOT NULL REFERENCES laboratory_results.results (result_id),
    incident_type varchar(40) NOT NULL,
    notes varchar(500),
    recorded_by varchar(80) NOT NULL,
    recorded_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_processing_incidents_result ON laboratory_results.processing_incidents (result_id);
