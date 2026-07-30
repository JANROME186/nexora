package com.nexora.hop.platformfoundation.imagingoperations.shared;

public enum ImagingErrorCode {
    APPOINTMENT_NOT_FOUND("imaging.error.appointment_not_found"),
    MODALITY_CONFLICT("imaging.error.modality_conflict"),
    ROOM_NOT_AVAILABLE("imaging.error.room_not_available"),
    RECEPTION_NOT_FOUND("imaging.error.reception_not_found"),
    STUDY_NOT_FOUND("imaging.error.study_not_found"),
    ACCESSION_EXISTS("imaging.error.accession_exists"),
    DICOM_CONFIG_NOT_FOUND("imaging.error.dicom_config_not_found"),
    PACS_ENDPOINT_NOT_FOUND("imaging.error.pacs_endpoint_not_found"),
    DICTATION_NOT_FOUND("imaging.error.dictation_not_found"),
    REPORT_NOT_FOUND("imaging.error.report_not_found"),
    DELIVERY_PACKAGE_NOT_FOUND("imaging.error.delivery_package_not_found"),
    INVALID_STATUS_TRANSITION("imaging.error.invalid_status_transition"),
    DICOM_TRANSFER_FAILED("imaging.error.dicom_transfer_failed"),
    PACS_STORE_FAILED("imaging.error.pacs_store_failed"),
    DICOM_HEADER_INVALID("imaging.error.dicom_header_invalid"),
    DELIVERY_PACKAGE_ACCESS_DENIED("imaging.error.delivery_package_access_denied"),
    REPORT_ACCESS_DENIED("imaging.error.report_access_denied");

    private final String messageKey;

    ImagingErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
