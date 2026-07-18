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
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

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
-- MXN/USD). See 03-architecture/data-architecture/database-architecture.yaml and
-- seed-data-catalog.yaml. Parallel name_es_mx/name_en_us columns are a deliberate, size-appropriate
-- denormalization for these small, static reference tables (see normalization-report.yaml).
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
