package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.application.CriticalResultEscalationService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.CriticalResultEscalation;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class CriticalResultEscalationControllerTest {

    private final CriticalResultEscalationService service = mock(CriticalResultEscalationService.class);
    private final CriticalResultEscalationController controller = new CriticalResultEscalationController(service);

    @Test
    void acknowledgeDelegatesToServiceAndReturnsOk() {
        UUID escalationId = UUID.randomUUID();
        CriticalResultEscalation escalation = escalation(escalationId);
        when(service.acknowledge(eq(escalationId), eq("user-1"), any(AuditMetadata.class))).thenReturn(escalation);

        ResponseEntity<CriticalResultEscalation> response =
                controller.acknowledge(escalationId, "user-1", "actor-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(escalation);
    }

    @Test
    void closeDelegatesToServiceAndReturnsOk() {
        UUID escalationId = UUID.randomUUID();
        CriticalResultEscalation escalation = escalation(escalationId);
        when(service.close(eq(escalationId), any(AuditMetadata.class))).thenReturn(escalation);

        ResponseEntity<CriticalResultEscalation> response = controller.close(escalationId, "actor-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(escalation);
    }

    @Test
    void escalateDelegatesToServiceAndReturnsOk() {
        UUID escalationId = UUID.randomUUID();
        CriticalResultEscalation escalation = escalation(escalationId);
        when(service.escalate(eq(escalationId), any(AuditMetadata.class))).thenReturn(escalation);

        ResponseEntity<CriticalResultEscalation> response = controller.escalate(escalationId, "actor-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(escalation);
    }

    @Test
    void getOpenEscalationsDelegatesToServiceAndReturnsOk() {
        CriticalResultEscalation escalation = escalation(UUID.randomUUID());
        when(service.listOpenEscalations(anyString())).thenReturn(List.of(escalation));

        ResponseEntity<List<CriticalResultEscalation>> response = controller.getOpenEscalations("tenant-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(escalation);
    }

    private static CriticalResultEscalation escalation(UUID escalationId) {
        return new CriticalResultEscalation(
                escalationId,
                new TenantId("tenant-1"),
                new LaboratoryId("lab-1"),
                new ResultId("result-1"),
                "critical reason",
                LocalDateTime.now().plusMinutes(15),
                new AuditMetadata("actor-1", LocalDateTime.now(), "actor-1", LocalDateTime.now()));
    }
}
