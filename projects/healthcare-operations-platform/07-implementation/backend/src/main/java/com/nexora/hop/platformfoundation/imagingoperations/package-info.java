/**
 * Imaging Operations bounded context, compiled from COM-MOD-014 (BCM-IMG-001 through BCM-IMG-008).
 * Hosts eight capability sub-packages as sibling bounded contexts inside a single Spring Modulith
 * module: {@code appointmentscheduling} (AGG-031 ImagingAppointmentSlot), {@code receptionintake}
 * (AGG-032 ImagingReceptionIntake), {@code studymanagement} (AGG-033 ImagingStudy),
 * {@code dicomintegration} (AGG-034 DicomAdapterConfiguration), {@code pacsintegration}
 * (AGG-035 PacsIntegrationEndpoint), {@code medicaldictation} (AGG-036 RadiologyDictation),
 * {@code radiologysignature} (AGG-037 RadiologyReport) and {@code studydelivery} (AGG-038
 * ImagingDeliveryPackage).
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Imaging Operations",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance", "peopleclinicalmasterdata", "frontdeskcaredelivery", "laboratoryworkflow", "resultsanddigitaldelivery"})
package com.nexora.hop.platformfoundation.imagingoperations;
