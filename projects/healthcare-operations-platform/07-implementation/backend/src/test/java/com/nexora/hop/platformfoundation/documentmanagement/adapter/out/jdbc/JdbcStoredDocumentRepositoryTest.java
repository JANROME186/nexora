package com.nexora.hop.platformfoundation.documentmanagement.adapter.out.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.nexora.hop.platformfoundation.documentmanagement.domain.RetentionPolicy;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StoredDocument;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class JdbcStoredDocumentRepositoryTest {

    private final NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
    private final JdbcStoredDocumentRepository repository = new JdbcStoredDocumentRepository(jdbcTemplate);

    @Test
    void saveIsNotYetImplementedAndThrows() {
        StoredDocument document =
                new StoredDocument(
                        UUID.randomUUID(),
                        new TenantId("tenant-1"),
                        new LaboratoryId("lab-1"),
                        "results_and_digital_delivery",
                        UUID.randomUUID(),
                        1,
                        "application/pdf",
                        "hash-1",
                        1024,
                        new StorageReference(
                                StorageReference.StorageProvider.LOCAL_FILESYSTEM,
                                "key-1",
                                LocalDateTime.now()),
                        RetentionPolicy.standard(LocalDate.now().plusYears(5)),
                        new AuditMetadata("actor-1", LocalDateTime.now(), "actor-1", LocalDateTime.now()));

        assertThatThrownBy(() -> repository.save(document))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void findMethodsReturnEmptyUntilJdbcMappingIsImplemented() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        assertThat(repository.findByPatientId(new PatientId("patient-1"))).isEmpty();
        assertThat(repository.findByResultId(new ResultId("result-1"), new TenantId("tenant-1"))).isEmpty();
    }
}
