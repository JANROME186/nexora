package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.application.EquipmentManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceEvent;
import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceRepository;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-QLT-005 Maintenance Management compiled from the capability model. */
@Service
public class MaintenanceManagementService {

  private static final Set<String> MAINTENANCE_TYPES =
      Set.of(MaintenanceEvent.TYPE_PREVENTIVE, MaintenanceEvent.TYPE_CORRECTIVE);

  private final MaintenanceRepository repository;
  private final InventoryItemService inventoryItemService;
  private final EquipmentManagementService equipmentManagementService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public MaintenanceManagementService(
      MaintenanceRepository repository,
      InventoryItemService inventoryItemService,
      EquipmentManagementService equipmentManagementService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, equipmentManagementService, auditRecorder, Clock.systemUTC());
  }

  MaintenanceManagementService(
      MaintenanceRepository repository,
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

  public MaintenanceEvent recordMaintenance(
      String inventoryItemId, RecordMaintenanceCommand command) {
    InventoryItem item = requireEquipmentItem(inventoryItemId);
    String actor = actorFrom(command.performedBy(), command.externalTechnicianRef());
    LocalDateTime startedAt = command.startedAt() == null ? LocalDateTime.now(clock) : command.startedAt();
    LocalDateTime completedAt = command.completedAt();
    validateCompletion(startedAt, completedAt);
    MaintenanceEvent saved =
        repository.save(
            new MaintenanceEvent(
                UUID.randomUUID().toString(),
                item.inventoryItemId(),
                item.tenantId(),
                item.branchId(),
                requireType(command.maintenanceType()),
                command.performedBy(),
                command.externalTechnicianRef(),
                requireText(command.description(), "Maintenance description is required."),
                startedAt,
                completedAt,
                command.downtimeMinutes(),
                command.nextScheduledAt(),
                new AuditMetadata(actor, LocalDateTime.now(clock), actor, LocalDateTime.now(clock))));
    equipmentManagementService.transitionAvailability(
        inventoryItemId,
        completedAt == null ? EquipmentProfile.STATUS_OUT_OF_SERVICE : EquipmentProfile.STATUS_AVAILABLE,
        completedAt == null
            ? EquipmentAvailabilityChange.REASON_MAINTENANCE_SCHEDULED
            : EquipmentAvailabilityChange.REASON_MAINTENANCE_COMPLETED,
        actor);
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        completedAt == null ? "MaintenanceScheduled" : "MaintenanceCompleted",
        "MaintenanceEvent",
        saved.maintenanceEventId(),
        "{\"inventoryItemId\":\"%s\"}".formatted(item.inventoryItemId()));
    return saved;
  }

  public MaintenanceEvent completeMaintenance(
      String maintenanceEventId, CompleteMaintenanceCommand command) {
    MaintenanceEvent current = requireMaintenance(maintenanceEventId);
    String actor = requireText(command.actorId(), "Actor id is required.");
    LocalDateTime completedAt = command.completedAt() == null ? LocalDateTime.now(clock) : command.completedAt();
    validateCompletion(current.startedAt(), completedAt);
    int downtime =
        command.downtimeMinutes() == null
            ? Math.toIntExact(Duration.between(current.startedAt(), completedAt).toMinutes())
            : command.downtimeMinutes();
    MaintenanceEvent saved =
        repository.save(
            current.complete(
                completedAt,
                downtime,
                command.nextScheduledAt(),
                new AuditMetadata(
                    current.audit().createdBy(), current.audit().createdAt(), actor, LocalDateTime.now(clock))));
    equipmentManagementService.transitionAvailability(
        current.inventoryItemId(),
        EquipmentProfile.STATUS_AVAILABLE,
        EquipmentAvailabilityChange.REASON_MAINTENANCE_COMPLETED,
        actor);
    auditRecorder.recordSystemEvent(
        current.tenantId(),
        "MaintenanceCompleted",
        "MaintenanceEvent",
        saved.maintenanceEventId(),
        "{\"inventoryItemId\":\"%s\"}".formatted(current.inventoryItemId()));
    return saved;
  }

  public List<MaintenanceEvent> listMaintenance(String inventoryItemId) {
    requireEquipmentItem(inventoryItemId);
    return repository.findByInventoryItemId(inventoryItemId);
  }

  private MaintenanceEvent requireMaintenance(String maintenanceEventId) {
    return repository
        .findById(requireText(maintenanceEventId, "Maintenance event id is required."))
        .orElseThrow(
            () ->
                new InventoryEntityNotFoundException(
                    "Maintenance event was not found.", InventoryErrorCodes.MAINTENANCE_NOT_FOUND));
  }

  private InventoryItem requireEquipmentItem(String inventoryItemId) {
    InventoryItem item = inventoryItemService.requireItem(inventoryItemId);
    if (!InventoryItem.ITEM_TYPE_EQUIPMENT.equals(item.itemType())) {
      throw new InventoryConflictException(
          "Inventory item is not equipment.",
          InventoryErrorCodes.MAINTENANCE_ITEM_TYPE_NOT_ELIGIBLE);
    }
    return item;
  }

  private static void validateCompletion(LocalDateTime startedAt, LocalDateTime completedAt) {
    if (completedAt != null && !completedAt.isAfter(startedAt)) {
      throw new InventoryConflictException(
          "Maintenance completion must be after start time.",
          InventoryErrorCodes.MAINTENANCE_COMPLETED_BEFORE_STARTED);
    }
  }

  private static String actorFrom(String performedBy, String externalTechnicianRef) {
    if (performedBy != null && !performedBy.isBlank()) {
      return performedBy;
    }
    return requireText(externalTechnicianRef, "Internal performer or external technician is required.");
  }

  private static String requireType(String value) {
    if (value == null || !MAINTENANCE_TYPES.contains(value)) {
      throw new InvalidInventoryCommandException(
          "Maintenance type is invalid. Allowed: " + MAINTENANCE_TYPES,
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

  public record RecordMaintenanceCommand(
      String maintenanceType,
      String performedBy,
      String externalTechnicianRef,
      String description,
      LocalDateTime startedAt,
      LocalDateTime completedAt,
      Integer downtimeMinutes,
      LocalDateTime nextScheduledAt) {}

  public record CompleteMaintenanceCommand(
      String actorId,
      LocalDateTime completedAt,
      Integer downtimeMinutes,
      LocalDateTime nextScheduledAt) {}
}
