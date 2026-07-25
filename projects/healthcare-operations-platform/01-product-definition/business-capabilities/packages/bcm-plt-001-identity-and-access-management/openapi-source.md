---
id: openapi-source
format: markdown_structured_payload
---

# Openapi Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.0.3
info:
  title: Identity and Access Management API
  version: 0.1.0
  description: Authentication and authorization endpoints for HOP portals.
paths:
  /api/auth/login:
    post:
      summary: Authenticate user credentials
      operationId: loginUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                tenantId:
                  type: string
                  format: uuid
                username:
                  type: string
                password:
                  type: string
              required:
              - tenantId
              - username
              - password
      responses:
        '200':
          description: Successful authentication
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string
                  expiresIn:
                    type: integer
                  locale:
                    type: string
        '401':
          description: Invalid credentials
        '403':
          description: Account locked or suspended
  /api/auth/logout:
    post:
      summary: Invalidate current user session
      operationId: logoutUser
      responses:
        '204':
          description: Successful logout
        '401':
          description: Unauthorized session
  /api/auth/assistance:
    post:
      summary: Initiate support-assisted login
      operationId: initiateAssistance
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                assistedUserId:
                  type: string
                  format: uuid
                ticketReference:
                  type: string
              required:
              - assistedUserId
              - ticketReference
      responses:
        '200':
          description: Assistance session initiated
          content:
            application/json:
              schema:
                type: object
                properties:
                  assistedToken:
                    type: string
        '403':
          description: Access denied or missing consent
```
