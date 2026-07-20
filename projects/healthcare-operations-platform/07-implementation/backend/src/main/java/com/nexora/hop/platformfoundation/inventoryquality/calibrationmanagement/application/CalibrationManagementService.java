package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationEvent;
import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationRepository;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.application.EquipmentManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-QLT-003 Calibration Management compiled from the capability model. */
@Service
public class CalibrationManagementService {

  private static final Set<String> RESULTS =
      Set.of(CalibrationEvent.RESULT_PASS, CalibrationEvent.RESULT_FAIL, CalibrationEvent.RESULT_ADJUSTED);

  private final CalibrationRepository repository;
  private final InventoryItemService inventoryItemService;
  private final EquipmentManagementService equipmentManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public CalibrationManagementService(
      CalibrationRepository repository,
      InventoryItemService inventoryItemService,
      EquipmentManagementService equipmentManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, equipmentManagementService, auditRecorder, Clock.systemUTC());
  }

  CalibrationManagementService(
      CalibrationRepository repository,
      InventoryItemService inventoryItemService,
      EquipmentManagementService equipmentManagementService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.inventoryItemService = inventoryItemService;
    this.equipmentManagementService = equipmentManagementService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public CalibrationEvent recordCalibration(
      String inventoryItemId, RecordCalibrationCommand command) {
    InventoryItem item = requireEquipmentItem(inventoryItemId);
    String performedBy = requireText(command.performedBy(), "Performed by is required.");
    LocalDateTime performedAt = command.performedAt() == null ? LocalDateTime.now(clock) : command.performedAt();
    String result = requireResult(command.result());
    LocalDate nextDueDate = command.nextDueDate();
    if (nextDueDate != null && !nextDueDate.isAfter(performedAt.toLocalDate())) {
      throw new InventoryConflictException(
          "Next due date must be after performed date.",
          InventoryErrorCodes.CALIBRATION_NEXT_DUE_DATE_INVALID);
    }
    CalibrationEvent saved =
        repository.save(
            new CalibrationEvent(
                UUID.randomUUID().toString(),
                item.inventoryItemId(),
                item.tenantId(),
                item.branchId(),
                requireText(command.calibrationStandardRef(), "Calibration standard ref is required."),
                performedBy,
                performedAt,
                result,
                nextDueDate,
                command.certificateReference(),
                new AuditMetadata(performedBy, LocalDateTime.now(clock), performedBy, LocalDateTime.now(clock))));
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "CalibrationRecorded",
        "CalibrationEvent",
        saved.calibrationEventId(),
        "{\"inventoryItemId\":\"%s\",\"result\":\"%s\"}".formatted(item.inventoryItemId(), result));
    if (CalibrationEvent.RESULT_FAIL.equals(result)) {
      equipmentManagementService.transitionAvailability(
          inventoryItemId,
          EquipmentProfile.STATUS_OUT_OF_SERVICE,
          EquipmentAvailabilityChange.REASON_CALIBRATION_FAILED,
          performedBy);
      auditRecorder.recordSystemEvent(
          item.tenantId(),
          "CalibrationFailed",
          "InventoryItem",
          item.inventoryItemId(),
          "{\"calibrationEventId\":\"%s\"}".formatted(saved.calibrationEventId()));
    }
    return saved;
  }

  public List<CalibrationEvent> listCalibrations(String inventoryItemId) {
    requireEquipmentItem(inventoryItemId);
    return repository.findByInventoryItemId(inventoryItemId);
  }

  private InventoryItem requireEquipmentItem(String inventoryItemId) {
    InventoryItem item = inventoryItemService.requireItem(inventoryItemId);
    if (!InventoryItem.ITEM_TYPE_EQUIPMENT.equals(item.itemType())) {
      throw new InventoryConflictException(
          "Inventory item is not equipment.",
          InventoryErrorCodes.CALIBRATION_ITEM_TYPE_NOT_ELIGIBLE);
    }
    return item;
  }

  private static String requireResult(String value) {
    if (value == null || !RESULTS.contains(value)) {
      throw new InvalidInventoryCommandException(
          "Calibration result is invalid. Allowed: " + RESULTS,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  private static String requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          message, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  public record RecordCalibrationCommand(
      String calibrationStandardRef,
      String performedBy,
      LocalDateTime performedAt,
      String result,
      LocalDate nextDueDate,
      String certificateReference) {}
}
