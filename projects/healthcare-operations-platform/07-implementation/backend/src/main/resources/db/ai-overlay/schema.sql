-- Schema definition for COM-MOD-015 AI Overlay
-- Business Capability Packages: BCM-AI-001, BCM-AI-006 and BCM-AI-008 compiled in COM-MOD-015-BE-001

CREATE SCHEMA IF NOT EXISTS ai_overlay;

CREATE TABLE IF NOT EXISTS ai_overlay.ai_interactions (
    session_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    actor_id varchar(80) NOT NULL,
    purpose varchar(120) NOT NULL,
    source_context_type varchar(80) NOT NULL,
    source_context_id varchar(120) NOT NULL,
    user_prompt text NOT NULL,
    draft_output text NOT NULL,
    citations_text text NOT NULL,
    confidence_band varchar(30) NOT NULL,
    safety_decision varchar(60) NOT NULL,
    review_status varchar(60) NOT NULL,
    reviewer_id varchar(80),
    review_reason text,
    model_provider_ref varchar(120) NOT NULL,
    model_name_ref varchar(120) NOT NULL,
    policy_version varchar(60) NOT NULL,
    lifecycle_status varchar(60) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ai_interactions_tenant_created
    ON ai_overlay.ai_interactions (tenant_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_ai_interactions_source_context
    ON ai_overlay.ai_interactions (tenant_id, source_context_type, source_context_id);
