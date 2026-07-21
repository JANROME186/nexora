package com.nexora.hop.platformfoundation.publicweb.intake;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort.PublicQuotationIntakeCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake.PublicIntakePort.PublicQuotationIntakeResult;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public-website quotation intake (BCM-ATT-006 RN-009). Creates a
 * {@code draft}-state QuotationRequest from a ProspectiveContact only, subject to BCM-PLT-005's
 * public rate-limit classification enforced by {@code PublicApiRateLimitInterceptor}. Never
 * issues, accepts, converts or prices a quotation.
 */
@RestController
@RequestMapping(PublicWebConstants.CARE_DELIVERY_BASE_PATH + "/quotation-requests")
class PublicQuotationIntakeController {

    private final PublicIntakePort intakePort;

    PublicQuotationIntakeController(PublicIntakePort intakePort) {
        this.intakePort = intakePort;
    }

    @PostMapping
    ResponseEntity<PublicQuotationIntakeResult> submit(@Valid @RequestBody PublicQuotationRequestBody body) {
        try {
            PublicQuotationIntakeResult saved = intakePort.submitQuotationRequest(
                    new PublicQuotationIntakeCommand(
                            body.tenantId(), body.laboratoryId(), body.branchId(),
                            body.prospectiveFullName(), body.prospectivePhone(), body.prospectiveEmail(),
                            body.lines() == null ? List.of()
                                    : body.lines().stream().map(PublicQuotationLineBody::toInput).toList()));
            return ResponseEntity.created(URI.create(PublicWebConstants.CARE_DELIVERY_BASE_PATH
                    + "/quotation-requests/" + saved.quotationId()))
                    .body(saved);
        } catch (PublicIntakePort.PublicIntakeException ex) {
            throw mapException(ex);
        }
    }

    private static PublicWebException mapException(PublicIntakePort.PublicIntakeException ex) {
        return switch (ex.kind()) {
            case INVALID -> new PublicWebException(HttpStatus.BAD_REQUEST,
                    PublicWebErrorCodes.PUBLIC_QUOTATION_REQUEST_INVALID, ex.getMessage());
            case CONFLICT -> new PublicWebException(HttpStatus.CONFLICT,
                    PublicWebErrorCodes.PUBLIC_QUOTATION_REQUEST_INVALID, ex.getMessage());
            case NOT_FOUND -> new PublicWebException(HttpStatus.NOT_FOUND,
                    PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED, ex.getMessage());
        };
    }

    record PublicQuotationLineBody(
            @NotBlank String testDefinitionId, @NotBlank String catalogItemKind, Integer quantity) {
        PublicIntakePort.PublicQuotationLine toInput() {
            return new PublicIntakePort.PublicQuotationLine(testDefinitionId, catalogItemKind, quantity);
        }
    }

    record PublicQuotationRequestBody(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            String prospectiveFullName,
            String prospectivePhone,
            String prospectiveEmail,
            List<PublicQuotationLineBody> lines) {
    }
}
