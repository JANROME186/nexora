package com.nexora.hop.platformfoundation.laboratoryworkflow;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.ChainOfCustodyEvent;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CollectionMethod;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CustodyEventType;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.OrderSamplesRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.PatientIdentitySnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleCollectionData;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleRequirementSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus;

@SpringBootTest
@ActiveProfiles("local") // Forces JdbcOrderSamplesRepository to be used
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class LabWorkflowLocalDatabaseTest {

    @Autowired
    private OrderSamplesRepository repository;

    @Test
    @Transactional
    void canSaveAndLoadSampleViaJdbc() {
        String tenantId = UUID.randomUUID().toString();
        String sampleId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        Sample sample = new Sample(
                sampleId,
                tenantId,
                "lab-1",
                "branch-1",
                "order-1",
                "line-1",
                new PatientIdentitySnapshot("pt-1", "John Doe", "1980-01-01", now),
                new SampleRequirementSnapshot("req-1", 1, "tube", "2ml", "keep cold", now),
                new SampleCollectionData("nurse-1", null, CollectionMethod.venipuncture, "tube", now, null),
                null,
                null,
                null,
                SampleStatus.collected,
                List.of(new ChainOfCustodyEvent(CustodyEventType.collected, "nurse-1", now, "branch-1")),
                now,
                now
        );

        repository.save(sample);

        Sample loaded = repository.findById(sampleId, tenantId).orElseThrow();
        assertThat(loaded.sampleId()).isEqualTo(sampleId);
        assertThat(loaded.tenantId()).isEqualTo(tenantId);
        assertThat(loaded.status()).isEqualTo(SampleStatus.collected);
        assertThat(loaded.chainOfCustody()).hasSize(1);
        assertThat(loaded.chainOfCustody().getFirst().eventType()).isEqualTo(CustodyEventType.collected);
    }
}
