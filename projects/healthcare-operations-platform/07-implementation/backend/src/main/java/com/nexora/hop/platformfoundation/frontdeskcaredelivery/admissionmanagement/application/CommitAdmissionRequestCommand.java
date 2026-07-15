package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application;

public record CommitAdmissionRequestCommand(boolean consentConfirmed, boolean sampleRequirementsAcknowledged) {
}
