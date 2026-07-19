package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationAdapterException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;

class LocalDeterministicMigrationDomainCommandAdapterTest {

    private final LocalDeterministicMigrationDomainCommandAdapter adapter =
            new LocalDeterministicMigrationDomainCommandAdapter();

    @Test
    void invokingTheSameCategoryTwiceReturnsTheSameCommandIdentifierIdempotently() {
        String first = adapter.invokeImportCommand("job-1", "patients.csv", 10);
        String second = adapter.invokeImportCommand("job-1", "patients.csv", 10);
        assertThat(first).isEqualTo(second);
        assertThat(first).startsWith("local-import-command-");
    }

    @Test
    void differentCategoriesOrJobsProduceDifferentCommandIdentifiers() {
        String category = adapter.invokeImportCommand("job-1", "patients.csv", 10);
        String otherJob = adapter.invokeImportCommand("job-2", "patients.csv", 10);
        String otherCategory = adapter.invokeImportCommand("job-1", "doctors.csv", 10);
        assertThat(category).isNotEqualTo(otherJob).isNotEqualTo(otherCategory);
    }

    @Test
    void aCategoryNameContainingFailMarkerDeterministicallyFails() {
        MigrationAdapterException exception = assertThrows(MigrationAdapterException.class,
                () -> adapter.invokeImportCommand("job-1", "records_FAIL.csv", 5));
        assertThat(exception.canonicalErrorCode()).isEqualTo(MigrationErrorCodes.MIGRATION_DOMAIN_COMMAND_FAILED);
    }
}
