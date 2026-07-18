package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.application.ResultDeliveryService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.DeliveryAuthorizationCheck;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ResultDeliveryControllerTest {

    private final ResultDeliveryService service = mock(ResultDeliveryService.class);
    private final ResultDeliveryController controller = new ResultDeliveryController(service);

    @Test
    void authorizeDeliveryDelegatesToServiceAndReturnsOk() {
        ResultDeliveryTicket ticket = ticket(UUID.randomUUID());
        when(service.authorizeResultDelivery(eq("result-1"), eq("tenant-1"), any(AuditMetadata.class)))
                .thenReturn(List.of(ticket));

        ResponseEntity<List<ResultDeliveryTicket>> response =
                controller.authorizeDelivery("result-1", "tenant-1", "actor-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(ticket);
    }

    @Test
    void getDeliveredResultDelegatesToServiceAndReturnsOk() {
        UUID ticketId = UUID.randomUUID();
        ResultDeliveryTicket ticket = ticket(ticketId);
        when(service.getDeliveredResult(eq(ticketId), eq("tenant-1"), eq("caller-1"), any(AuditMetadata.class)))
                .thenReturn(ticket);

        ResponseEntity<ResultDeliveryTicket> response =
                controller.getDeliveredResult(ticketId, "tenant-1", "caller-1", "actor-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(ticket);
    }

    private static ResultDeliveryTicket ticket(UUID ticketId) {
        return new ResultDeliveryTicket(
                ticketId,
                new ResultId("result-1"),
                new TenantId("tenant-1"),
                new PatientId("patient-1"),
                "access-code-1",
                LocalDateTime.now().plusDays(30),
                "patient",
                "patient-1",
                "patient_portal",
                new DeliveryAuthorizationCheck(true, true, false, LocalDateTime.now()),
                new AuditMetadata("actor-1", LocalDateTime.now(), "actor-1", LocalDateTime.now()));
    }
}
