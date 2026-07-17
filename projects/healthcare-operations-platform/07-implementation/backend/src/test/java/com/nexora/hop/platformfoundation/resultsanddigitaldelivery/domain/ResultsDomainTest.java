package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.domain;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReport;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.management.domain.ResultSearchIndexEntry;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequest;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResultsDomainTest {

    @Test
    void generatedResultReportCanBeSupersededOnce() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        UUID reportId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        GeneratedResultReport report = new GeneratedResultReport(reportId, new ResultId("r1"), new TenantId("t1"), documentId, 1, audit);

        assertEquals(reportId, report.getReportId());
        assertEquals(new ResultId("r1"), report.getResultId());
        assertEquals(new TenantId("t1"), report.getTenantId());
        assertEquals(documentId, report.getStoredDocumentId());
        assertEquals(1, report.getVersion());
        assertEquals(GeneratedResultReport.Status.GENERATED, report.getStatus());
        assertEquals(audit, report.getAudit());

        AuditMetadata updateAudit = new AuditMetadata("reviewer", LocalDateTime.now(), "reviewer", LocalDateTime.now());
        report.supersede(updateAudit);

        assertEquals(GeneratedResultReport.Status.SUPERSEDED, report.getStatus());
        assertEquals(updateAudit, report.getAudit());
        assertThrows(IllegalStateException.class, () -> report.supersede(updateAudit));
    }

    @Test
    void resultDeliveryTicketCanBeWithheld() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        UUID ticketId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        ResultDeliveryTicket ticket = new ResultDeliveryTicket(ticketId, new ResultId("r1"), new TenantId("t1"),
                new PatientId("p1"), "access-code", expiresAt, audit);

        assertEquals(ticketId, ticket.getTicketId());
        assertEquals(new ResultId("r1"), ticket.getResultId());
        assertEquals(new TenantId("t1"), ticket.getTenantId());
        assertEquals(new PatientId("p1"), ticket.getPatientId());
        assertEquals("access-code", ticket.getAccessCode());
        assertEquals(ResultDeliveryTicket.Status.AUTHORIZED, ticket.getStatus());
        assertEquals(expiresAt, ticket.getExpiresAt());
        assertEquals(audit, ticket.getAudit());

        AuditMetadata updateAudit = new AuditMetadata("reviewer", LocalDateTime.now(), "reviewer", LocalDateTime.now());
        ticket.withhold(updateAudit);

        assertEquals(ResultDeliveryTicket.Status.WITHHELD, ticket.getStatus());
        assertEquals(updateAudit, ticket.getAudit());
    }

    @Test
    void testPatientResultHistoryView() {
        PatientResultHistoryView r1 = new PatientResultHistoryView(new PatientId("p1"), List.of());
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
    }

    @Test
    void testResultSearchIndexEntry() {
        ResultSearchIndexEntry r1 = new ResultSearchIndexEntry(new ResultId("r1"), new TenantId("t1"), new LaboratoryId("l1"), new BranchId("b1"), new OrderId("o1"), new SampleId("s1"), new PatientId("p1"), "search", ResultSearchIndexEntry.Status.RELEASED, true, LocalDateTime.now(), 1);
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
    }

    @Test
    void resultNotificationRequestExposesTraceableIdentifiers() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        UUID resultNotificationId = UUID.randomUUID();
        UUID underlyingNotificationId = UUID.randomUUID();
        ResultNotificationRequest request = new ResultNotificationRequest(resultNotificationId, new ResultId("r1"),
                new TenantId("t1"), new PatientId("p1"), underlyingNotificationId, audit);

        assertEquals(resultNotificationId, request.getResultNotificationId());
        assertEquals(new ResultId("r1"), request.getResultId());
        assertEquals(new TenantId("t1"), request.getTenantId());
        assertEquals(new PatientId("p1"), request.getPatientId());
        assertEquals(underlyingNotificationId, request.getUnderlyingNotificationId());
        assertEquals(audit, request.getAudit());
    }
}
