package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.adapter.out.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain.PatientResultHistoryView;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class JdbcPatientResultHistoryRepositoryTest {

    private final NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
    private final JdbcPatientResultHistoryRepository repository =
            new JdbcPatientResultHistoryRepository(jdbcTemplate);

    @Test
    void saveIsNotYetImplementedAndThrows() {
        PatientResultHistoryView view = new PatientResultHistoryView(new PatientId("patient-1"), List.of());

        assertThatThrownBy(() -> repository.save(view)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void findMethodsReturnEmptyUntilJdbcMappingIsImplemented() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        assertThat(repository.findByPatientId(new PatientId("patient-1"))).isEmpty();
        assertThat(repository.findByResultId(new ResultId("result-1"), new TenantId("tenant-1"))).isEmpty();
    }
}
