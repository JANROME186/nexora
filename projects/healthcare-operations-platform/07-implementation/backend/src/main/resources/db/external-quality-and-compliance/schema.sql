CREATE TABLE IF NOT EXISTS external_quality_evaluations (
    evaluation_id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    program_code VARCHAR(100) NOT NULL,
    survey_cycle VARCHAR(100),
    test_definition_id VARCHAR(36) NOT NULL,
    sample_code VARCHAR(100) NOT NULL,
    measured_value DOUBLE PRECISION NOT NULL,
    peer_group_mean DOUBLE PRECISION,
    peer_group_sd DOUBLE PRECISION,
    peer_group_count INTEGER,
    z_score DOUBLE PRECISION,
    performance_rating VARCHAR(50) NOT NULL,
    capa_investigation_id VARCHAR(36),
    stored_document_id VARCHAR(36),
    evaluated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS capa_investigations (
    capa_id VARCHAR(36) PRIMARY KEY,
    capa_number VARCHAR(100) NOT NULL UNIQUE,
    tenant_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_category VARCHAR(100) NOT NULL,
    source_reference_id VARCHAR(100),
    assigned_investigator_id VARCHAR(36) NOT NULL,
    target_completion_date DATE,
    status VARCHAR(50) NOT NULL,
    root_cause_methodology VARCHAR(100),
    root_cause_summary TEXT,
    effectiveness_rating VARCHAR(50),
    closure_notes TEXT,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_schedules (
    audit_id VARCHAR(36) PRIMARY KEY,
    audit_code VARCHAR(100) NOT NULL UNIQUE,
    tenant_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    standard_reference VARCHAR(100),
    lead_auditor_id VARCHAR(36) NOT NULL,
    planned_start_date DATE,
    planned_end_date DATE,
    status VARCHAR(50) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_findings (
    finding_id VARCHAR(36) PRIMARY KEY,
    audit_id VARCHAR(36) REFERENCES audit_schedules(audit_id) ON DELETE CASCADE,
    clause_reference VARCHAR(100),
    severity VARCHAR(50) NOT NULL,
    observation TEXT NOT NULL,
    evidence_reference TEXT,
    capa_id VARCHAR(36),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS quality_event_intakes (
    event_id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    payload_json TEXT,
    capa_id VARCHAR(36),
    ingested_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
