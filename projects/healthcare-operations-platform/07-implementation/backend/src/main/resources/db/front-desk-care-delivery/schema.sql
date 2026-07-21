-- Generated/model-derived schema for MVP-MOD-004 Front Desk and Care Delivery. Source models:
-- 01-product-definition/business-capabilities/packages/bcm-att-001/003/004/006 and bcm-lab-001
-- business-model.yaml. Compiled for backlog item MVP-MOD-004-BE-001. BCM-LAB-001 owns the
-- DiagnosticOrder aggregate (AGG-007) in the orders-samples bounded context; BCM-ATT-001/003/004
-- orchestrate around it without owning it. BCM-ATT-006 owns a standalone QuotationRequest
-- aggregate in the cash-sales bounded context. Cross-context references (patient_id, doctor_id,
-- branch_id, test_definition_id, price_list_id) are plain columns without foreign keys, mirroring
-- the people-and-clinical-master-data schema convention: bounded contexts never join across
-- persistence schemas.

CREATE SCHEMA IF NOT EXISTS care_delivery;

-- BCM-LAB-001 DiagnosticOrder (AGG-007)
CREATE TABLE IF NOT EXISTS care_delivery.diagnostic_orders (
    order_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    intake_channel varchar(40) NOT NULL,
    source_reference_id varchar(36),
    patient_id varchar(36) NOT NULL,
    patient_snapshot_version integer NOT NULL,
    patient_full_name varchar(240),
    patient_document_type varchar(40),
    patient_document_number_masked varchar(80),
    patient_birth_date date,
    patient_captured_at timestamp with time zone NOT NULL,
    doctor_id varchar(36),
    doctor_snapshot_version integer,
    doctor_full_name varchar(240),
    doctor_license_number varchar(80),
    doctor_captured_at timestamp with time zone,
    branch_snapshot_version integer NOT NULL,
    branch_name varchar(180),
    branch_captured_at timestamp with time zone NOT NULL,
    clinical_notes text,
    price_list_id varchar(36),
    price_list_version integer,
    total_amount numeric(14,2),
    total_currency varchar(3),
    pricing_captured_at timestamp with time zone,
    status varchar(20) NOT NULL,
    cancellation_reason varchar(240),
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_diagnostic_orders_tenant ON care_delivery.diagnostic_orders (tenant_id);

CREATE TABLE IF NOT EXISTS care_delivery.diagnostic_order_lines (
    order_line_id varchar(36) PRIMARY KEY,
    order_id varchar(36) NOT NULL REFERENCES care_delivery.diagnostic_orders (order_id),
    test_definition_id varchar(36) NOT NULL,
    catalog_item_kind varchar(20) NOT NULL,
    catalog_item_name varchar(240),
    catalog_published_version integer NOT NULL,
    quantity integer NOT NULL,
    unit_amount numeric(14,2),
    unit_currency varchar(3),
    line_status varchar(20) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_diagnostic_order_lines_order ON care_delivery.diagnostic_order_lines (order_id);

-- BCM-ATT-001 AppointmentSlot process record.
-- COM-MOD-011-BE-001: patient_id is nullable for anonymous public_website channel (RN-008);
-- prospective_full_name/phone/email carry the reused BCM-ATT-006 ProspectiveContact shape.
CREATE TABLE IF NOT EXISTS care_delivery.appointments (
    appointment_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    patient_id varchar(36),
    doctor_id varchar(36),
    scheduled_start date NOT NULL,
    scheduled_end date NOT NULL,
    channel varchar(40) NOT NULL,
    status varchar(20) NOT NULL,
    linked_order_id varchar(36),
    cancellation_reason varchar(240),
    actor_id varchar(80),
    prospective_full_name varchar(200),
    prospective_phone varchar(80),
    prospective_email varchar(200),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

-- Additive columns / nullability relaxation for the RN-008 anonymous public-website path.
ALTER TABLE care_delivery.appointments
    ADD COLUMN IF NOT EXISTS prospective_full_name varchar(200);
ALTER TABLE care_delivery.appointments
    ADD COLUMN IF NOT EXISTS prospective_phone varchar(80);
ALTER TABLE care_delivery.appointments
    ADD COLUMN IF NOT EXISTS prospective_email varchar(200);
ALTER TABLE care_delivery.appointments
    ALTER COLUMN patient_id DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_appointments_patient_branch ON care_delivery.appointments (patient_id, branch_id);

CREATE TABLE IF NOT EXISTS care_delivery.appointment_requested_items (
    item_id varchar(36) PRIMARY KEY,
    appointment_id varchar(36) NOT NULL REFERENCES care_delivery.appointments (appointment_id),
    test_definition_id varchar(36) NOT NULL,
    catalog_item_kind varchar(20) NOT NULL
);

-- BCM-ATT-003 ReceptionVisit process record
CREATE TABLE IF NOT EXISTS care_delivery.reception_visits (
    visit_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    linked_appointment_id varchar(36),
    intake_channel varchar(20) NOT NULL,
    identity_confirmed boolean NOT NULL DEFAULT false,
    identity_confirmation_method varchar(40),
    queue_status varchar(20) NOT NULL,
    priority varchar(20) NOT NULL,
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reception_visits_tenant ON care_delivery.reception_visits (tenant_id);

-- BCM-ATT-004 AdmissionRequest process record
CREATE TABLE IF NOT EXISTS care_delivery.admission_requests (
    admission_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    visit_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    doctor_id varchar(36),
    clinical_notes_draft text,
    consent_confirmed boolean NOT NULL DEFAULT false,
    sample_requirements_acknowledged boolean NOT NULL DEFAULT false,
    admission_status varchar(20) NOT NULL,
    created_order_id varchar(36),
    rejection_reason varchar(240),
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_admission_requests_tenant ON care_delivery.admission_requests (tenant_id);

CREATE TABLE IF NOT EXISTS care_delivery.admission_catalog_selections (
    selection_id varchar(36) PRIMARY KEY,
    admission_id varchar(36) NOT NULL REFERENCES care_delivery.admission_requests (admission_id),
    test_definition_id varchar(36) NOT NULL,
    catalog_item_kind varchar(20) NOT NULL,
    quantity integer NOT NULL
);

-- BCM-ATT-006 QuotationRequest standalone process aggregate
CREATE TABLE IF NOT EXISTS care_delivery.quotations (
    quotation_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    patient_id varchar(36),
    prospective_full_name varchar(240),
    prospective_phone varchar(40),
    prospective_email varchar(254),
    price_list_id varchar(36),
    price_list_version integer,
    total_amount numeric(14,2),
    total_currency varchar(3),
    discount_kind varchar(20),
    discount_value numeric(14,2),
    valid_until date,
    status varchar(20) NOT NULL,
    converted_order_id varchar(36),
    cancellation_reason varchar(240),
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_quotations_tenant ON care_delivery.quotations (tenant_id);

CREATE TABLE IF NOT EXISTS care_delivery.quotation_lines (
    line_id varchar(36) PRIMARY KEY,
    quotation_id varchar(36) NOT NULL REFERENCES care_delivery.quotations (quotation_id),
    test_definition_id varchar(36) NOT NULL,
    catalog_item_kind varchar(20) NOT NULL,
    published_version integer NOT NULL,
    quantity integer NOT NULL,
    unit_amount numeric(14,2),
    unit_currency varchar(3)
);

CREATE INDEX IF NOT EXISTS idx_quotation_lines_quotation ON care_delivery.quotation_lines (quotation_id);
