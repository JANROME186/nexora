CREATE SCHEMA IF NOT EXISTS organization;
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS organization.tenants (
    tenant_id varchar(36) PRIMARY KEY,
    name varchar(180) NOT NULL,
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

-- COM-MOD-012-BE-001 (BCM-ORG-001 TenantRoot): tenant provisioning/lifecycle fields. The
-- pre-existing "name" column is dropped in favor of legal_name/trade_name once every remaining
-- caller has migrated to the richer provisionTenant model; this is pre-GA iterative schema
-- evolution (no production tenant data exists yet), consistent with the incremental ALTER
-- statements already used for identity.user_accounts.
ALTER TABLE organization.tenants DROP COLUMN IF EXISTS name;
ALTER TABLE organization.tenants ADD COLUMN IF NOT EXISTS code varchar(60);
ALTER TABLE organization.tenants ADD COLUMN IF NOT EXISTS legal_name varchar(180);
ALTER TABLE organization.tenants ADD COLUMN IF NOT EXISTS trade_name varchar(180);
ALTER TABLE organization.tenants ADD COLUMN IF NOT EXISTS tax_id varchar(60);
ALTER TABLE organization.tenants ADD COLUMN IF NOT EXISTS tier varchar(40) NOT NULL DEFAULT 'STARTER';
ALTER TABLE organization.tenants
    ADD COLUMN IF NOT EXISTS isolation_strategy varchar(40) NOT NULL DEFAULT 'DISCRIMINATOR_WITH_RLS';
UPDATE organization.tenants SET legal_name = tenant_id WHERE legal_name IS NULL;
UPDATE organization.tenants SET code = tenant_id WHERE code IS NULL;
ALTER TABLE organization.tenants ALTER COLUMN code SET NOT NULL;
ALTER TABLE organization.tenants ALTER COLUMN legal_name SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS tenants_code_key ON organization.tenants (code);

CREATE TABLE IF NOT EXISTS organization.laboratories (
    laboratory_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL REFERENCES organization.tenants (tenant_id),
    name varchar(180) NOT NULL,
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS organization.branches (
    branch_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL REFERENCES organization.tenants (tenant_id),
    laboratory_id varchar(36) NOT NULL REFERENCES organization.laboratories (laboratory_id),
    name varchar(180) NOT NULL,
    status varchar(40) NOT NULL,
    version integer NOT NULL DEFAULT 1,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS identity.user_accounts (
    user_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL REFERENCES organization.tenants (tenant_id),
    display_name varchar(180) NOT NULL,
    email varchar(254) NOT NULL,
    status varchar(40) NOT NULL,
    username varchar(180) NOT NULL DEFAULT '',
    password_hash varchar(255) NOT NULL DEFAULT '',
    failed_login_attempts integer NOT NULL DEFAULT 0,
    locked_until timestamp with time zone,
    last_login_at timestamp with time zone,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

ALTER TABLE identity.user_accounts ADD COLUMN IF NOT EXISTS username varchar(180) NOT NULL DEFAULT '';
ALTER TABLE identity.user_accounts ADD COLUMN IF NOT EXISTS password_hash varchar(255) NOT NULL DEFAULT '';
ALTER TABLE identity.user_accounts ADD COLUMN IF NOT EXISTS failed_login_attempts integer NOT NULL DEFAULT 0;
ALTER TABLE identity.user_accounts ADD COLUMN IF NOT EXISTS locked_until timestamp with time zone;
ALTER TABLE identity.user_accounts ADD COLUMN IF NOT EXISTS last_login_at timestamp with time zone;

CREATE TABLE IF NOT EXISTS identity.role_assignments (
    role_assignment_id varchar(36) PRIMARY KEY,
    user_id varchar(36) NOT NULL REFERENCES identity.user_accounts (user_id),
    role_code varchar(80) NOT NULL,
    scope_type varchar(40) NOT NULL,
    scope_id varchar(36) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS audit.audit_events (
    audit_event_id varchar(36) PRIMARY KEY,
    occurred_at timestamp with time zone NOT NULL,
    tenant_id varchar(36),
    actor_id varchar(80) NOT NULL,
    actor_type varchar(40) NOT NULL,
    action varchar(120) NOT NULL,
    subject_type varchar(80) NOT NULL,
    subject_id varchar(80) NOT NULL,
    metadata_json jsonb NOT NULL
);

-- HOP-ENT-FOUND-001: country/locale/currency reference catalogs (baseline MX/US, es-MX/en-US,
-- MXN/USD). See 03-architecture/data-architecture/database-architecture.md and
-- seed-data-catalog.md. Parallel name_es_mx/name_en_us columns are a deliberate, size-appropriate
-- denormalization for these small, static reference tables (see normalization-report.md).
CREATE TABLE IF NOT EXISTS organization.countries (
    country_code varchar(2) PRIMARY KEY,
    name_es_mx varchar(120) NOT NULL,
    name_en_us varchar(120) NOT NULL,
    status varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS organization.locales (
    locale_code varchar(10) PRIMARY KEY,
    name_es_mx varchar(120) NOT NULL,
    name_en_us varchar(120) NOT NULL,
    is_default boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS organization.currencies (
    currency_code varchar(3) PRIMARY KEY,
    name_es_mx varchar(120) NOT NULL,
    name_en_us varchar(120) NOT NULL,
    minor_unit_digits smallint NOT NULL
);

INSERT INTO organization.countries (country_code, name_es_mx, name_en_us, status) VALUES
    ('MX', 'México', 'Mexico', 'active'),
    ('US', 'Estados Unidos', 'United States', 'active')
ON CONFLICT (country_code) DO NOTHING;

INSERT INTO organization.locales (locale_code, name_es_mx, name_en_us, is_default) VALUES
    ('es-MX', 'Español (México)', 'Spanish (Mexico)', true),
    ('en-US', 'Inglés (Estados Unidos)', 'English (United States)', false)
ON CONFLICT (locale_code) DO NOTHING;

INSERT INTO organization.currencies (currency_code, name_es_mx, name_en_us, minor_unit_digits) VALUES
    ('MXN', 'Peso mexicano', 'Mexican Peso', 2),
    ('USD', 'Dólar estadounidense', 'US Dollar', 2)
ON CONFLICT (currency_code) DO NOTHING;
