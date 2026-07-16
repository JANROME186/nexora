-- Generated/model-derived schema for MVP-MOD-005 Cashier and Billing Request.
-- Source models: BCM-ATT-005 Cashier Operations and BCM-ATT-008 Billing Request Management.
-- Country-specific fiscal adapters remain outside this schema and attach through adapter state.

CREATE SCHEMA IF NOT EXISTS cash_sales;

CREATE TABLE IF NOT EXISTS cash_sales.cash_sessions (
    session_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    opened_by varchar(80) NOT NULL,
    opening_amount numeric(14,2) NOT NULL,
    opening_currency varchar(3) NOT NULL,
    expected_amount numeric(14,2),
    expected_currency varchar(3),
    counted_amount numeric(14,2),
    counted_currency varchar(3),
    variance_amount numeric(14,2),
    variance_currency varchar(3),
    variance_reason varchar(240),
    status varchar(20) NOT NULL,
    opened_at timestamp with time zone NOT NULL,
    closed_at timestamp with time zone
);

CREATE INDEX IF NOT EXISTS idx_cash_sessions_tenant_branch_status
    ON cash_sales.cash_sessions (tenant_id, branch_id, status);

CREATE TABLE IF NOT EXISTS cash_sales.sales (
    sale_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    source_type varchar(40) NOT NULL,
    source_reference_id varchar(36) NOT NULL,
    subtotal_amount numeric(14,2) NOT NULL,
    subtotal_currency varchar(3) NOT NULL,
    discount_amount numeric(14,2) NOT NULL,
    discount_currency varchar(3) NOT NULL,
    total_amount numeric(14,2) NOT NULL,
    total_currency varchar(3) NOT NULL,
    paid_amount numeric(14,2) NOT NULL,
    paid_currency varchar(3) NOT NULL,
    outstanding_amount numeric(14,2) NOT NULL,
    outstanding_currency varchar(3) NOT NULL,
    status varchar(20) NOT NULL,
    cancellation_reason varchar(240),
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_sales_source UNIQUE (source_type, source_reference_id)
);

CREATE INDEX IF NOT EXISTS idx_sales_tenant ON cash_sales.sales (tenant_id);

CREATE TABLE IF NOT EXISTS cash_sales.sale_lines (
    sale_line_id varchar(36) PRIMARY KEY,
    sale_id varchar(36) NOT NULL REFERENCES cash_sales.sales (sale_id),
    catalog_item_id varchar(36) NOT NULL,
    catalog_item_kind varchar(20) NOT NULL,
    description_snapshot varchar(240),
    quantity integer NOT NULL,
    unit_amount numeric(14,2) NOT NULL,
    unit_currency varchar(3) NOT NULL,
    line_total_amount numeric(14,2) NOT NULL,
    line_total_currency varchar(3) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sale_lines_sale ON cash_sales.sale_lines (sale_id);

CREATE TABLE IF NOT EXISTS cash_sales.payment_allocations (
    payment_id varchar(36) PRIMARY KEY,
    sale_id varchar(36) NOT NULL REFERENCES cash_sales.sales (sale_id),
    session_id varchar(36),
    amount numeric(14,2) NOT NULL,
    currency varchar(3) NOT NULL,
    method varchar(20) NOT NULL,
    reference varchar(120),
    registered_by varchar(80) NOT NULL,
    registered_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payment_allocations_sale ON cash_sales.payment_allocations (sale_id);

CREATE TABLE IF NOT EXISTS cash_sales.invoice_requests (
    invoice_request_id varchar(36) PRIMARY KEY,
    tenant_id varchar(36) NOT NULL,
    laboratory_id varchar(36) NOT NULL,
    branch_id varchar(36) NOT NULL,
    sale_id varchar(36) NOT NULL,
    patient_id varchar(36) NOT NULL,
    fiscal_legal_name varchar(240) NOT NULL,
    fiscal_tax_identifier varchar(80) NOT NULL,
    fiscal_address varchar(500) NOT NULL,
    fiscal_regime varchar(120),
    fiscal_captured_at timestamp with time zone NOT NULL,
    status varchar(20) NOT NULL,
    adapter_correlation_id varchar(120),
    adapter_response_snapshot text,
    actor_id varchar(80),
    version integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    CONSTRAINT uq_invoice_requests_sale UNIQUE (sale_id)
);

CREATE INDEX IF NOT EXISTS idx_invoice_requests_tenant ON cash_sales.invoice_requests (tenant_id);

CREATE TABLE IF NOT EXISTS cash_sales.invoice_tax_lines (
    tax_line_id varchar(36) PRIMARY KEY,
    invoice_request_id varchar(36) NOT NULL REFERENCES cash_sales.invoice_requests (invoice_request_id),
    base_amount numeric(14,2) NOT NULL,
    base_currency varchar(3) NOT NULL,
    tax_code varchar(40) NOT NULL,
    tax_rate numeric(8,4) NOT NULL,
    tax_amount numeric(14,2) NOT NULL,
    tax_currency varchar(3) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_invoice_tax_lines_request
    ON cash_sales.invoice_tax_lines (invoice_request_id);
