CREATE SCHEMA IF NOT EXISTS results_and_digital_delivery;
CREATE SCHEMA IF NOT EXISTS document_management;
CREATE SCHEMA IF NOT EXISTS notification_management;

-- Document Management
CREATE TABLE IF NOT EXISTS document_management.stored_documents (
    document_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    owner_capability varchar(120),
    owner_reference_id varchar(36),
    document_version integer NOT NULL,
    content_type varchar(80) NOT NULL,
    content_hash varchar(255) NOT NULL,
    size_bytes integer NOT NULL,
    storage_provider varchar(80) NOT NULL,
    storage_key varchar(255) NOT NULL,
    stored_at timestamp with time zone,
    retention_policy_type varchar(40),
    retention_period_days integer,
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

-- Results and Digital Delivery
CREATE TABLE IF NOT EXISTS results_and_digital_delivery.generated_result_reports (
    report_id varchar(36) PRIMARY KEY,
    result_id varchar(36) NOT NULL,
    tenant_id varchar(36) NOT NULL,
    stored_document_id varchar(36),
    integrity_checksum varchar(255),
    version integer NOT NULL,
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS results_and_digital_delivery.result_delivery_tickets (
    ticket_id varchar(36) PRIMARY KEY,
    result_id varchar(36) NOT NULL,
    tenant_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    access_code varchar(80),
    status varchar(40) NOT NULL,
    expires_at timestamp with time zone,
    recipient_type varchar(80),
    recipient_id varchar(80),
    delivery_channel varchar(80),
    authorization_check_type varchar(40),
    authorization_check_value varchar(255),
    delivered_at timestamp with time zone,
    viewed_at timestamp with time zone,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS results_and_digital_delivery.critical_result_escalations (
    escalation_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    result_id varchar(36) NOT NULL,
    critical_reason varchar(512),
    assigned_handler_id varchar(36),
    escalation_tier integer NOT NULL,
    acknowledgement_deadline timestamp with time zone,
    acknowledged_by varchar(36),
    acknowledged_at timestamp with time zone,
    status varchar(40) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS results_and_digital_delivery.result_notification_requests (
    result_notification_id varchar(36) PRIMARY KEY,
    result_id varchar(36) NOT NULL,
    tenant_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    underlying_notification_id varchar(36),
    recipient_id varchar(80),
    recipient_type varchar(80),
    trigger_reason varchar(120),
    composed_template_reference varchar(255),
    dispatch_reference varchar(36),
    dispatch_status varchar(80),
    channel varchar(40),
    dispatched_at timestamp with time zone,
    delivered_at timestamp with time zone,
    failure_reason varchar(512),
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS results_and_digital_delivery.patient_result_history_entries (
    entry_id SERIAL PRIMARY KEY,
    patient_id varchar(36) NOT NULL,
    result_id varchar(36) NOT NULL,
    analyte_name varchar(180) NOT NULL,
    string_value varchar(255),
    reference_range varchar(255),
    is_abnormal boolean NOT NULL,
    released_at timestamp with time zone NOT NULL
);

-- Notification Management
CREATE TABLE IF NOT EXISTS notification_management.notification_requests (
    notification_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    recipient_address varchar(255) NOT NULL,
    channel varchar(40) NOT NULL,
    subject varchar(255),
    content text,
    status varchar(40) NOT NULL,
    requested_at timestamp with time zone,
    dispatched_at timestamp with time zone,
    created_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL
);
