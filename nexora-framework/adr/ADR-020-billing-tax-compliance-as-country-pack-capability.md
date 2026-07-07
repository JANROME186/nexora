# ADR-020 Billing and Tax Compliance as Country Pack Capability

## Status
Accepted

## Context

Nexora debe emitir comprobantes fiscales en múltiples países sin acoplar el núcleo del producto a una autoridad fiscal o proveedor específico.

## Decision

La facturación se implementará como capacidad de negocio independiente con adaptadores fiscales y Country Packs. El dominio fiscal no dependerá directamente de SAT, DIAN, SUNAT, PACs u otros proveedores.

## Consequences

- Las reglas fiscales variables se encapsulan por país.
- El núcleo mantiene estabilidad internacional.
- Los conectores fiscales pueden evolucionar como plugins/marketplace.
- Se requiere mayor disciplina en pruebas de contrato, idempotencia y auditoría.
