package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.out.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.DeliveryAuthorizationCheck;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class JdbcResultDeliveryTicketRepositoryTest {

    private final NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
    private final JdbcResultDeliveryTicketRepository repository =
            new JdbcResultDeliveryTicketRepository(jdbcTemplate);

    @Test
    void saveIsNotYetImplementedAndThrows() {
        ResultDeliveryTicket ticket =
                new ResultDeliveryTicket(
                        UUID.randomUUID(),
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

        assertThatThrownBy(() -> repository.save(ticket))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void findMethodsReturnEmptyUntilJdbcMappingIsImplemented() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        assertThat(repository.findByPatientId(new PatientId("patient-1"))).isEmpty();
        assertThat(repository.findByResultId(new ResultId("result-1"))).isEmpty();
        assertThat(repository.findAll()).isEmpty();
        assertThat(repository.findByRecipientId("recipient-1")).isEmpty();
    }
}
