package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.ReferringDoctorAuthorizationPort;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.shared.ResultsDeliveryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultHistoryService {

    /** Mirrors {@code RolePermissionCatalog.REFERRING_DOCTOR}; kept local since this module does
     * not depend on identityaccess (caller role is passed explicitly by the controller instead,
     * matching the {@code ResultDeliveryController} callerId precedent). */
    private static final String REFERRING_DOCTOR_ROLE = "REFERRING_DOCTOR";

    private final PatientResultHistoryRepository repository;
    private final ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort;

    public ResultHistoryService(
            PatientResultHistoryRepository repository,
            ReferringDoctorAuthorizationPort referringDoctorAuthorizationPort) {
        this.repository = repository;
        this.referringDoctorAuthorizationPort = referringDoctorAuthorizationPort;
    }

    public PatientResultHistoryView getHistoryForPatient(String patientIdStr) {
        return getHistoryForPatient(patientIdStr, null, null, null);
    }

    /**
     * COM-MOD-009-PORTAL-002: when {@code callerRoleCode} is {@code REFERRING_DOCTOR}, enforces
     * that the calling doctor ({@code callerId}) has actually referred this patient (i.e. is the
     * referring doctor on at least one of the patient's diagnostic orders) before returning their
     * result history, per the physician-patient relationship / least-privilege requirement.
     * Any other caller role (or a null role, e.g. the patient's own self-access, already enforced
     * upstream by the interceptor path check) is not subject to this additional check.
     */
    public PatientResultHistoryView getHistoryForPatient(
            String patientIdStr, String tenantId, String callerRoleCode, String callerId) {
        if (REFERRING_DOCTOR_ROLE.equals(callerRoleCode)
                && !referringDoctorAuthorizationPort.isPatientReferredByDoctor(tenantId, callerId, patientIdStr)) {
            throw new ResultHistoryAccessDeniedException(
                    ResultsDeliveryErrorCodes.DELIVERY_DOCTOR_REFERRAL_MISMATCH
                            + ": the requesting doctor has not referred this patient.");
        }
        PatientId patientId = new PatientId(patientIdStr);
        Optional<PatientResultHistoryView> view = repository.findByPatientId(patientId);
        return view.orElse(new PatientResultHistoryView(patientId, java.util.List.of()));
    }
}
