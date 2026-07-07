# Nexora Aggregate Catalog

## Purpose

The Aggregate Catalog identifies the official Aggregate Roots and their owning bounded contexts.

This document prevents ambiguity about who owns and mutates business state.

## Initial Aggregate Roots

| Aggregate | Owner Context | Domain |
|---|---|---|
| Patient | Patient Management | Clinical |
| Laboratory | Organization Management | Identity |
| Branch | Organization Management | Identity |
| UserAccount | Identity & Access | Identity |
| Doctor | Medical Staff | Clinical |
| TestDefinition | Catalog & Test Configuration | Clinical |
| DiagnosticOrder | Orders & Samples | Clinical |
| Sample | Orders & Samples | Clinical |
| LaboratoryResult | Laboratory Results | Clinical |
| Sale | Cash & Sales | Business |
| CashRegister | Cash & Sales | Business |
| Invoice | Billing & Tax | Business |
| InventoryItem | Inventory & Procurement | Business |
| Supplier | Inventory & Procurement | Business |
| ImagingStudy | Imaging Operations | Clinical |
| MigrationJob | Data Migration & Portability | Platform |

## Mutation Rule

A bounded context may not directly mutate an aggregate owned by another bounded context.
