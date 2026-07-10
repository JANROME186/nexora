-- Generated/model-derived schema for MVP-MOD-003 People and Clinical Master Data. Source models:
-- 01-product-definition/business-capabilities/packages/bcm-per-001/002/003 and bcm-att-002
-- business-model.yaml. Compiled for backlog item MVP-MOD-003-BE-001. Owning bounded contexts
-- patient-management (AGG-001 Patient) and medical-staff (AGG-005 Doctor) share one Postgres
-- schema {people} for MVP compilation. Table-per-aggregate boundaries stay intact.

CREATE SCHEMA IF NOT EXISTS people;

-- BCM-PER-002 Patient (AGG-001)
CREATE TABLE IF NOT EXISTS people.patients (
    patient_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    patient_code varchar(80) NOT NULL,
    given_name varchar(120) NOT NULL,
    middle_name varchar(120),
    family_name varchar(120) NOT NULL,
    second_family_name varchar(120),
    preferred_name varchar(120),
    normalized_given_name varchar(120),
    normalized_family_name varchar(120),
    birth_date date,
    sex_at_birth varchar(20),
    primary_document_type varchar(40),
    primary_document_number varchar(80),
    primary_document_issuing_country varchar(3),
    primary_document_issued_at date,
    primary_document_expires_at date,
    address_country varchar(3),
    address_state varchar(120),
    address_city varchar(120),
    address_postal_code varchar(20),
    address_street varchar(240),
    preferred_locale varchar(20),
    status varchar(20) NOT NULL,
    merged_into_patient_id varchar(36),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (tenant_id, patient_code)
);

CREATE INDEX IF NOT EXISTS idx_patients_normalized_natural_key
    ON people.patients (tenant_id, normalized_family_name, normalized_given_name);

CREATE TABLE IF NOT EXISTS people.patient_representatives (
    representative_id varchar(36) PRIMARY KEY,
    patient_id varchar(36) NOT NULL REFERENCES people.patients (patient_id),
    relationship varchar(40) NOT NULL,
    given_name varchar(120) NOT NULL,
    middle_name varchar(120),
    family_name varchar(120) NOT NULL,
    second_family_name varchar(120),
    document_type varchar(40) NOT NULL,
    document_number varchar(80) NOT NULL,
    authorization_from date NOT NULL,
    authorization_to date,
    status varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS people.patient_consents (
    consent_id varchar(36) PRIMARY KEY,
    patient_id varchar(36) NOT NULL REFERENCES people.patients (patient_id),
    consent_type varchar(40) NOT NULL,
    granted boolean NOT NULL,
    granted_by varchar(40) NOT NULL,
    granted_at timestamp with time zone NOT NULL,
    revoked_at timestamp with time zone,
    evidence_reference varchar(240)
);

CREATE TABLE IF NOT EXISTS people.patient_documents (
    document_id varchar(36) PRIMARY KEY,
    patient_id varchar(36) NOT NULL REFERENCES people.patients (patient_id),
    category varchar(40) NOT NULL,
    file_reference varchar(1024) NOT NULL,
    uploaded_at timestamp with time zone NOT NULL,
    expires_at date
);

CREATE TABLE IF NOT EXISTS people.patient_emergency_contacts (
    emergency_contact_id varchar(36) PRIMARY KEY,
    patient_id varchar(36) NOT NULL REFERENCES people.patients (patient_id),
    relationship varchar(80) NOT NULL,
    given_name varchar(120) NOT NULL,
    middle_name varchar(120),
    family_name varchar(120) NOT NULL,
    second_family_name varchar(120),
    phone_country_code varchar(8) NOT NULL,
    phone_national_number varchar(40) NOT NULL,
    preferred boolean NOT NULL DEFAULT false
);

-- BCM-PER-003 Doctor (AGG-005)
CREATE TABLE IF NOT EXISTS people.doctors (
    doctor_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    doctor_code varchar(80) NOT NULL,
    given_name varchar(120) NOT NULL,
    middle_name varchar(120),
    family_name varchar(120) NOT NULL,
    second_family_name varchar(120),
    normalized_given_name varchar(120),
    normalized_family_name varchar(120),
    primary_document_type varchar(40),
    primary_document_number varchar(80),
    primary_document_issuing_country varchar(3),
    primary_document_issued_at date,
    primary_document_expires_at date,
    address_country varchar(3),
    address_city varchar(120),
    address_street varchar(240),
    doctor_type varchar(40) NOT NULL,
    status varchar(20) NOT NULL,
    portal_status varchar(40),
    portal_email varchar(254),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    UNIQUE (tenant_id, doctor_code)
);

CREATE INDEX IF NOT EXISTS idx_doctors_normalized_natural_key
    ON people.doctors (tenant_id, normalized_family_name, normalized_given_name);

CREATE TABLE IF NOT EXISTS people.doctor_credentials (
    credential_id varchar(36) PRIMARY KEY,
    doctor_id varchar(36) NOT NULL REFERENCES people.doctors (doctor_id),
    credential_type varchar(40) NOT NULL,
    credential_number varchar(120) NOT NULL,
    issuing_authority varchar(240) NOT NULL,
    issuing_country varchar(3),
    issued_at date NOT NULL,
    expires_at date,
    verification_status varchar(20) NOT NULL,
    verified_at timestamp with time zone
);

CREATE TABLE IF NOT EXISTS people.doctor_specialty_assignments (
    assignment_id varchar(36) PRIMARY KEY,
    doctor_id varchar(36) NOT NULL REFERENCES people.doctors (doctor_id),
    specialty_code varchar(80) NOT NULL,
    primary_flag boolean NOT NULL DEFAULT false
);

-- BCM-PER-001 Person merge coordination (cross-context, not an owning aggregate)
CREATE TABLE IF NOT EXISTS people.person_merge_coordinations (
    coordination_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    source_kind varchar(20) NOT NULL,
    source_record_id varchar(36) NOT NULL,
    target_kind varchar(20) NOT NULL,
    target_record_id varchar(36) NOT NULL,
    status varchar(40) NOT NULL,
    patient_merge_applied boolean NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

-- BCM-ATT-002 Patient Registration process record
CREATE TABLE IF NOT EXISTS people.patient_registrations (
    registration_request_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    intake_channel varchar(40) NOT NULL,
    candidate_patient_id varchar(36),
    registration_kind varchar(40) NOT NULL,
    normalized_family_name varchar(120),
    normalized_given_name varchar(120),
    birth_date date,
    draft_given_name varchar(120),
    draft_family_name varchar(120),
    draft_document_type varchar(40),
    draft_document_number varchar(80),
    draft_patient_code varchar(80),
    outcome varchar(20) NOT NULL,
    outcome_patient_id varchar(36),
    actor_id varchar(80),
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);
