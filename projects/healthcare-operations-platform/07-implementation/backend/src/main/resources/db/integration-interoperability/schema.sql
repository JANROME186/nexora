-- Generated/model-derived schema for MVP-MOD-008 Integration and Migration Readiness.
-- Source models: BCM-PLT-004 Integration Management and BCM-PLT-005 API Management.

CREATE SCHEMA IF NOT EXISTS integration_interoperability;

CREATE TABLE IF NOT EXISTS integration_interoperability.integration_endpoints (
    endpoint_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    endpoint_name varchar(160) NOT NULL,
    protocol varchar(30) NOT NULL,
    direction varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_integration_endpoints_tenant
    ON integration_interoperability.integration_endpoints (tenant_id, laboratory_id);

CREATE TABLE IF NOT EXISTS integration_interoperability.integration_message_records (
    message_id varchar(36) PRIMARY KEY,
    endpoint_id varchar(36) NOT NULL REFERENCES integration_interoperability.integration_endpoints (endpoint_id),
    external_message_id varchar(160) NOT NULL,
    correlation_id varchar(80),
    source_protocol varchar(30) NOT NULL,
    raw_payload_reference varchar(160) NOT NULL,
    received_at timestamp with time zone NOT NULL,
    message_type varchar(80),
    canonical_fields_text text,
    target_bounded_context varchar(80),
    normalization_status varchar(30) NOT NULL,
    canonical_error_code varchar(80),
    retry_count integer NOT NULL DEFAULT 0,
    next_retry_at timestamp with time zone,
    dead_letter_reason varchar(500),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_integration_message_endpoint_external_id UNIQUE (endpoint_id, external_message_id)
);

CREATE INDEX IF NOT EXISTS idx_integration_messages_endpoint
    ON integration_interoperability.integration_message_records (endpoint_id);

CREATE TABLE IF NOT EXISTS integration_interoperability.api_surface_registrations (
    registration_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36),
    owner_capability varchar(40) NOT NULL,
    operation_id varchar(160) NOT NULL,
    classification varchar(20) NOT NULL,
    api_version varchar(20) NOT NULL,
    deprecation_status varchar(30) NOT NULL,
    deprecation_window_from timestamp with time zone,
    deprecation_window_to timestamp with time zone,
    migration_note varchar(500),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_api_surface_operation UNIQUE (operation_id)
);

CREATE TABLE IF NOT EXISTS integration_interoperability.partner_api_keys (
    key_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    consumer_name varchar(160) NOT NULL,
    granted_scopes_text text NOT NULL,
    rate_limit_policy_ref varchar(36),
    status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_partner_api_keys_tenant
    ON integration_interoperability.partner_api_keys (tenant_id);

CREATE TABLE IF NOT EXISTS integration_interoperability.rate_limit_policies (
    policy_id varchar(36) PRIMARY KEY,
    classification varchar(20) NOT NULL,
    requests_per_minute integer NOT NULL,
    consumer_identification_method varchar(32) NOT NULL DEFAULT 'partner_api_key',
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_rate_limit_policy_classification UNIQUE (classification)
);

-- COM-MOD-011-BE-001: additive column for RN-007 consumer identification method. Added with a
-- default so pre-existing rows keep the partner-key behavior TD-BE-015 previously scoped to.
ALTER TABLE integration_interoperability.rate_limit_policies
    ADD COLUMN IF NOT EXISTS consumer_identification_method varchar(32) NOT NULL DEFAULT 'partner_api_key';
