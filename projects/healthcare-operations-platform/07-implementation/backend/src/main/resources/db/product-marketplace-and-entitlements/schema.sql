-- Generated/model-derived schema for COM-MOD-017 Product Marketplace and Extension Packaging.
-- Source model: BCM-PLT-011 Product Marketplace and Entitlements.

CREATE SCHEMA IF NOT EXISTS marketplace_entitlements;

CREATE TABLE IF NOT EXISTS marketplace_entitlements.marketplace_packages (
    package_id varchar(36) PRIMARY KEY,
    code varchar(160) NOT NULL,
    name varchar(200) NOT NULL,
    category varchar(60) NOT NULL,
    capability_mappings_text text NOT NULL,
    status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_marketplace_packages_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_marketplace_packages_status
    ON marketplace_entitlements.marketplace_packages (status);

CREATE TABLE IF NOT EXISTS marketplace_entitlements.package_versions (
    version_id varchar(36) PRIMARY KEY,
    package_id varchar(36) NOT NULL REFERENCES marketplace_entitlements.marketplace_packages (package_id),
    version varchar(30) NOT NULL,
    lifecycle_status varchar(20) NOT NULL,
    compatibility_approved boolean NOT NULL DEFAULT false,
    security_review_approved boolean NOT NULL DEFAULT false,
    support_model_approved boolean NOT NULL DEFAULT false,
    telemetry_model_approved boolean NOT NULL DEFAULT false,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_package_versions_package_version UNIQUE (package_id, version)
);

CREATE TABLE IF NOT EXISTS marketplace_entitlements.commercial_offers (
    offer_id varchar(36) PRIMARY KEY,
    package_id varchar(36) NOT NULL REFERENCES marketplace_entitlements.marketplace_packages (package_id),
    package_version varchar(30) NOT NULL,
    offer_code varchar(160) NOT NULL,
    offer_type varchar(30) NOT NULL,
    lifecycle_status varchar(20) NOT NULL,
    tier_codes_text text,
    trial_period_days integer,
    billing_event_rules_summary varchar(500),
    effective_version integer NOT NULL DEFAULT 1,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_commercial_offers_package
    ON marketplace_entitlements.commercial_offers (package_id);

CREATE TABLE IF NOT EXISTS marketplace_entitlements.tenant_entitlements (
    entitlement_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    package_id varchar(36) NOT NULL REFERENCES marketplace_entitlements.marketplace_packages (package_id),
    offer_id varchar(36) REFERENCES marketplace_entitlements.commercial_offers (offer_id),
    status varchar(20) NOT NULL,
    granted_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone,
    revoked_reason varchar(500),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tenant_entitlements_tenant_package
    ON marketplace_entitlements.tenant_entitlements (tenant_id, package_id);

CREATE TABLE IF NOT EXISTS marketplace_entitlements.package_installations (
    installation_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    package_id varchar(36) NOT NULL REFERENCES marketplace_entitlements.marketplace_packages (package_id),
    entitlement_id varchar(36) REFERENCES marketplace_entitlements.tenant_entitlements (entitlement_id),
    version varchar(30) NOT NULL,
    lifecycle_status varchar(20) NOT NULL,
    rollback_checkpoint_version varchar(30),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_package_installations_tenant_package
    ON marketplace_entitlements.package_installations (tenant_id, package_id);

CREATE TABLE IF NOT EXISTS marketplace_entitlements.billing_event_records (
    billing_event_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    entitlement_id varchar(36),
    event_type varchar(80) NOT NULL,
    amount_minor_units bigint NOT NULL DEFAULT 0,
    currency varchar(10),
    provider_reference varchar(160),
    adapter_status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_billing_event_records_tenant
    ON marketplace_entitlements.billing_event_records (tenant_id);
