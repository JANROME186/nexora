-- Generated/model-derived schema for MVP-MOD-008 Integration and Migration Readiness.
-- Source model: BCM-PLT-010 Open Data Ingestion and Migration (AGG-016 MigrationJob).

CREATE SCHEMA IF NOT EXISTS data_migration_portability;

CREATE TABLE IF NOT EXISTS data_migration_portability.migration_jobs (
    migration_job_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    source_system_name varchar(160) NOT NULL,
    status varchar(30) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_migration_jobs_tenant
    ON data_migration_portability.migration_jobs (tenant_id, laboratory_id);

CREATE TABLE IF NOT EXISTS data_migration_portability.import_batches (
    import_batch_id varchar(36) PRIMARY KEY,
    migration_job_id varchar(36) NOT NULL REFERENCES data_migration_portability.migration_jobs (migration_job_id),
    source_system_name varchar(160) NOT NULL,
    source_system_version varchar(80),
    exporting_organization varchar(200) NOT NULL,
    export_datetime timestamp with time zone NOT NULL,
    export_timezone varchar(60) NOT NULL,
    exported_by varchar(160) NOT NULL,
    contact_email varchar(200) NOT NULL,
    manifest_files_text text NOT NULL,
    manifest_entity_counts_text text NOT NULL,
    checksum_algorithm varchar(20) NOT NULL,
    manifest_checksums_text text NOT NULL,
    declared_formats_text text NOT NULL,
    declared_encoding varchar(20) NOT NULL,
    stored_package_reference varchar(160),
    entity_counts_text text,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_import_batches_job
    ON data_migration_portability.import_batches (migration_job_id);

CREATE TABLE IF NOT EXISTS data_migration_portability.mapping_templates (
    mapping_template_id varchar(36) PRIMARY KEY,
    import_batch_id varchar(36) NOT NULL REFERENCES data_migration_portability.import_batches (import_batch_id),
    field_mappings_text text,
    code_dictionaries_text text,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mapping_templates_batch
    ON data_migration_portability.mapping_templates (import_batch_id);

CREATE TABLE IF NOT EXISTS data_migration_portability.import_validation_reports (
    report_id varchar(36) PRIMARY KEY,
    import_batch_id varchar(36) NOT NULL REFERENCES data_migration_portability.import_batches (import_batch_id),
    structural_errors_text text,
    row_level_errors_text text,
    row_level_warnings_text text,
    validation_categories_text text,
    passed boolean NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_import_validation_reports_batch
    ON data_migration_portability.import_validation_reports (import_batch_id);

CREATE TABLE IF NOT EXISTS data_migration_portability.reconciliation_reports (
    reconciliation_report_id varchar(36) PRIMARY KEY,
    migration_job_id varchar(36) NOT NULL REFERENCES data_migration_portability.migration_jobs (migration_job_id),
    phase varchar(20) NOT NULL,
    imported_counts_text text,
    rejected_counts_text text,
    skipped_counts_text text,
    warning_counts_text text,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_reports_job
    ON data_migration_portability.reconciliation_reports (migration_job_id);

CREATE TABLE IF NOT EXISTS data_migration_portability.import_executions (
    execution_id varchar(36) PRIMARY KEY,
    migration_job_id varchar(36) NOT NULL REFERENCES data_migration_portability.migration_jobs (migration_job_id),
    import_batch_id varchar(36) NOT NULL,
    attempt_number integer NOT NULL,
    domain_commands_invoked_text text,
    checkpoint text,
    status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_import_executions_job
    ON data_migration_portability.import_executions (migration_job_id);
