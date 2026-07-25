-- Schema definition for COM-MOD-014 Imaging Operations
-- Business Capability Packages: BCM-IMG-001 through BCM-IMG-008

CREATE SCHEMA IF NOT EXISTS imaging_operations;

-- AGG-031: ImagingAppointmentSlot
CREATE TABLE IF NOT EXISTS imaging_operations.imaging_appointment_slots (
    slot_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    modality varchar(30) NOT NULL,
    procedure_code varchar(60) NOT NULL,
    procedure_room_id varchar(36) NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone NOT NULL,
    duration_minutes integer NOT NULL,
    slot_status varchar(30) NOT NULL,
    notes text,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_img_app_slots_tenant_patient
    ON imaging_operations.imaging_appointment_slots (tenant_id, patient_id);

CREATE INDEX IF NOT EXISTS idx_img_app_slots_room_time
    ON imaging_operations.imaging_appointment_slots (procedure_room_id, start_time, end_time);

-- AGG-032: ImagingReceptionIntake
CREATE TABLE IF NOT EXISTS imaging_operations.imaging_reception_intakes (
    intake_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    appointment_slot_id varchar(36) NOT NULL REFERENCES imaging_operations.imaging_appointment_slots(slot_id),
    patient_id varchar(36) NOT NULL,
    intake_time timestamp with time zone NOT NULL,
    check_in_status varchar(30) NOT NULL,
    preparation_verified boolean NOT NULL DEFAULT false,
    intake_notes text,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_img_reception_tenant_slot
    ON imaging_operations.imaging_reception_intakes (tenant_id, appointment_slot_id);

-- AGG-033: ImagingStudy
CREATE TABLE IF NOT EXISTS imaging_operations.imaging_studies (
    study_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    accession_number varchar(80) NOT NULL,
    patient_id varchar(36) NOT NULL,
    modality varchar(30) NOT NULL,
    study_description varchar(255) NOT NULL,
    study_status varchar(30) NOT NULL,
    series_count integer NOT NULL DEFAULT 0,
    instance_count integer NOT NULL DEFAULT 0,
    study_date timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_img_studies_accession UNIQUE (tenant_id, accession_number)
);

CREATE INDEX IF NOT EXISTS idx_img_studies_tenant_patient
    ON imaging_operations.imaging_studies (tenant_id, patient_id);

-- AGG-034: DicomAdapterConfiguration
CREATE TABLE IF NOT EXISTS imaging_operations.dicom_adapter_configurations (
    configuration_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    ae_title varchar(80) NOT NULL,
    host varchar(120) NOT NULL,
    port integer NOT NULL,
    modality_type varchar(30) NOT NULL,
    connection_status varchar(30) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_dicom_config_ae_title UNIQUE (tenant_id, ae_title)
);

-- AGG-035: PacsIntegrationEndpoint
CREATE TABLE IF NOT EXISTS imaging_operations.pacs_integration_endpoints (
    endpoint_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    pacs_node_id varchar(80) NOT NULL,
    base_url varchar(255) NOT NULL,
    protocol varchar(30) NOT NULL,
    status varchar(30) NOT NULL,
    auth_credentials_masked varchar(100),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_pacs_endpoint_node UNIQUE (tenant_id, pacs_node_id)
);

-- AGG-036: RadiologyDictation
CREATE TABLE IF NOT EXISTS imaging_operations.radiology_dictations (
    dictation_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    study_id varchar(36) NOT NULL REFERENCES imaging_operations.imaging_studies(study_id),
    radiologist_id varchar(36) NOT NULL,
    dictation_text text,
    audio_reference_url varchar(255),
    dictation_status varchar(30) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rad_dictations_study
    ON imaging_operations.radiology_dictations (study_id);

-- AGG-037: RadiologyReport
CREATE TABLE IF NOT EXISTS imaging_operations.radiology_reports (
    report_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    study_id varchar(36) NOT NULL REFERENCES imaging_operations.imaging_studies(study_id),
    radiologist_id varchar(36) NOT NULL,
    findings_text text NOT NULL,
    impression_text text NOT NULL,
    report_status varchar(30) NOT NULL,
    signed_at timestamp with time zone,
    digital_signature_hash varchar(128),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rad_reports_study
    ON imaging_operations.radiology_reports (study_id);

-- AGG-038: ImagingDeliveryPackage
CREATE TABLE IF NOT EXISTS imaging_operations.imaging_delivery_packages (
    package_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    study_id varchar(36) NOT NULL REFERENCES imaging_operations.imaging_studies(study_id),
    patient_id varchar(36) NOT NULL,
    delivery_format varchar(30) NOT NULL,
    delivery_status varchar(30) NOT NULL,
    portal_access_token varchar(128),
    expires_at timestamp with time zone,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_img_delivery_tenant_study
    ON imaging_operations.imaging_delivery_packages (tenant_id, study_id);
