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
    void testGeneratedResultReport() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        GeneratedResultReport r1 = new GeneratedResultReport(UUID.randomUUID(), new ResultId("r1"), new TenantId("t1"), UUID.randomUUID(), 1, audit);
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void testResultDeliveryTicket() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        ResultDeliveryTicket r1 = new ResultDeliveryTicket(UUID.randomUUID(), new ResultId("r1"), new TenantId("t1"), new PatientId("p1"), "status", LocalDateTime.now(), audit);
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
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
    void testResultNotificationRequest() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        ResultNotificationRequest r1 = new ResultNotificationRequest(UUID.randomUUID(), new ResultId("r1"), new TenantId("t1"), new PatientId("p1"), UUID.randomUUID(), audit);
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
    }
}
