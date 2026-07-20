-- Generated/model-derived schema for COM-MOD-010 Inventory and Internal Quality.
-- Source models: BCM-INV-001 Product Catalog, BCM-INV-002 Reagent Management, BCM-INV-003 Lot
-- Management, BCM-INV-004 Procurement Management, BCM-INV-005 Stock Entries, BCM-INV-006 Stock
-- Exits, BCM-INV-007 Consumption Tracking, BCM-INV-008 Inventory Adjustments and BCM-INV-009
-- Waste Management, BCM-QLT-001 Internal Quality Controls, BCM-QLT-003 Calibration Management,
-- BCM-QLT-004 Equipment Management and BCM-QLT-005 Maintenance Management.

CREATE SCHEMA IF NOT EXISTS inventory_quality;

-- ============================================================
-- AGG-013 InventoryItem (BCM-INV-001, shared aggregate; delegated field mutation
-- authority granted to sibling BCM-INV-* / BCM-QLT-* capabilities).
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.inventory_items (
    inventory_item_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    item_code varchar(80) NOT NULL,
    item_name varchar(200) NOT NULL,
    item_type varchar(30) NOT NULL,
    classification varchar(40) NOT NULL,
    unit_of_measure varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    -- StockSummary fields; mutated only through delegated BCM-INV-005/006/007/008/009 commands.
    on_hand_quantity numeric(18,4) NOT NULL DEFAULT 0,
    reserved_quantity numeric(18,4) NOT NULL DEFAULT 0,
    reorder_point numeric(18,4),
    reorder_quantity numeric(18,4),
    last_movement_at timestamp with time zone,
    -- ReagentProfile fields (VO-CAT-002); mutated only through BCM-INV-002 AssignReagentProfile.
    reagent_linked_test_definition_id varchar(36),
    reagent_category varchar(30),
    reagent_consumption_ratio numeric(18,6),
    -- EquipmentProfile fields (VO-CAT-003); mutated only through BCM-QLT-004 commands.
    equipment_asset_tag varchar(80),
    equipment_serial_number varchar(80),
    equipment_manufacturer varchar(120),
    equipment_model varchar(120),
    equipment_installed_at timestamp with time zone,
    equipment_location varchar(200),
    equipment_availability_status varchar(30),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_inventory_items_code_per_scope
        UNIQUE (tenant_id, laboratory_id, branch_id, item_code)
);

CREATE INDEX IF NOT EXISTS idx_inventory_items_tenant
    ON inventory_quality.inventory_items (tenant_id, laboratory_id, branch_id);
CREATE INDEX IF NOT EXISTS idx_inventory_items_status
    ON inventory_quality.inventory_items (status);

-- ============================================================
-- StockLot (BCM-INV-003, new owned aggregate for lot-level metadata and remaining quantity).
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.stock_lots (
    stock_lot_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    lot_number varchar(80) NOT NULL,
    supplier_id varchar(36),
    supplier_name varchar(160),
    expiration_date date,
    received_quantity numeric(18,4) NOT NULL,
    remaining_quantity numeric(18,4) NOT NULL,
    status varchar(20) NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_stock_lots_number_per_item UNIQUE (inventory_item_id, lot_number)
);

CREATE INDEX IF NOT EXISTS idx_stock_lots_item ON inventory_quality.stock_lots (inventory_item_id);
CREATE INDEX IF NOT EXISTS idx_stock_lots_status ON inventory_quality.stock_lots (status);

-- ============================================================
-- PurchaseOrder + PurchaseOrderLine (BCM-INV-004).
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.purchase_orders (
    purchase_order_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    supplier_id varchar(36) NOT NULL,
    supplier_name varchar(160) NOT NULL,
    status varchar(20) NOT NULL,
    total_amount numeric(18,4) NOT NULL DEFAULT 0,
    currency_code varchar(8) NOT NULL,
    approver_id varchar(80),
    cancellation_reason varchar(500),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_purchase_orders_tenant
    ON inventory_quality.purchase_orders (tenant_id, laboratory_id, branch_id);

CREATE TABLE IF NOT EXISTS inventory_quality.purchase_order_lines (
    purchase_order_line_id varchar(36) PRIMARY KEY,
    purchase_order_id varchar(36) NOT NULL
        REFERENCES inventory_quality.purchase_orders (purchase_order_id) ON DELETE CASCADE,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    ordered_quantity numeric(18,4) NOT NULL,
    unit_cost numeric(18,4) NOT NULL,
    received_quantity numeric(18,4) NOT NULL DEFAULT 0,
    line_status varchar(20) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_purchase_order_lines_order
    ON inventory_quality.purchase_order_lines (purchase_order_id);

-- ============================================================
-- Stock movement records: entries (BCM-INV-005), exits (BCM-INV-006), consumption (BCM-INV-007),
-- adjustments (BCM-INV-008) and waste (BCM-INV-009). Each mutates InventoryItem.stockSummary
-- and (for lot-scoped records) StockLot.remainingQuantity through its owning service, never
-- through a direct write.
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.stock_entries (
    stock_entry_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    stock_lot_id varchar(36) REFERENCES inventory_quality.stock_lots (stock_lot_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    purchase_order_line_id varchar(36),
    quantity numeric(18,4) NOT NULL,
    entry_type varchar(30) NOT NULL,
    reason_code varchar(60),
    received_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_stock_entries_item
    ON inventory_quality.stock_entries (inventory_item_id);

CREATE TABLE IF NOT EXISTS inventory_quality.stock_exits (
    stock_exit_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    stock_lot_id varchar(36) NOT NULL REFERENCES inventory_quality.stock_lots (stock_lot_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    destination_branch_id varchar(36),
    quantity numeric(18,4) NOT NULL,
    exit_type varchar(30) NOT NULL,
    reason_code varchar(60),
    occurred_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_stock_exits_item
    ON inventory_quality.stock_exits (inventory_item_id);

CREATE TABLE IF NOT EXISTS inventory_quality.consumption_records (
    consumption_record_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    stock_lot_id varchar(36) REFERENCES inventory_quality.stock_lots (stock_lot_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    diagnostic_order_id varchar(36),
    test_definition_id varchar(36),
    consumed_quantity numeric(18,4) NOT NULL,
    consumption_context varchar(30) NOT NULL,
    occurred_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_consumption_records_item
    ON inventory_quality.consumption_records (inventory_item_id);

CREATE TABLE IF NOT EXISTS inventory_quality.inventory_adjustments (
    adjustment_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    stock_lot_id varchar(36) REFERENCES inventory_quality.stock_lots (stock_lot_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    delta_quantity numeric(18,4) NOT NULL,
    reason_code varchar(60) NOT NULL,
    reason_note varchar(500),
    approver_id varchar(80) NOT NULL,
    requested_by varchar(80) NOT NULL,
    occurred_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_inventory_adjustments_item
    ON inventory_quality.inventory_adjustments (inventory_item_id);

CREATE TABLE IF NOT EXISTS inventory_quality.waste_records (
    waste_record_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    stock_lot_id varchar(36) NOT NULL REFERENCES inventory_quality.stock_lots (stock_lot_id),
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    disposed_quantity numeric(18,4) NOT NULL,
    reason_code varchar(60) NOT NULL,
    reason_note varchar(500),
    disposed_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_waste_records_item
    ON inventory_quality.waste_records (inventory_item_id);

-- ============================================================
-- QualityControlRun (BCM-QLT-001). Control-material lots are read from StockLot and InventoryItem;
-- this table stores the append-only run evidence plus supervisor overrides.
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.quality_control_runs (
    qc_run_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    test_definition_id varchar(36) NOT NULL,
    control_material_stock_lot_id varchar(36) NOT NULL
        REFERENCES inventory_quality.stock_lots (stock_lot_id),
    measured_value numeric(18,6) NOT NULL,
    expected_min numeric(18,6) NOT NULL,
    expected_max numeric(18,6) NOT NULL,
    expected_captured_at timestamp with time zone NOT NULL,
    rule_evaluation varchar(30) NOT NULL,
    acceptance_decision varchar(30) NOT NULL,
    linked_laboratory_result_ids text,
    performed_by varchar(80) NOT NULL,
    performed_at timestamp with time zone NOT NULL,
    evidence_reference varchar(500),
    override_reason varchar(500),
    override_by varchar(80),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT ck_quality_control_expected_range CHECK (expected_min <= expected_max)
);

CREATE INDEX IF NOT EXISTS idx_quality_control_runs_scope
    ON inventory_quality.quality_control_runs (tenant_id, laboratory_id, branch_id);
CREATE INDEX IF NOT EXISTS idx_quality_control_runs_lot
    ON inventory_quality.quality_control_runs (control_material_stock_lot_id);

-- ============================================================
-- CalibrationEvent (BCM-QLT-003). A failed result is consumed by BCM-QLT-004 to set the equipment
-- availability status to out_of_service.
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.calibration_events (
    calibration_event_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    tenant_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    calibration_standard_ref varchar(160) NOT NULL,
    performed_by varchar(80) NOT NULL,
    performed_at timestamp with time zone NOT NULL,
    result varchar(30) NOT NULL,
    next_due_date date,
    certificate_reference varchar(500),
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_calibration_events_item
    ON inventory_quality.calibration_events (inventory_item_id);

-- ============================================================
-- EquipmentAvailabilityChange (BCM-QLT-004). Append-only history for delegated mutations to
-- InventoryItem.equipmentProfile.availabilityStatus.
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.equipment_availability_changes (
    change_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    tenant_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    previous_status varchar(30),
    new_status varchar(30) NOT NULL,
    reason_code varchar(60) NOT NULL,
    changed_by varchar(80) NOT NULL,
    changed_at timestamp with time zone NOT NULL,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_equipment_availability_item
    ON inventory_quality.equipment_availability_changes (inventory_item_id);

-- ============================================================
-- MaintenanceEvent (BCM-QLT-005). Scheduling and completion events coordinate equipment
-- availability through BCM-QLT-004.
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_quality.maintenance_events (
    maintenance_event_id varchar(36) PRIMARY KEY,
    inventory_item_id varchar(36) NOT NULL
        REFERENCES inventory_quality.inventory_items (inventory_item_id),
    tenant_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    maintenance_type varchar(30) NOT NULL,
    performed_by varchar(80),
    external_technician_ref varchar(160),
    description varchar(1000) NOT NULL,
    started_at timestamp with time zone NOT NULL,
    completed_at timestamp with time zone,
    downtime_minutes integer,
    next_scheduled_at timestamp with time zone,
    created_by varchar(80) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_by varchar(80) NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT ck_maintenance_completion_after_start
        CHECK (completed_at IS NULL OR completed_at > started_at)
);

CREATE INDEX IF NOT EXISTS idx_maintenance_events_item
    ON inventory_quality.maintenance_events (inventory_item_id);
