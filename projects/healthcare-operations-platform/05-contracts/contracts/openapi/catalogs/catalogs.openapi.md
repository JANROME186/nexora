---
id: catalogs.openapi
format: markdown_structured_payload
---

# Catalogs.Openapi

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Catalogs API
  version: 0.27.0
  description: Contract-first API for governed master catalogs and catalog values.
servers:
- url: /api/v1
security:
- bearerAuth: []
tags:
- name: Catalogs
paths:
  /catalogs:
    get:
      tags:
      - Catalogs
      operationId: listCatalogDefinitions
      summary: List catalog definitions
      parameters:
      - $ref: '#/components/parameters/TenantId'
      - name: status
        in: query
        schema:
          type: string
          enum:
          - draft
          - active
          - deprecated
          - retired
      responses:
        '200':
          description: Catalog definitions
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/CatalogDefinition'
    post:
      tags:
      - Catalogs
      operationId: createCatalogDefinition
      summary: Create catalog definition
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateCatalogDefinitionRequest'
      responses:
        '201':
          description: Created catalog
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CatalogDefinition'
  /catalogs/{catalogId}/values:
    get:
      tags:
      - Catalogs
      operationId: listCatalogValues
      summary: List catalog values
      parameters:
      - $ref: '#/components/parameters/CatalogId'
      - name: locale
        in: query
        schema:
          type: string
          example: es-MX
      responses:
        '200':
          description: Catalog values
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/CatalogValue'
    post:
      tags:
      - Catalogs
      operationId: addCatalogValue
      summary: Add catalog value
      parameters:
      - $ref: '#/components/parameters/CatalogId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AddCatalogValueRequest'
      responses:
        '201':
          description: Added value
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CatalogValue'
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  parameters:
    TenantId:
      name: x-tenant-id
      in: header
      required: true
      schema:
        type: string
    CatalogId:
      name: catalogId
      in: path
      required: true
      schema:
        type: string
        format: uuid
  schemas:
    CatalogDefinition:
      type: object
      required:
      - id
      - code
      - name
      - status
      - version
      properties:
        id:
          type: string
          format: uuid
        code:
          type: string
          example: SEX
        name:
          type: string
          example: Sex
        description:
          type: string
        scope:
          type: string
          enum:
          - global
          - tenant
          - branch
        status:
          type: string
          enum:
          - draft
          - active
          - deprecated
          - retired
        version:
          type: integer
    CatalogValue:
      type: object
      required:
      - id
      - code
      - displayName
      - status
      properties:
        id:
          type: string
          format: uuid
        code:
          type: string
        displayName:
          type: string
        localizedNames:
          type: object
          additionalProperties:
            type: string
        sortOrder:
          type: integer
        status:
          type: string
          enum:
          - active
          - inactive
          - deprecated
    CreateCatalogDefinitionRequest:
      type: object
      required:
      - code
      - name
      - scope
      properties:
        code:
          type: string
        name:
          type: string
        description:
          type: string
        scope:
          type: string
          enum:
          - global
          - tenant
          - branch
    AddCatalogValueRequest:
      type: object
      required:
      - code
      - displayName
      properties:
        code:
          type: string
        displayName:
          type: string
        localizedNames:
          type: object
          additionalProperties:
            type: string
```
