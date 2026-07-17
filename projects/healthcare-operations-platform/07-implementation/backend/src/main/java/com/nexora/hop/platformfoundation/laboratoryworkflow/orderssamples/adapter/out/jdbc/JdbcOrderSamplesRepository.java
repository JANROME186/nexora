package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.ChainOfCustodyEvent;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CollectionMethod;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CustodyEventType;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.OrderSamplesRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.PatientConditionAtCollection;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.PatientIdentitySnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.ReceptionCondition;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.RejectionReasonCode;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.RejectionStage;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleCollectionData;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleReceptionRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleRejectionReason;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleRequirementSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SpecimenLabelInfo;

/**
 * JDBC repository for Sample aggregate (local profile with PostgreSQL).
 */
@Repository
@Profile("local")
class JdbcOrderSamplesRepository implements OrderSamplesRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcOrderSamplesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Sample save(Sample sample) {
        jdbcTemplate.update("""
                insert into orders_samples.samples (
                    sample_id, tenant_id, laboratory_id, branch_id, order_id, order_line_id,
                    patient_id, patient_name, patient_birth_date, patient_snapshot_captured_at,
                    requirement_id, requirement_version, container_type, minimum_volume,
                    handling_instructions, requirement_captured_at,
                    collector_id, collection_site, collection_method, container_used,
                    collected_at, patient_condition,
                    label_id, barcode_value, printed_at,
                    received_by, received_at, condition_at_reception,
                    rejected_by, rejected_at, rejection_stage, rejection_reason_code, rejection_notes,
                    status, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (sample_id) do update set
                    label_id = excluded.label_id,
                    barcode_value = excluded.barcode_value,
                    printed_at = excluded.printed_at,
                    received_by = excluded.received_by,
                    received_at = excluded.received_at,
                    condition_at_reception = excluded.condition_at_reception,
                    rejected_by = excluded.rejected_by,
                    rejected_at = excluded.rejected_at,
                    rejection_stage = excluded.rejection_stage,
                    rejection_reason_code = excluded.rejection_reason_code,
                    rejection_notes = excluded.rejection_notes,
                    status = excluded.status,
                    updated_at = excluded.updated_at
                """,
                sample.sampleId(), sample.tenantId(), sample.laboratoryId(), sample.branchId(),
                sample.orderId(), sample.orderLineId(),
                sample.patientSnapshot().patientId(), sample.patientSnapshot().fullName(),
                sample.patientSnapshot().birthDate(),
                ts(sample.patientSnapshot().capturedAt()),
                sample.sampleRequirementSnapshot().sampleRequirementId(),
                sample.sampleRequirementSnapshot().publishedVersion(),
                sample.sampleRequirementSnapshot().containerType(),
                sample.sampleRequirementSnapshot().minimumVolume(),
                sample.sampleRequirementSnapshot().handlingInstructions(),
                ts(sample.sampleRequirementSnapshot().capturedAt()),
                sample.collectionData().collectorId(),
                sample.collectionData().collectionSite(),
                sample.collectionData().collectionMethod().name(),
                sample.collectionData().containerUsed(),
                ts(sample.collectionData().collectedAt()),
                sample.collectionData().patientConditionAtCollection() != null
                        ? sample.collectionData().patientConditionAtCollection().name() : null,
                sample.labelInfo() != null ? sample.labelInfo().labelId() : null,
                sample.labelInfo() != null ? sample.labelInfo().barcodeValue() : null,
                sample.labelInfo() != null ? ts(sample.labelInfo().printedAt()) : null,
                sample.receptionRecord() != null ? sample.receptionRecord().receivedBy() : null,
                sample.receptionRecord() != null ? ts(sample.receptionRecord().receivedAt()) : null,
                sample.receptionRecord() != null
                        ? sample.receptionRecord().conditionAtReception().name() : null,
                sample.rejectionReason() != null ? sample.rejectionReason().rejectedBy() : null,
                sample.rejectionReason() != null ? ts(sample.rejectionReason().rejectedAt()) : null,
                sample.rejectionReason() != null
                        ? sample.rejectionReason().rejectionStage().name() : null,
                sample.rejectionReason() != null
                        ? sample.rejectionReason().reasonCode().name() : null,
                sample.rejectionReason() != null ? sample.rejectionReason().notes() : null,
                sample.status().name(),
                ts(sample.createdAt()), ts(sample.updatedAt()));

        saveCustodyEvents(sample);
        return sample;
    }

    private void saveCustodyEvents(Sample sample) {
        // Delete and re-insert all custody events (append-only log; no event is ever removed)
        jdbcTemplate.update(
                "delete from orders_samples.chain_of_custody where sample_id = ?",
                sample.sampleId());
        for (ChainOfCustodyEvent evt : sample.chainOfCustody()) {
            jdbcTemplate.update("""
                    insert into orders_samples.chain_of_custody
                        (sample_id, event_type, actor_id, occurred_at, location_branch_id)
                    values (?, ?, ?, ?, ?)
                    """,
                    sample.sampleId(), evt.eventType().name(), evt.actorId(),
                    ts(evt.occurredAt()), evt.locationBranchId());
        }
    }

    @Override
    public Optional<Sample> findById(String sampleId, String tenantId) {
        List<Sample> result = jdbcTemplate.query(
                "select * from orders_samples.samples where sample_id = ? and tenant_id = ?",
                (rs, rn) -> mapSample(rs, loadCustodyEvents(sampleId)),
                sampleId, tenantId);
        return result.stream().findFirst();
    }

    @Override
    public List<Sample> findByOrderId(String orderId, String tenantId) {
        return jdbcTemplate.query(
                "select * from orders_samples.samples where order_id = ? and tenant_id = ?",
                (rs, rn) -> mapSample(rs, loadCustodyEvents(rs.getString("sample_id"))),
                orderId, tenantId);
    }

    @Override
    public List<Sample> findByStatus(SampleStatus status, String tenantId) {
        return jdbcTemplate.query(
                "select * from orders_samples.samples where status = ? and tenant_id = ?",
                (rs, rn) -> mapSample(rs, loadCustodyEvents(rs.getString("sample_id"))),
                status.name(), tenantId);
    }

    @Override
    public List<Sample> findCollectionWorklist(String tenantId, String branchId) {
        return jdbcTemplate.query("""
                select * from orders_samples.samples
                 where tenant_id = ? and branch_id = ? and status = ?
                """,
                (rs, rn) -> mapSample(rs, loadCustodyEvents(rs.getString("sample_id"))),
                tenantId, branchId, SampleStatus.collected.name());
    }

    @Override
    public List<Sample> findReceptionWorklist(String tenantId, String laboratoryId) {
        return jdbcTemplate.query("""
                select * from orders_samples.samples
                 where tenant_id = ? and laboratory_id = ? and status = ?
                """,
                (rs, rn) -> mapSample(rs, loadCustodyEvents(rs.getString("sample_id"))),
                tenantId, laboratoryId, SampleStatus.labeled.name());
    }

    private List<ChainOfCustodyEvent> loadCustodyEvents(String sampleId) {
        return jdbcTemplate.query(
                "select * from orders_samples.chain_of_custody where sample_id = ? order by occurred_at",
                (rs, rn) -> new ChainOfCustodyEvent(
                        CustodyEventType.valueOf(rs.getString("event_type")),
                        rs.getString("actor_id"),
                        instant(rs.getTimestamp("occurred_at")),
                        rs.getString("location_branch_id")),
                sampleId);
    }

    private static Sample mapSample(ResultSet rs, List<ChainOfCustodyEvent> custody)
            throws SQLException {
        PatientIdentitySnapshot patientSnapshot = new PatientIdentitySnapshot(
                rs.getString("patient_id"), rs.getString("patient_name"),
                rs.getString("patient_birth_date"),
                instant(rs.getTimestamp("patient_snapshot_captured_at")));

        SampleRequirementSnapshot reqSnapshot = new SampleRequirementSnapshot(
                rs.getString("requirement_id"), rs.getInt("requirement_version"),
                rs.getString("container_type"), rs.getString("minimum_volume"),
                rs.getString("handling_instructions"),
                instant(rs.getTimestamp("requirement_captured_at")));

        String condition = rs.getString("patient_condition");
        SampleCollectionData collectionData = new SampleCollectionData(
                rs.getString("collector_id"), rs.getString("collection_site"),
                CollectionMethod.valueOf(rs.getString("collection_method")),
                rs.getString("container_used"),
                instant(rs.getTimestamp("collected_at")),
                condition != null ? PatientConditionAtCollection.valueOf(condition) : null);

        String labelId = rs.getString("label_id");
        SpecimenLabelInfo labelInfo = labelId != null
                ? new SpecimenLabelInfo(labelId, rs.getString("barcode_value"),
                        instant(rs.getTimestamp("printed_at")))
                : null;

        String receivedBy = rs.getString("received_by");
        SampleReceptionRecord receptionRecord = receivedBy != null
                ? new SampleReceptionRecord(receivedBy,
                        instant(rs.getTimestamp("received_at")),
                        ReceptionCondition.valueOf(rs.getString("condition_at_reception")))
                : null;

        String rejectedBy = rs.getString("rejected_by");
        SampleRejectionReason rejectionReason = rejectedBy != null
                ? new SampleRejectionReason(rejectedBy,
                        instant(rs.getTimestamp("rejected_at")),
                        RejectionStage.valueOf(rs.getString("rejection_stage")),
                        RejectionReasonCode.valueOf(rs.getString("rejection_reason_code")),
                        rs.getString("rejection_notes"))
                : null;

        return new Sample(
                rs.getString("sample_id"), rs.getString("tenant_id"),
                rs.getString("laboratory_id"), rs.getString("branch_id"),
                rs.getString("order_id"), rs.getString("order_line_id"),
                patientSnapshot, reqSnapshot, collectionData, labelInfo, receptionRecord,
                rejectionReason, SampleStatus.valueOf(rs.getString("status")),
                new ArrayList<>(custody),
                instant(rs.getTimestamp("created_at")), instant(rs.getTimestamp("updated_at")));
    }

    private static Timestamp ts(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static Instant instant(Timestamp ts) {
        return ts != null ? ts.toInstant() : null;
    }
}
