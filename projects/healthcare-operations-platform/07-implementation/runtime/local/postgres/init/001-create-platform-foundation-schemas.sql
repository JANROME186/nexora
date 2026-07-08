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
