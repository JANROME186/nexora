package com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class JdbcNotificationRequestRepositoryTest {

    private final NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
    private final JdbcNotificationRequestRepository repository =
            new JdbcNotificationRequestRepository(jdbcTemplate);

    @Test
    void saveIsNotYetImplementedAndThrows() {
        NotificationRequest request =
                new NotificationRequest(
                        UUID.randomUUID(),
                        new TenantId("tenant-1"),
                        new LaboratoryId("lab-1"),
                        "patient@example.com",
                        NotificationRequest.Channel.EMAIL,
                        "Result ready",
                        "Your result is ready",
                        new AuditMetadata("actor-1", LocalDateTime.now(), "actor-1", LocalDateTime.now()));

        assertThatThrownBy(() -> repository.save(request))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void findMethodsReturnEmptyUntilJdbcMappingIsImplemented() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        assertThat(repository.findByPatientId(new PatientId("patient-1"))).isEmpty();
        assertThat(repository.findByResultId(new ResultId("result-1"), new TenantId("tenant-1"))).isEmpty();
        assertThat(repository.findAll()).isEmpty();
        assertThat(repository.findByRecipientId("recipient-1")).isEmpty();
    }
}
