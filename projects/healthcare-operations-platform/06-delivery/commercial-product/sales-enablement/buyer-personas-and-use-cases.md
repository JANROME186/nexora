# HOP Buyer Personas and Use Cases

## Overview

This document defines the primary buyer personas for the Healthcare Operations Platform with their pain points, goals, key use cases, and recommended packages.

## Persona 1: Laboratory Director

**Title:** Director / General Manager
**Organization:** Clinical diagnostic laboratory (1-10 branches)
**Role:** Primary decision maker
**Recommended Package:** Professional

**Pain Points:**
- Fragmented systems for catalog, orders, results, billing, and inventory
- Manual processes for result validation and delivery
- Limited visibility across branches
- High cost and vendor lock-in with current systems

**Key Use Cases:**
- End-to-end diagnostic workflow from order to result delivery
- Multi-branch operations with centralized catalog and pricing
- Digital result delivery to patients and referring doctors

## Persona 2: Quality Manager

**Title:** Quality Manager / QA Lead
**Organization:** Regulated laboratory or hospital lab department
**Role:** Influencer (drives quality/compliance requirements)
**Recommended Package:** Enterprise

**Pain Points:**
- Manual tracking of external quality controls
- Paper-based CAPA management
- Audit preparation requires manual evidence assembly
- Fragmented equipment calibration tracking

**Key Use Cases:**
- Internal and external QC management
- CAPA workflows with traceability
- Audit scheduling and evidence retention
- Equipment calibration and maintenance tracking

## Persona 3: IT Manager

**Title:** IT Manager / CTO
**Organization:** Multi-branch network or hospital group
**Role:** Technical evaluator
**Recommended Package:** Enterprise

**Pain Points:**
- Legacy systems difficult to integrate and maintain
- Vendor lock-in to proprietary infrastructure
- No API governance for external integrations
- Manual security and compliance auditing

**Key Use Cases:**
- Self-hosted deployment with Docker and PostgreSQL
- API management with rate limiting
- Data migration with dry-run validation
- Observability with Prometheus and structured logging

## Persona 4: Chief Financial Officer

**Title:** CFO / Finance Director
**Organization:** Any diagnostic laboratory
**Role:** Budget approver
**Recommended Package:** Professional

**Pain Points:**
- High licensing and maintenance costs for legacy systems
- Manual financial reconciliation
- Cash session discrepancies

**Key Use Cases:**
- Cash session management with variance tracking
- Billing request automation through fiscal adapters
- Financial audit trail
- Multi-price-list support

## Persona 5: Operations Manager

**Title:** Operations Manager / Branch Manager
**Organization:** Multi-branch diagnostic laboratory
**Role:** User champion
**Recommended Package:** Professional

**Pain Points:**
- Disconnected workflows between front desk, lab, and cashier
- No real-time sample status visibility
- Result delivery delays

**Key Use Cases:**
- Unified front desk worklist
- Diagnostic order creation with real-time pricing
- Sample tracking from collection through release
- Result notification to patients and doctors

## Related Documents

- [Sales Demo Script](sales-demo-script.md)
- [Customer Value Proposition](customer-value-proposition.md)
- [Commercial Packages](../commercial-packages/hop-commercial-packages.md)
