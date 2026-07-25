---
id: api-contract.openapi
format: markdown_structured_payload
---

# Api Contract.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: HOP Platform Foundation API
  version: 0.1.0
  description: Initial contract for tenant, laboratory, branch, user, role and audit
    baseline.
servers:
- url: /api
security:
- bearerAuth: []
paths:
  /platform/tenants:
    post:
      operationId: createTenant
      tags:
      - Platform Foundation
      summary: Create tenant
      x-actors:
      - ACT-001
      x-permissions:
      - manage_tenants
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateTenantRequest'
      responses:
        '201':
          description: Tenant created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TenantResponse'
  /platform/tenants/{tenantId}:
    get:
      operationId: getTenant
      tags:
      - Platform Foundation
      summary: Get tenant by id
      x-actors:
      - ACT-001
      x-permissions:
      - manage_tenants
      parameters:
      - name: tenantId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Tenant found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TenantResponse'
  /organization/laboratories:
    post:
      operationId: createLaboratory
      tags:
      - Organization
      summary: Create laboratory
      x-actors:
      - ACT-001
      - ACT-002
      x-permissions:
      - manage_laboratories
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateLaboratoryRequest'
      responses:
        '201':
          description: Laboratory created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LaboratoryResponse'
  /organization/laboratories/{laboratoryId}:
    get:
      operationId: getLaboratory
      tags:
      - Organization
      summary: Get laboratory by id
      x-actors:
      - ACT-001
      - ACT-002
      x-permissions:
      - manage_laboratories
      parameters:
      - name: laboratoryId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Laboratory found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LaboratoryResponse'
  /organization/branches:
    post:
      operationId: createBranch
      tags:
      - Organization
      summary: Create branch
      x-actors:
      - ACT-002
      - ACT-003
      x-permissions:
      - manage_branches
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateBranchRequest'
      responses:
        '201':
          description: Branch created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/BranchResponse'
  /organization/branches/{branchId}:
    get:
      operationId: getBranch
      tags:
      - Organization
      summary: Get branch by id
      x-actors:
      - ACT-002
      - ACT-003
      x-permissions:
      - manage_branches
      parameters:
      - name: branchId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: Branch found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/BranchResponse'
  /identity/users:
    post:
      operationId: createUser
      tags:
      - Identity
      summary: Create user account
      x-actors:
      - ACT-001
      - ACT-002
      - ACT-003
      x-permissions:
      - manage_users
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
      responses:
        '201':
          description: User created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
  /identity/users/{userId}:
    get:
      operationId: getUser
      tags:
      - Identity
      summary: Get user account by id
      x-actors:
      - ACT-001
      - ACT-002
      - ACT-003
      x-permissions:
      - manage_users
      parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          description: User found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
  /identity/users/{userId}/role-assignments:
    post:
      operationId: assignRole
      tags:
      - Identity
      summary: Assign scoped role
      x-actors:
      - ACT-001
      - ACT-002
      - ACT-003
      x-permissions:
      - manage_roles
      parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AssignRoleRequest'
      responses:
        '204':
          description: Role assigned
  /audit/events:
    get:
      operationId: searchAuditEvents
      tags:
      - Audit
      summary: Search audit events
      x-actors:
      - ACT-001
      - ACT-002
      - ACT-003
      - ACT-018
      x-permissions:
      - view_platform_audit
      - view_branch_audit
      - query_audit_events_with_authorization
      parameters:
      - name: tenantId
        in: query
        schema:
          type: string
      - name: subjectId
        in: query
        schema:
          type: string
      responses:
        '200':
          description: Audit events
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/AuditEventResponse'
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  schemas:
    CreateTenantRequest:
      type: object
      required:
      - name
      properties:
        name:
          type: string
    TenantResponse:
      type: object
      required:
      - tenantId
      - name
      properties:
        tenantId:
          type: string
        name:
          type: string
        status:
          type: string
    CreateLaboratoryRequest:
      type: object
      required:
      - tenantId
      - name
      properties:
        tenantId:
          type: string
        name:
          type: string
    LaboratoryResponse:
      type: object
      required:
      - laboratoryId
      - tenantId
      - name
      properties:
        laboratoryId:
          type: string
        tenantId:
          type: string
        name:
          type: string
        status:
          type: string
    CreateBranchRequest:
      type: object
      required:
      - laboratoryId
      - name
      properties:
        laboratoryId:
          type: string
        name:
          type: string
    BranchResponse:
      type: object
      required:
      - branchId
      - tenantId
      - laboratoryId
      - name
      properties:
        branchId:
          type: string
        tenantId:
          type: string
        laboratoryId:
          type: string
        name:
          type: string
        status:
          type: string
    CreateUserRequest:
      type: object
      required:
      - tenantId
      - displayName
      - email
      properties:
        tenantId:
          type: string
        displayName:
          type: string
        email:
          type: string
          format: email
    UserResponse:
      type: object
      required:
      - userId
      - tenantId
      - displayName
      - email
      - status
      properties:
        userId:
          type: string
        tenantId:
          type: string
        displayName:
          type: string
        email:
          type: string
        status:
          type: string
          enum:
          - created
          - active
          - locked
          - suspended
    AssignRoleRequest:
      type: object
      required:
      - roleCode
      - scope
      properties:
        roleCode:
          type: string
        scope:
          $ref: '#/components/schemas/AccessScope'
    AccessScope:
      type: object
      required:
      - type
      - id
      properties:
        type:
          type: string
          enum:
          - platform
          - tenant
          - laboratory
          - branch
        id:
          type: string
    AuditEventResponse:
      type: object
      required:
      - auditEventId
      - occurredAt
      - actorId
      - actorType
      - action
      - subjectType
      - subjectId
      - metadataJson
      properties:
        auditEventId:
          type: string
        occurredAt:
          type: string
          format: date-time
        tenantId:
          type: string
        actorId:
          type: string
        actorType:
          type: string
        action:
          type: string
        subjectType:
          type: string
        subjectId:
          type: string
        metadataJson:
          type: string
```
