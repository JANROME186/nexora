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
