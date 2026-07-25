---
id: organizations.openapi
format: markdown_structured_payload
---

# Organizations.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Organizations API
  version: 0.24.0
  description: API contract for tenants, organizations, branches and organizational
    structure.
servers:
- url: https://api.nexora.local
  description: Local/API gateway
paths:
  /v1/tenants:
    post:
      summary: Create tenant
      operationId: createTenant
      tags:
      - Tenants
      responses:
        '201':
          description: Tenant created
  /v1/tenants/{tenantId}:
    get:
      summary: Get tenant
      operationId: getTenant
      tags:
      - Tenants
      parameters:
      - name: tenantId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Tenant
    patch:
      summary: Update tenant
      operationId: updateTenant
      tags:
      - Tenants
      parameters:
      - name: tenantId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Tenant updated
  /v1/tenants/{tenantId}/activate:
    post:
      summary: Activate tenant
      operationId: activateTenant
      tags:
      - Tenants
      parameters:
      - name: tenantId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Tenant activated
  /v1/branches:
    post:
      summary: Create branch
      operationId: createBranch
      tags:
      - Branches
      responses:
        '201':
          description: Branch created
    get:
      summary: List branches
      operationId: listBranches
      tags:
      - Branches
      responses:
        '200':
          description: Branch list
  /v1/branches/{branchId}:
    get:
      summary: Get branch
      operationId: getBranch
      tags:
      - Branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Branch
    patch:
      summary: Update branch
      operationId: updateBranch
      tags:
      - Branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Branch updated
  /v1/branches/{branchId}/activate:
    post:
      summary: Activate branch
      operationId: activateBranch
      tags:
      - Branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Branch activated
  /v1/branches/{branchId}/deactivate:
    post:
      summary: Deactivate branch
      operationId: deactivateBranch
      tags:
      - Branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Branch deactivated
  /v1/branches/{branchId}/availability:
    get:
      summary: Check branch service availability
      operationId: checkBranchAvailability
      tags:
      - Branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      - name: serviceCode
        in: query
        required: false
        schema:
          type: string
      responses:
        '200':
          description: Availability result
components:
  schemas:
    Tenant:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
        status:
          type: string
          enum:
          - Draft
          - Active
          - Suspended
          - Cancelled
    Branch:
      type: object
      properties:
        id:
          type: string
        tenantId:
          type: string
        code:
          type: string
        name:
          type: string
        status:
          type: string
          enum:
          - Draft
          - ReadyForActivation
          - Active
          - TemporarilyClosed
          - Inactive
          - Archived
```
