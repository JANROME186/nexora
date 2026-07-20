package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityRepository;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** BCM-QLT-004 Equipment Management delegated writer for InventoryItem.equipmentProfile. */
@Service
public class EquipmentManagementService {

  private static final Set<String> AVAILABILITY_STATUSES =
      Set.of(
          EquipmentProfile.STATUS_AVAILABLE,
          EquipmentProfile.STATUS_IN_USE,
          EquipmentProfile.STATUS_OUT_OF_SERVICE,
          EquipmentProfile.STATUS_RETIRED);

  private static final Set<String> REASON_CODES =
      Set.of(
          EquipmentAvailabilityChange.REASON_ROUTINE,
          EquipmentAvailabilityChange.REASON_CALIBRATION_FAILED,
          EquipmentAvailabilityChange.REASON_MAINTENANCE_SCHEDULED,
          EquipmentAvailabilityChange.REASON_MAINTENANCE_COMPLETED,
          EquipmentAvailabilityChange.REASON_DECOMMISSIONED,
          EquipmentAvailabilityChange.REASON_OTHER);

  private final EquipmentAvailabilityRepository repository;
  private final InventoryItemService inventoryItemService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public EquipmentManagementService(
      EquipmentAvailabilityRepository repository,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder) {
    this(repository, inventoryItemService, auditRecorder, Clock.systemUTC());
  }

  EquipmentManagementService(
      EquipmentAvailabilityRepository repository,
      InventoryItemService inventoryItemService,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.inventoryItemService = inventoryItemService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public InventoryItem setEquipmentProfile(String inventoryItemId, SetEquipmentProfileCommand command) {
    InventoryItem item = requireEquipmentItem(inventoryItemId);
    String actor = requireText(command.actorId(), "Actor id is required.");
    String status =
        requireEnum(
            command.availabilityStatus(),
            AVAILABILITY_STATUSES,
            "Equipment availability status is invalid.");
    EquipmentProfile profile =
        new EquipmentProfile(
            requireText(command.assetTag(), "Asset tag is required."),
            command.serialNumber(),
            command.manufacturer(),
            command.model(),
            command.installedAt(),
            command.location(),
            status);
    LocalDateTime now = LocalDateTime.now(clock);
    InventoryItem saved =
        inventoryItemService.save(
            item.withEquipmentProfile(
                profile,
                new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    recordChange(item, status, EquipmentAvailabilityChange.REASON_ROUTINE, actor, now);
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "EquipmentProfileSet",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"assetTag\":\"%s\",\"availabilityStatus\":\"%s\"}".formatted(profile.assetTag(), status));
    return saved;
  }

  public InventoryItem changeAvailability(
      String inventoryItemId, ChangeEquipmentAvailabilityCommand command) {
    return transitionAvailability(
        inventoryItemId,
        command.newStatus(),
        command.reasonCode(),
        requireText(command.actorId(), "Actor id is required."));
  }

  public InventoryItem transitionAvailability(
      String inventoryItemId, String newStatus, String reasonCode, String actorId) {
    InventoryItem item = requireEquipmentItem(inventoryItemId);
    if (item.equipmentProfile() == null) {
      throw new InventoryConflictException(
          "Equipment profile must be set before changing availability.",
          InventoryErrorCodes.EQUIPMENT_PROFILE_MISSING);
    }
    String target = requireEnum(newStatus, AVAILABILITY_STATUSES, "Equipment status is invalid.");
    String reason = requireEnum(reasonCode, REASON_CODES, "Equipment reason code is invalid.");
    if (EquipmentProfile.STATUS_RETIRED.equals(item.equipmentProfile().availabilityStatus())
        && !EquipmentProfile.STATUS_RETIRED.equals(target)) {
      throw new InventoryConflictException(
          "Retired equipment cannot transition back to an active status.",
          InventoryErrorCodes.EQUIPMENT_RETIRED_TRANSITION_FORBIDDEN);
    }
    String actor = actorId == null || actorId.isBlank() ? "system" : actorId;
    LocalDateTime now = LocalDateTime.now(clock);
    EquipmentProfile updated = item.equipmentProfile().withAvailabilityStatus(target);
    InventoryItem saved =
        inventoryItemService.save(
            item.withEquipmentProfile(
                updated,
                new AuditMetadata(item.audit().createdBy(), item.audit().createdAt(), actor, now)));
    recordChange(item, target, reason, actor, now);
    auditRecorder.recordSystemEvent(
        item.tenantId(),
        "EquipmentAvailabilityChanged",
        "InventoryItem",
        item.inventoryItemId(),
        "{\"newStatus\":\"%s\",\"reasonCode\":\"%s\"}".formatted(target, reason));
    return saved;
  }

  public EquipmentProfile getEquipmentProfile(String inventoryItemId) {
    InventoryItem item = requireEquipmentItem(inventoryItemId);
    if (item.equipmentProfile() == null) {
      throw new InventoryConflictException(
          "Equipment profile has not been set.", InventoryErrorCodes.EQUIPMENT_PROFILE_MISSING);
    }
    return item.equipmentProfile();
  }

  public List<EquipmentAvailabilityChange> listAvailabilityChanges(String inventoryItemId) {
    requireEquipmentItem(inventoryItemId);
    return repository.findByInventoryItemId(inventoryItemId);
  }

  private InventoryItem requireEquipmentItem(String inventoryItemId) {
    InventoryItem item = inventoryItemService.requireItem(inventoryItemId);
    if (!InventoryItem.ITEM_TYPE_EQUIPMENT.equals(item.itemType())) {
      throw new InventoryConflictException(
          "Inventory item is not equipment.", InventoryErrorCodes.EQUIPMENT_ITEM_TYPE_NOT_ELIGIBLE);
    }
    return item;
  }

  private void recordChange(
      InventoryItem item, String newStatus, String reasonCode, String actor, LocalDateTime now) {
    String previous =
        item.equipmentProfile() == null
            ? null
            : item.equipmentProfile().availabilityStatus();
    repository.save(
        new EquipmentAvailabilityChange(
            UUID.randomUUID().toString(),
            item.inventoryItemId(),
            item.tenantId(),
            item.branchId(),
            previous,
            newStatus,
            reasonCode,
            actor,
            now,
            new AuditMetadata(actor, now, actor, now)));
  }

  private static String requireText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          message, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  private static String requireEnum(String value, Set<String> allowed, String message) {
    if (value == null || !allowed.contains(value)) {
      throw new InvalidInventoryCommandException(
          message + " Allowed: " + allowed, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  public record SetEquipmentProfileCommand(
      String assetTag,
      String serialNumber,
      String manufacturer,
      String model,
      LocalDateTime installedAt,
      String location,
      String availabilityStatus,
      String actorId) {}

  public record ChangeEquipmentAvailabilityCommand(
      String newStatus, String reasonCode, String actorId) {}
}
