-- COM-MOD-012-BE-001 (BCM-PLT-002 Platform Configuration and Feature Flags).
CREATE SCHEMA IF NOT EXISTS platform_configuration;

CREATE TABLE IF NOT EXISTS platform_configuration.config_parameters (
    config_key varchar(160) PRIMARY KEY,
    value_type varchar(20) NOT NULL,
    raw_value varchar(2000) NOT NULL,
    tenant_override_allowed boolean NOT NULL DEFAULT false,
    is_encrypted boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS platform_configuration.feature_flags (
    flag_key varchar(160) PRIMARY KEY,
    enabled_by_default boolean NOT NULL DEFAULT false,
    target_tenants varchar(2000) NOT NULL DEFAULT '',
    rollout_percentage integer NOT NULL DEFAULT 0,
    updated_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL
);

-- Baseline operational configuration parameters (business-model.yaml ConfigKeyRoot); the session
-- timeout is tenant-overridable, the maintenance-mode switch is platform-global only.
INSERT INTO platform_configuration.config_parameters
    (config_key, value_type, raw_value, tenant_override_allowed, is_encrypted) VALUES
    ('platform.security.session_timeout_minutes', 'INTEGER', '30', true, false),
    ('platform.operations.maintenance_mode', 'BOOLEAN', 'false', false, false)
ON CONFLICT (config_key) DO NOTHING;
