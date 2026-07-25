---
id: cash-api
format: markdown_structured_payload
---

# Cash Api

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
openapi: 3.1.0
info:
  title: Nexora Cash API
  version: 0.30.0
  description: Contract-first API for sales, payments, refunds and cash closing.
servers:
- url: /api/v1
security:
- bearerAuth: []
tags:
- name: Sales
- name: Payments
- name: Refunds
- name: Cash Drawer
- name: Cash Closing
paths:
  /sales:
    post:
      tags:
      - Sales
      summary: Create sale from order
      operationId: createSale
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateSaleRequest'
      responses:
        '201':
          description: Sale created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Sale'
    get:
      tags:
      - Sales
      summary: Search sales
      operationId: searchSales
      parameters:
      - in: query
        name: branchId
        schema:
          type: string
      - in: query
        name: status
        schema:
          type: string
      responses:
        '200':
          description: Sales list
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/Sale'
  /sales/{saleId}/payments:
    post:
      tags:
      - Payments
      summary: Register payment
      operationId: registerPayment
      parameters:
      - $ref: '#/components/parameters/SaleId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterPaymentRequest'
      responses:
        '201':
          description: Payment registered
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Payment'
  /sales/{saleId}/discounts:
    post:
      tags:
      - Sales
      summary: Apply discount
      operationId: applyDiscount
      parameters:
      - $ref: '#/components/parameters/SaleId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ApplyDiscountRequest'
      responses:
        '200':
          description: Discount applied or approval requested
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Sale'
  /sales/{saleId}/cancel:
    post:
      tags:
      - Sales
      summary: Cancel sale
      operationId: cancelSale
      parameters:
      - $ref: '#/components/parameters/SaleId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CancelSaleRequest'
      responses:
        '200':
          description: Sale cancelled
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Sale'
  /refunds:
    post:
      tags:
      - Refunds
      summary: Request refund
      operationId: requestRefund
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RefundRequest'
      responses:
        '201':
          description: Refund requested
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Refund'
  /cash-drawer-sessions:
    post:
      tags:
      - Cash Drawer
      summary: Open cash drawer session
      operationId: openCashDrawerSession
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OpenCashDrawerSessionRequest'
      responses:
        '201':
          description: Cash drawer session opened
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CashDrawerSession'
  /cash-drawer-sessions/{sessionId}/movements:
    post:
      tags:
      - Cash Drawer
      summary: Register manual cash movement
      operationId: registerCashMovement
      parameters:
      - $ref: '#/components/parameters/SessionId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterCashMovementRequest'
      responses:
        '201':
          description: Movement registered
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CashMovement'
  /cash-closings:
    post:
      tags:
      - Cash Closing
      summary: Request cash closing
      operationId: requestCashClosing
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RequestCashClosingRequest'
      responses:
        '201':
          description: Cash closing requested
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CashClosing'
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  parameters:
    SaleId:
      in: path
      name: saleId
      required: true
      schema:
        type: string
    SessionId:
      in: path
      name: sessionId
      required: true
      schema:
        type: string
  schemas:
    Money:
      type: object
      required:
      - amount
      - currency
      properties:
        amount:
          type: number
          format: decimal
        currency:
          type: string
          example: MXN
    CreateSaleRequest:
      type: object
      required:
      - orderId
      - branchId
      properties:
        orderId:
          type: string
        branchId:
          type: string
    Sale:
      type: object
      required:
      - id
      - orderId
      - status
      - total
      - balance
      properties:
        id:
          type: string
        orderId:
          type: string
        patientId:
          type: string
        branchId:
          type: string
        status:
          type: string
          enum:
          - DRAFT
          - PENDING_PAYMENT
          - PARTIALLY_PAID
          - PAID
          - CANCELLED
          - REFUNDED
          - ADJUSTED
        total:
          $ref: '#/components/schemas/Money'
        balance:
          $ref: '#/components/schemas/Money'
    RegisterPaymentRequest:
      type: object
      required:
      - method
      - amount
      properties:
        method:
          type: string
          enum:
          - CASH
          - CARD
          - TRANSFER
          - CHECK
          - OTHER
        amount:
          $ref: '#/components/schemas/Money'
        reference:
          type: string
    Payment:
      type: object
      properties:
        id:
          type: string
        saleId:
          type: string
        method:
          type: string
        amount:
          $ref: '#/components/schemas/Money'
        status:
          type: string
          enum:
          - REGISTERED
          - VOIDED
          - REFUNDED
    ApplyDiscountRequest:
      type: object
      required:
      - discountType
      - amount
      - reason
      properties:
        discountType:
          type: string
          enum:
          - PERCENTAGE
          - FIXED_AMOUNT
          - PROMOTION
        amount:
          type: number
        reason:
          type: string
    CancelSaleRequest:
      type: object
      required:
      - reason
      properties:
        reason:
          type: string
        refundRequested:
          type: boolean
    RefundRequest:
      type: object
      required:
      - paymentId
      - amount
      - reason
      properties:
        paymentId:
          type: string
        amount:
          $ref: '#/components/schemas/Money'
        reason:
          type: string
    Refund:
      type: object
      properties:
        id:
          type: string
        paymentId:
          type: string
        amount:
          $ref: '#/components/schemas/Money'
        status:
          type: string
          enum:
          - REQUESTED
          - APPROVED
          - REJECTED
          - PROCESSED
    OpenCashDrawerSessionRequest:
      type: object
      required:
      - branchId
      - openingAmount
      properties:
        branchId:
          type: string
        openingAmount:
          $ref: '#/components/schemas/Money'
    CashDrawerSession:
      type: object
      properties:
        id:
          type: string
        branchId:
          type: string
        cashierUserId:
          type: string
        status:
          type: string
          enum:
          - OPENED
          - SUSPENDED
          - PENDING_CLOSE
          - CLOSED
          - APPROVED
    RegisterCashMovementRequest:
      type: object
      required:
      - movementType
      - amount
      - reason
      properties:
        movementType:
          type: string
          enum:
          - CASH_IN
          - CASH_OUT
        amount:
          $ref: '#/components/schemas/Money'
        reason:
          type: string
    CashMovement:
      type: object
      properties:
        id:
          type: string
        sessionId:
          type: string
        movementType:
          type: string
        amount:
          $ref: '#/components/schemas/Money'
    RequestCashClosingRequest:
      type: object
      required:
      - sessionId
      - countedAmounts
      properties:
        sessionId:
          type: string
        countedAmounts:
          type: array
          items:
            type: object
            properties:
              method:
                type: string
              amount:
                $ref: '#/components/schemas/Money'
    CashClosing:
      type: object
      properties:
        id:
          type: string
        sessionId:
          type: string
        status:
          type: string
          enum:
          - REQUESTED
          - DIFFERENCE_DETECTED
          - APPROVED
          - REJECTED
```
