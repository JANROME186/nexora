# Technical Prerequisites Checklist

## Overview

This checklist establishes the infrastructure, software runtime, system resource, and network prerequisites required prior to deploying and onboarding a tenant on **Healthcare Operations Platform (HOP)**.

## Infrastructure & Runtime Stack Requirements

| Component / Layer | Technology Standard | Minimum Version | Recommended Commercial Version | Notes |
|---|---|---|---|---|
| **Operating System** | Linux (Ubuntu/Debian/RHEL) or WSL2 | Linux Kernel 5.15+ | Ubuntu 24.04 LTS / RHEL 9 | Standard container host |
| **Container Engine** | Docker Engine & Docker Compose | Docker 24.0+ / Compose v2.20+ | Docker Engine 26.0+ | Mandatory runtime environment |
| **Relational Database**| PostgreSQL | PostgreSQL 16.0 | PostgreSQL 16.6+ | Self-hosted or managed DB |
| **Backend Runtime** | Java OpenJDK | Java 21 LTS | Eclipse Temurin 21.0.x | Spring Boot 3.x backend |
| **Frontend Runtimes** | Node.js & npm | Node 20.x LTS / npm 10.x | Node 20.11+ LTS | Vite / React 19 builds |
| **HTTP Proxy / TLS** | Nginx or Caddy | Nginx 1.24+ | Caddy 2.7+ / Nginx 1.26+ | Reverse proxy with TLS 1.3 |

## Hardware Resource Allocations

### Minimum Local / Small Tenant Topology
- **CPU**: 4 vCPUs.
- **RAM**: 8 GB RAM (Backend 2 GB heap, PostgreSQL 2 GB, Portals 1 GB total, OS 3 GB).
- **Disk Storage**: 50 GB SSD (NVMe preferred for database I/O).

### Recommended Commercial Enterprise Topology
- **CPU**: 16 vCPUs.
- **RAM**: 32 GB RAM.
- **Disk Storage**: 500 GB NVMe (Automated WAL archiving and daily snapshot storage).

## Network & Port Allocations

| Default Port | Service Surface | Transport Protocol | Internal / External Access |
|---|---|---|---|
| `8080` / `8090` | HOP Backend REST API & Actuator | HTTP / HTTPS | Internal & Reverse Proxy |
| `3000` | Employee Portal (Vite/React) | HTTP / HTTPS | Staff Workstations |
| `3001` | Public Website (Vite/React) | HTTP / HTTPS | Internet Anonymous Access |
| `3002` | Patient Portal | HTTP / HTTPS | Patient Access |
| `3003` | Doctor Portal | HTTP / HTTPS | Referring Doctor Access |
| `5432` | PostgreSQL Database Container | TCP | Database Connections Only |

## Pre-Deployment Verification Checklist

- [ ] Docker Engine and Compose installed and verified via `docker compose version`.
- [ ] PostgreSQL 16 container running and accessible with `pg_isready`.
- [ ] Required database schemas initialized (`db/platform-foundation/schema.sql`, `db/catalog-test-configuration/schema.sql`, `db/inventory-and-internal-quality/schema.sql`, etc.).
- [ ] TLS certificates installed on reverse proxy for production domains.
- [ ] CORS policies configured to allow authorized portal origins only.
- [ ] Network firewall rules restricting database port `5432` to internal container network.
- [ ] Automated backup routine verified via `pg_dump` test dry-run.
