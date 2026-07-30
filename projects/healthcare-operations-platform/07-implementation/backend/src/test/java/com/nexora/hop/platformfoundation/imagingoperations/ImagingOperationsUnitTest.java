package com.nexora.hop.platformfoundation.imagingoperations;

import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.adapter.in.web.ImagingAppointmentSchedulingController;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.adapter.out.persistence.InMemoryImagingAppointmentSlotRepository;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.application.ImagingAppointmentSchedulingService;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlot;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.in.web.DicomIntegrationController;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out.DicomGatewayAdapter;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out.persistence.InMemoryDicomAdapterConfigurationRepository;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.application.DicomIntegrationService;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfiguration;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.adapter.in.web.MedicalDictationController;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.adapter.out.persistence.InMemoryRadiologyDictationRepository;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.application.MedicalDictationService;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictation;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.in.web.PacsIntegrationController;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out.PacsBridgeAdapter;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out.persistence.InMemoryPacsIntegrationEndpointRepository;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.application.PacsIntegrationService;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpoint;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.adapter.in.web.RadiologySignatureController;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.adapter.out.persistence.InMemoryRadiologyReportRepository;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.application.RadiologySignatureService;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.adapter.in.web.ImagingReceptionController;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.adapter.out.persistence.InMemoryImagingReceptionIntakeRepository;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.application.ImagingReceptionService;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntake;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingAccessDeniedException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingDomainException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingExceptionHandler;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.adapter.in.web.ImagingStudyDeliveryController;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.adapter.out.persistence.InMemoryImagingDeliveryPackageRepository;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.application.ImagingStudyDeliveryService;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackage;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.adapter.in.web.ImagingStudyManagementController;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.adapter.out.persistence.InMemoryImagingStudyRepository;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.application.ImagingStudyManagementService;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImagingOperationsUnitTest {

    private String tenantId;
    private String actorId;
    private ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort;

    private ImagingAppointmentSchedulingService schedulingService;
    private ImagingAppointmentSchedulingController schedulingController;

    private ImagingReceptionService receptionService;
    private ImagingReceptionController receptionController;

    private ImagingStudyManagementService studyService;
    private ImagingStudyManagementController studyController;

    private DicomIntegrationService dicomService;
    private DicomIntegrationController dicomController;

    private PacsIntegrationService pacsService;
    private PacsIntegrationController pacsController;

    private MedicalDictationService dictationService;
    private MedicalDictationController dictationController;

    private RadiologySignatureService signatureService;
    private RadiologySignatureController signatureController;

    private ImagingStudyDeliveryService deliveryService;
    private ImagingStudyDeliveryController deliveryController;

    private ImagingExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-test-01";
        actorId = "usr-test-01";

        schedulingService = new ImagingAppointmentSchedulingService(new InMemoryImagingAppointmentSlotRepository());
        schedulingController = new ImagingAppointmentSchedulingController(schedulingService);

        receptionService = new ImagingReceptionService(new InMemoryImagingReceptionIntakeRepository());
        receptionController = new ImagingReceptionController(receptionService);

        InMemoryImagingStudyRepository studyRepository = new InMemoryImagingStudyRepository();
        studyService = new ImagingStudyManagementService(studyRepository);
        studyController = new ImagingStudyManagementController(studyService);

        dicomService = new DicomIntegrationService(new InMemoryDicomAdapterConfigurationRepository(), new DicomGatewayAdapter());
        dicomController = new DicomIntegrationController(dicomService);

        pacsService = new PacsIntegrationService(new InMemoryPacsIntegrationEndpointRepository(), new PacsBridgeAdapter());
        pacsController = new PacsIntegrationController(pacsService);

        dictationService = new MedicalDictationService(new InMemoryRadiologyDictationRepository());
        dictationController = new MedicalDictationController(dictationService);

        referringDoctorAuthorizationPort = mock(ReferringDoctorAuthorizationPort.class);

        signatureService = new RadiologySignatureService(
                new InMemoryRadiologyReportRepository(), studyRepository, referringDoctorAuthorizationPort);
        signatureController = new RadiologySignatureController(signatureService);

        deliveryService = new ImagingStudyDeliveryService(
                new InMemoryImagingDeliveryPackageRepository(), referringDoctorAuthorizationPort);
        deliveryController = new ImagingStudyDeliveryController(deliveryService);

        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("imaging.error.appointment_not_found", Locale.ENGLISH, "Specified imaging appointment slot was not found.");
        messageSource.addMessage("imaging.error.room_not_available", Locale.ENGLISH, "Procedure room is not available for the selected slot time.");
        messageSource.addMessage("imaging.error.delivery_package_access_denied", Locale.ENGLISH, "Not authorized to access this imaging delivery package.");
        exceptionHandler = new ImagingExceptionHandler(messageSource);
    }

    @Test
    void testBcmImgEndpointsStatus() {
        assertThat(schedulingController.getBcmImg001Status().getBody()).containsEntry("capability", "BCM-IMG-001");
        assertThat(receptionController.getBcmImg002Status().getBody()).containsEntry("capability", "BCM-IMG-002");
        assertThat(studyController.getBcmImg003Status().getBody()).containsEntry("capability", "BCM-IMG-003");
        assertThat(dicomController.getBcmImg004Status().getBody()).containsEntry("capability", "BCM-IMG-004");
        assertThat(pacsController.getBcmImg005Status().getBody()).containsEntry("capability", "BCM-IMG-005");
        assertThat(dictationController.getBcmImg006Status().getBody()).containsEntry("capability", "BCM-IMG-006");
        assertThat(signatureController.getBcmImg007Status().getBody()).containsEntry("capability", "BCM-IMG-007");
        assertThat(deliveryController.getBcmImg008Status().getBody()).containsEntry("capability", "BCM-IMG-008");
    }

    @Test
    void testAppointmentSchedulingWorkflow() {
        Instant startTime = Instant.now().plusSeconds(3600);
        ImagingAppointmentSchedulingController.ScheduleSlotRequest request =
                new ImagingAppointmentSchedulingController.ScheduleSlotRequest(
                        "pat-100", "br-01", "CT", "CT_CHEST", "room-101", startTime, 30, "Patient fasting"
                );

        ResponseEntity<ImagingAppointmentSlot> response = schedulingController.scheduleSlot(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ImagingAppointmentSlot slot = response.getBody();
        assertThat(slot).isNotNull();
        assertThat(slot.modality()).isEqualTo("CT");
        assertThat(slot.slotStatus()).isEqualTo("SCHEDULED");

        // Verify room overlap exception (TD-DEF-002 reduction check)
        assertThatThrownBy(() -> schedulingController.scheduleSlot(tenantId, actorId, request))
                .isInstanceOf(ImagingDomainException.class)
                .hasMessageContaining("already occupied");

        // Fetch & List
        assertThat(schedulingController.getSlot(tenantId, slot.slotId()).getBody()).isNotNull();
        ResponseEntity<List<ImagingAppointmentSlot>> listResp = schedulingController.listSlotsForPatient(tenantId, "pat-100");
        assertThat(listResp.getBody()).hasSize(1);

        // Update status
        ResponseEntity<ImagingAppointmentSlot> updatedResp = schedulingController.updateStatus(tenantId, actorId, slot.slotId(), Map.of("status", "CONFIRMED"));
        assertThat(updatedResp.getBody().slotStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void testReceptionWorkflow() {
        ImagingReceptionController.CheckInRequest request = new ImagingReceptionController.CheckInRequest("slot-001", "pat-100", true, "Checked in at reception");
        ResponseEntity<ImagingReceptionIntake> response = receptionController.checkIn(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ImagingReceptionIntake intake = response.getBody();
        assertThat(intake.checkInStatus()).isEqualTo("CHECKED_IN");
        assertThat(intake.preparationVerified()).isTrue();

        assertThat(receptionController.getIntake(tenantId, intake.intakeId()).getBody()).isNotNull();
        assertThat(receptionController.getIntakeBySlot(tenantId, "slot-001").getBody()).isNotNull();
    }

    @Test
    void testStudyManagementWorkflow() {
        ImagingStudyManagementController.CreateStudyRequest request = new ImagingStudyManagementController.CreateStudyRequest(
                "ACC-2026-001", "pat-100", "MRI", "MRI BRAIN WITH CONTRAST"
        );
        ResponseEntity<ImagingStudy> response = studyController.createStudy(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ImagingStudy study = response.getBody();
        assertThat(study.accessionNumber()).isEqualTo("ACC-2026-001");
        assertThat(study.studyStatus()).isEqualTo("ORDERED");

        // Duplicate accession check
        assertThatThrownBy(() -> studyController.createStudy(tenantId, actorId, request))
                .isInstanceOf(ImagingDomainException.class)
                .hasMessageContaining("already exists");

        // Update counts and status
        ImagingStudyManagementController.UpdateStudyStatusRequest updateReq = new ImagingStudyManagementController.UpdateStudyStatusRequest(2, 45, "ACQUIRED");
        ResponseEntity<ImagingStudy> updated = studyController.updateStudyStatus(tenantId, actorId, study.studyId(), updateReq);
        assertThat(updated.getBody().seriesCount()).isEqualTo(2);
        assertThat(updated.getBody().instanceCount()).isEqualTo(45);
        assertThat(updated.getBody().studyStatus()).isEqualTo("ACQUIRED");
    }

    @Test
    void testDicomIntegrationWorkflow() {
        DicomIntegrationController.RegisterDicomConfigRequest request = new DicomIntegrationController.RegisterDicomConfigRequest(
                "PACS_CT_AE", "192.168.1.50", 104, "CT"
        );
        ResponseEntity<DicomAdapterConfiguration> response = dicomController.registerConfig(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DicomAdapterConfiguration config = response.getBody();
        assertThat(config.connectionStatus()).isEqualTo("ACTIVE");

        assertThat(dicomController.getConfig(tenantId, config.configurationId()).getBody()).isNotNull();
        assertThat(dicomController.listConfigs(tenantId).getBody()).hasSize(1);

        ResponseEntity<Map<String, String>> echoResp = dicomController.echoCEcho(tenantId, config.configurationId());
        assertThat(echoResp.getBody().get("result")).contains("C-ECHO SUCCESS");

        var worklistResp = dicomController.queryWorklist(tenantId, config.configurationId(), "PAT-100", "CT");
        assertThat(worklistResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(worklistResp.getBody()).hasSize(1);
        assertThat(worklistResp.getBody().get(0).modality()).isEqualTo("CT");

        var transferReq = new DicomIntegrationController.DicomTransferApiRequest("1.2.840.10008.1.1", "VIEWER_AE");
        var transferResp = dicomController.requestTransfer(tenantId, config.configurationId(), transferReq);
        assertThat(transferResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(transferResp.getBody().status()).isEqualTo("COMPLETED");

        var validateReq = new DicomIntegrationController.DicomValidateHeaderApiRequest("PAT-100", "1.2.840.10008.1.1", "CT");
        var validateResp = dicomController.validateHeader(tenantId, config.configurationId(), validateReq);
        assertThat(validateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(validateResp.getBody().valid()).isTrue();
    }

    @Test
    void testPacsIntegrationWorkflow() {
        PacsIntegrationController.RegisterPacsEndpointRequest request = new PacsIntegrationController.RegisterPacsEndpointRequest(
                "MAIN_PACS_NODE", "http://pacs.local/wado", "WADO_RS"
        );
        ResponseEntity<PacsIntegrationEndpoint> response = pacsController.registerEndpoint(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PacsIntegrationEndpoint endpoint = response.getBody();
        assertThat(endpoint.status()).isEqualTo("ONLINE");

        assertThat(pacsController.getEndpoint(tenantId, endpoint.endpointId()).getBody()).isNotNull();
        assertThat(pacsController.listEndpoints(tenantId).getBody()).hasSize(1);

        ResponseEntity<Map<String, String>> queryResp = pacsController.queryPacs(tenantId, endpoint.endpointId(), "ACC-2026-001");
        assertThat(queryResp.getBody().get("result")).contains("PACS_QUERY_OK");

        var qidoResp = pacsController.qidoSearch(tenantId, endpoint.endpointId(), "PAT-200", "MR");
        assertThat(qidoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(qidoResp.getBody()).hasSize(1);
        assertThat(qidoResp.getBody().get(0).modality()).isEqualTo("MR");

        var wadoResp = pacsController.getWadoUrl(tenantId, endpoint.endpointId(), "1.3.12.2.1107.5.2.32.35177.1", "1.3.12.2.1107.5.2.32.35177.1.1", "1.3.12.2.1107.5.2.32.35177.1.1.1");
        assertThat(wadoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wadoResp.getBody().retrieveUrl()).contains("pacs.nexora.local/wado");

        var stowReq = new PacsIntegrationController.PacsStowStoreApiRequest("1.3.12.2.1107.5.2.32.35177.2", "application/dicom", "ZHVtbXkgZGF0YQ==");
        var stowResp = pacsController.stowStore(tenantId, endpoint.endpointId(), stowReq);
        assertThat(stowResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stowResp.getBody().status()).isEqualTo("STORED");
    }


    @Test
    void testMedicalDictationWorkflow() {
        MedicalDictationController.CreateDictationRequest request = new MedicalDictationController.CreateDictationRequest(
                "std-100", "No acute intracranial hemorrhage identified.", "s3://audio/dict-001.mp3"
        );
        ResponseEntity<RadiologyDictation> response = dictationController.createDictation(tenantId, actorId, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        RadiologyDictation dictation = response.getBody();
        assertThat(dictation.dictationStatus()).isEqualTo("COMPLETED");

        assertThat(dictationController.getDictation(tenantId, dictation.dictationId()).getBody()).isNotNull();
        assertThat(dictationController.listDictationsForStudy(tenantId, "std-100").getBody()).hasSize(1);
    }

    @Test
    void testRadiologySignatureWorkflow() {
        RadiologySignatureController.CreateReportRequest createReq = new RadiologySignatureController.CreateReportRequest(
                "std-100", "Normal ventricles and sulci.", "Unremarkable brain MRI."
        );
        ResponseEntity<RadiologyReport> draftResp = signatureController.createReport(tenantId, actorId, createReq);
        assertThat(draftResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        RadiologyReport draft = draftResp.getBody();
        assertThat(draft.reportStatus()).isEqualTo("DRAFT");

        ResponseEntity<RadiologyReport> signedResp = signatureController.signReport(tenantId, actorId, draft.reportId());
        assertThat(signedResp.getBody().reportStatus()).isEqualTo("FINAL_SIGNED");
        assertThat(signedResp.getBody().digitalSignatureHash()).isNotNull();

        assertThat(signatureController.getReport(tenantId, draft.reportId(), null, null).getBody()).isNotNull();
        assertThat(signatureController.listReportsForStudy(tenantId, "std-100", null, null).getBody()).hasSize(1);
    }

    @Test
    void testImagingStudyDeliveryWorkflow() {
        ImagingStudyDeliveryController.CreateDeliveryPackageRequest createReq = new ImagingStudyDeliveryController.CreateDeliveryPackageRequest(
                "std-100", "pat-100", "DICOM_ZIP"
        );
        ResponseEntity<ImagingDeliveryPackage> prepResp = deliveryController.createDeliveryPackage(tenantId, actorId, createReq);
        assertThat(prepResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ImagingDeliveryPackage pkg = prepResp.getBody();
        assertThat(pkg.deliveryStatus()).isEqualTo("PREPARED");

        ResponseEntity<ImagingDeliveryPackage> delResp = deliveryController.markDelivered(tenantId, actorId, pkg.packageId());
        assertThat(delResp.getBody().deliveryStatus()).isEqualTo("DELIVERED");

        assertThat(deliveryController.getDeliveryPackage(tenantId, pkg.packageId(), null, null).getBody()).isNotNull();
        assertThat(deliveryController.listDeliveryPackagesForPatient(tenantId, "pat-100", null, null).getBody()).hasSize(1);
    }

    @Test
    void patientMayViewTheirOwnDeliveryPackageAndReport() {
        ImagingStudyDeliveryController.CreateDeliveryPackageRequest createReq =
                new ImagingStudyDeliveryController.CreateDeliveryPackageRequest("std-200", "pat-200", "DICOM_ZIP");
        ImagingDeliveryPackage pkg = deliveryController.createDeliveryPackage(tenantId, actorId, createReq).getBody();

        assertThat(deliveryController.getDeliveryPackage(tenantId, pkg.packageId(), "PATIENT", "pat-200").getBody())
                .isNotNull();
        assertThat(deliveryController
                        .listDeliveryPackagesForPatient(tenantId, "pat-200", "PATIENT", "pat-200")
                        .getBody())
                .hasSize(1);
    }

    @Test
    void patientCannotViewAnotherPatientsDeliveryPackageOrList() {
        ImagingStudyDeliveryController.CreateDeliveryPackageRequest createReq =
                new ImagingStudyDeliveryController.CreateDeliveryPackageRequest("std-201", "pat-201", "DICOM_ZIP");
        ImagingDeliveryPackage pkg = deliveryController.createDeliveryPackage(tenantId, actorId, createReq).getBody();

        assertThatThrownBy(() -> deliveryController.getDeliveryPackage(tenantId, pkg.packageId(), "PATIENT", "pat-999"))
                .isInstanceOf(ImagingAccessDeniedException.class);
        assertThatThrownBy(() -> deliveryController.listDeliveryPackagesForPatient(tenantId, "pat-201", "PATIENT", "pat-999"))
                .isInstanceOf(ImagingAccessDeniedException.class);
    }

    @Test
    void referringDoctorWithConfirmedReferralMayViewDeliveryPackage() {
        ImagingStudyDeliveryController.CreateDeliveryPackageRequest createReq =
                new ImagingStudyDeliveryController.CreateDeliveryPackageRequest("std-202", "pat-202", "DICOM_ZIP");
        ImagingDeliveryPackage pkg = deliveryController.createDeliveryPackage(tenantId, actorId, createReq).getBody();
        when(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, "doc-01", "pat-202")).thenReturn(true);

        assertThat(deliveryController.getDeliveryPackage(tenantId, pkg.packageId(), "REFERRING_DOCTOR", "doc-01").getBody())
                .isNotNull();
    }

    @Test
    void referringDoctorWithoutReferralCannotViewDeliveryPackage() {
        ImagingStudyDeliveryController.CreateDeliveryPackageRequest createReq =
                new ImagingStudyDeliveryController.CreateDeliveryPackageRequest("std-203", "pat-203", "DICOM_ZIP");
        ImagingDeliveryPackage pkg = deliveryController.createDeliveryPackage(tenantId, actorId, createReq).getBody();
        when(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, "doc-02", "pat-203")).thenReturn(false);

        assertThatThrownBy(() -> deliveryController.getDeliveryPackage(tenantId, pkg.packageId(), "REFERRING_DOCTOR", "doc-02"))
                .isInstanceOf(ImagingAccessDeniedException.class);
    }

    @Test
    void patientMayViewReportForTheirOwnStudyButNotAnothersStudy() {
        ImagingStudyManagementController.CreateStudyRequest studyReq =
                new ImagingStudyManagementController.CreateStudyRequest("ACC-2026-300", "pat-300", "CT", "CT ABDOMEN");
        ImagingStudy study = studyController.createStudy(tenantId, actorId, studyReq).getBody();

        RadiologySignatureController.CreateReportRequest reportReq =
                new RadiologySignatureController.CreateReportRequest(study.studyId(), "Findings.", "Impression.");
        RadiologyReport report = signatureController.createReport(tenantId, actorId, reportReq).getBody();

        assertThat(signatureController.getReport(tenantId, report.reportId(), "PATIENT", "pat-300").getBody())
                .isNotNull();
        assertThat(signatureController
                        .listReportsForStudy(tenantId, study.studyId(), "PATIENT", "pat-300")
                        .getBody())
                .hasSize(1);

        assertThatThrownBy(() -> signatureController.getReport(tenantId, report.reportId(), "PATIENT", "pat-999"))
                .isInstanceOf(ImagingAccessDeniedException.class);
    }

    @Test
    void referringDoctorReferralCheckAppliesToReportAccess() {
        ImagingStudyManagementController.CreateStudyRequest studyReq =
                new ImagingStudyManagementController.CreateStudyRequest("ACC-2026-301", "pat-301", "CT", "CT PELVIS");
        ImagingStudy study = studyController.createStudy(tenantId, actorId, studyReq).getBody();

        RadiologySignatureController.CreateReportRequest reportReq =
                new RadiologySignatureController.CreateReportRequest(study.studyId(), "Findings.", "Impression.");
        RadiologyReport report = signatureController.createReport(tenantId, actorId, reportReq).getBody();

        when(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, "doc-03", "pat-301")).thenReturn(true);
        assertThat(signatureController.getReport(tenantId, report.reportId(), "REFERRING_DOCTOR", "doc-03").getBody())
                .isNotNull();

        when(referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, "doc-04", "pat-301")).thenReturn(false);
        assertThatThrownBy(() -> signatureController.getReport(tenantId, report.reportId(), "REFERRING_DOCTOR", "doc-04"))
                .isInstanceOf(ImagingAccessDeniedException.class);
    }

    @Test
    void testExceptionHandlerHandling() {
        ImagingNotFoundException notFound = new ImagingNotFoundException(ImagingErrorCode.APPOINTMENT_NOT_FOUND, "Not found");
        ResponseEntity<Map<String, Object>> notFoundResp = exceptionHandler.handleNotFound(notFound, Locale.ENGLISH);
        assertThat(notFoundResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(notFoundResp.getBody()).containsEntry("code", "APPOINTMENT_NOT_FOUND");

        ImagingDomainException domainEx = new ImagingDomainException(ImagingErrorCode.ROOM_NOT_AVAILABLE, "Room taken");
        ResponseEntity<Map<String, Object>> domainResp = exceptionHandler.handleDomainException(domainEx, Locale.ENGLISH);
        assertThat(domainResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(domainResp.getBody()).containsEntry("code", "ROOM_NOT_AVAILABLE");

        ImagingAccessDeniedException accessDenied = new ImagingAccessDeniedException(
                ImagingErrorCode.DELIVERY_PACKAGE_ACCESS_DENIED, "Not authorized");
        ResponseEntity<Map<String, Object>> accessDeniedResp = exceptionHandler.handleAccessDenied(accessDenied, Locale.ENGLISH);
        assertThat(accessDeniedResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(accessDeniedResp.getBody()).containsEntry("code", "DELIVERY_PACKAGE_ACCESS_DENIED");
    }
}
