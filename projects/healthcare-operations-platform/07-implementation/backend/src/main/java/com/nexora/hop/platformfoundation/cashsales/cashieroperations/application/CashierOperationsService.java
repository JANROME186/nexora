package com.nexora.hop.platformfoundation.cashsales.cashieroperations.application;

import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.optionalText;
import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.requiredText;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashierOperationsRepository;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashSession;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.PaymentAllocation;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleLine;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleTotals;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesConflictException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesEntityNotFoundException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesErrorCodes;
import com.nexora.hop.platformfoundation.cashsales.shared.InvalidCashSalesCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.DiagnosticOrderManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.QuotationManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;

@Service
public class CashierOperationsService {

    private static final String DEFAULT_CURRENCY = "USD";

    private final CashierOperationsRepository repository;
    private final DiagnosticOrderManagementService diagnosticOrderService;
    private final QuotationManagementService quotationService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public CashierOperationsService(
            CashierOperationsRepository repository,
            DiagnosticOrderManagementService diagnosticOrderService,
            QuotationManagementService quotationService,
            AuditRecorder auditRecorder) {
        this(repository, diagnosticOrderService, quotationService, auditRecorder, Clock.systemUTC());
    }

    CashierOperationsService(
            CashierOperationsRepository repository,
            DiagnosticOrderManagementService diagnosticOrderService,
            QuotationManagementService quotationService,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.diagnosticOrderService = diagnosticOrderService;
        this.quotationService = quotationService;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public CashSession openSession(OpenCashSessionCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String openedBy = requiredText(command.openedBy(), "Opened-by actor is required.");
        repository.findOpenSession(tenantId, branchId).ifPresent(session -> {
            throw new CashSalesConflictException("An open cash session already exists for this branch.");
        });
        Money openingAmount = money(command.currency(), command.openingAmount());
        Instant now = Instant.now(clock);
        CashSession session = new CashSession(newId(), tenantId, laboratoryId, branchId, openedBy,
                openingAmount, openingAmount, null, null, null, CashSession.STATUS_OPEN, now, null);
        CashSession saved = repository.saveSession(session);
        auditRecorder.recordSystemEvent(tenantId, "CashSessionOpened", "CashSession", saved.sessionId(),
                "{\"branchId\":\"%s\"}".formatted(branchId));
        return saved;
    }

    public CashSession closeSession(String sessionId, CloseCashSessionCommand command) {
        CashSession session = requireSession(sessionId);
        if (!CashSession.STATUS_OPEN.equals(session.status())) {
            throw new CashSalesConflictException("Only an open cash session can be closed.");
        }
        Money countedAmount = money(command.currency(), command.countedAmount());
        Money expectedAmount = expectedAmount(session);
        BigDecimal variance = countedAmount.amount().subtract(expectedAmount.amount());
        if (variance.compareTo(BigDecimal.ZERO) != 0 && optionalText(command.varianceReason()) == null) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.CASH_VARIANCE_REASON_REQUIRED
                            + ": variance reason is required when counted cash differs from expected cash.");
        }
        CashSession closed = new CashSession(session.sessionId(), session.tenantId(), session.laboratoryId(),
                session.branchId(), session.openedBy(), session.openingAmount(), expectedAmount, countedAmount,
                new Money(countedAmount.currency(), variance), optionalText(command.varianceReason()),
                CashSession.STATUS_CLOSED, session.openedAt(), Instant.now(clock));
        CashSession saved = repository.saveSession(closed);
        auditRecorder.recordSystemEvent(session.tenantId(), "CashSessionClosed", "CashSession", session.sessionId(),
                "{\"branchId\":\"%s\",\"varianceAmount\":\"%s\"}"
                        .formatted(session.branchId(), variance.toPlainString()));
        return saved;
    }

    public Sale createSale(CreateSaleCommand command) {
        String sourceType = requiredText(command.sourceType(), "Sale source type is required.");
        String sourceReferenceId = requiredText(command.sourceReferenceId(), "Sale source reference id is required.");
        repository.findSaleBySource(sourceType, sourceReferenceId).ifPresent(existing -> {
            throw new CashSalesConflictException("A sale already exists for this source reference.");
        });
        if (Sale.SOURCE_DIAGNOSTIC_ORDER.equals(sourceType)) {
            return createFromDiagnosticOrder(command, sourceReferenceId);
        }
        if (Sale.SOURCE_QUOTATION.equals(sourceType)) {
            return createFromQuotation(command, sourceReferenceId);
        }
        throw new InvalidCashSalesCommandException("Sale source type is invalid.");
    }

    public PaymentAllocation registerPayment(String saleId, RegisterPaymentCommand command) {
        Sale sale = requireSale(saleId);
        if (Sale.STATUS_CANCELLED.equals(sale.status()) || Sale.STATUS_REFUNDED.equals(sale.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.SALE_TERMINAL_STATE_IMMUTABLE
                            + ": cancelled or refunded sales cannot receive payments.");
        }
        Money paymentAmount = money(command.currency(), command.amount());
        if (paymentAmount.amount().compareTo(sale.totals().outstandingAmount().amount()) > 0) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.PAYMENT_EXCEEDS_OUTSTANDING_BALANCE
                            + ": payment cannot exceed outstanding sale balance.");
        }
        String method = requiredText(command.method(), "Payment method is required.");
        CashSession session = null;
        if (PaymentAllocation.METHOD_CASH.equals(method)) {
            session = resolveOpenCashSession(sale, command.sessionId());
        }
        PaymentAllocation payment = new PaymentAllocation(newId(), saleId, session == null ? null : session.sessionId(),
                paymentAmount, method, optionalText(command.reference()),
                requiredText(command.registeredBy(), "Payment actor is required."), Instant.now(clock));
        PaymentAllocation saved = repository.savePayment(payment);
        Sale updated = withPayment(sale, paymentAmount);
        repository.saveSale(updated);
        auditRecorder.recordSystemEvent(sale.tenantId(), "SalePaymentRegistered", "Sale", saleId,
                "{\"method\":\"%s\",\"amount\":\"%s\"}".formatted(method, paymentAmount.amount().toPlainString()));
        return saved;
    }

    public Sale cancelSale(String saleId, String reasonCode) {
        Sale sale = requireSale(saleId);
        if (Sale.STATUS_PAID.equals(sale.status())) {
            throw new CashSalesConflictException("Paid sales require refund workflow before cancellation.");
        }
        Sale cancelled = new Sale(sale.saleId(), sale.tenantId(), sale.laboratoryId(), sale.branchId(),
                sale.patientId(), sale.sourceType(), sale.sourceReferenceId(), sale.totals(),
                Sale.STATUS_CANCELLED, requiredText(reasonCode, "Cancellation reason is required."),
                sale.actorId(), sale.version() + 1, sale.createdAt(), Instant.now(clock));
        Sale saved = repository.saveSale(cancelled);
        auditRecorder.recordSystemEvent(sale.tenantId(), "SaleCancelled", "Sale", sale.saleId(),
                "{\"reasonCode\":\"%s\"}".formatted(reasonCode));
        return saved;
    }

    public Sale getSale(String saleId) {
        return requireSale(saleId);
    }

    public List<Sale> listSales(String tenantId) {
        return repository.findSalesByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<SaleLine> listSaleLines(String saleId) {
        requireSale(saleId);
        return repository.findSaleLines(saleId);
    }

    public List<PaymentAllocation> listPayments(String saleId) {
        requireSale(saleId);
        return repository.findPayments(saleId);
    }

    public CashSession getSession(String sessionId) {
        return requireSession(sessionId);
    }

    public List<CashSession> listSessions(String tenantId) {
        return repository.findSessionsByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    private Sale createFromDiagnosticOrder(CreateSaleCommand command, String orderId) {
        DiagnosticOrder order = diagnosticOrderService.get(orderId);
        if (!DiagnosticOrder.STATUS_ACCEPTED.equals(order.status())
                && !DiagnosticOrder.STATUS_COMPLETED.equals(order.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.SALE_SOURCE_NOT_ACCEPTED
                            + ": diagnostic order must be accepted or completed before sale creation.");
        }
        if (order.pricingSnapshot() == null) {
            throw new CashSalesConflictException("Diagnostic order must have a pricing snapshot.");
        }
        List<OrderLine> orderLines = diagnosticOrderService.getOrderLines(orderId);
        String saleId = newId();
        for (OrderLine line : orderLines) {
            Money lineTotal = multiply(line.unitAmount(), line.quantity());
            repository.saveSaleLine(new SaleLine(newId(), saleId, line.testDefinitionId(),
                    line.catalogItemKind(), line.catalogItemName(), line.quantity(), line.unitAmount(), lineTotal));
        }
        return saveNewSale(command, saleId, order.tenantId(), order.laboratoryId(), order.branchId(),
                order.patientSnapshot().patientId(), Sale.SOURCE_DIAGNOSTIC_ORDER, orderId,
                order.pricingSnapshot().totalAmount());
    }

    private Sale createFromQuotation(CreateSaleCommand command, String quotationId) {
        QuotationRequest quotation = quotationService.get(quotationId);
        if (!QuotationRequest.STATUS_ACCEPTED.equals(quotation.status())
                && !QuotationRequest.STATUS_CONVERTED.equals(quotation.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.SALE_SOURCE_NOT_ACCEPTED
                            + ": quotation must be accepted or converted before sale creation.");
        }
        if (quotation.totalAmount() == null || quotation.patientId() == null) {
            throw new CashSalesConflictException("Quotation requires patient and total snapshots before sale creation.");
        }
        String saleId = newId();
        for (QuotationLine line : quotationService.getLines(quotationId)) {
            Money lineTotal = multiply(line.unitAmount(), line.quantity());
            repository.saveSaleLine(new SaleLine(newId(), saleId, line.testDefinitionId(),
                    line.catalogItemKind(), line.testDefinitionId(), line.quantity(), line.unitAmount(), lineTotal));
        }
        return saveNewSale(command, saleId, quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                quotation.patientId(), Sale.SOURCE_QUOTATION, quotationId, quotation.totalAmount());
    }

    private Sale saveNewSale(CreateSaleCommand command, String saleId, String tenantId, String laboratoryId,
            String branchId, String patientId, String sourceType, String sourceReferenceId, Money totalAmount) {
        if (optionalText(command.tenantId()) != null && !tenantId.equals(command.tenantId())) {
            throw new CashSalesConflictException("Sale source tenant does not match requested tenant.");
        }
        SaleTotals totals = new SaleTotals(totalAmount, new Money(totalAmount.currency(), BigDecimal.ZERO),
                totalAmount, new Money(totalAmount.currency(), BigDecimal.ZERO), totalAmount);
        Instant now = Instant.now(clock);
        Sale sale = new Sale(saleId, tenantId, laboratoryId, branchId, patientId, sourceType, sourceReferenceId,
                totals, Sale.STATUS_PAYABLE, null, optionalText(command.actorId()), 1, now, now);
        Sale saved = repository.saveSale(sale);
        auditRecorder.recordSystemEvent(tenantId, "SaleCreated", "Sale", saved.saleId(),
                "{\"sourceType\":\"%s\",\"sourceReferenceId\":\"%s\"}".formatted(sourceType, sourceReferenceId));
        return saved;
    }

    private CashSession resolveOpenCashSession(Sale sale, String requestedSessionId) {
        CashSession session = optionalText(requestedSessionId) == null
                ? repository.findOpenSession(sale.tenantId(), sale.branchId()).orElse(null)
                : requireSession(requestedSessionId);
        if (session == null || !CashSession.STATUS_OPEN.equals(session.status())
                || !sale.tenantId().equals(session.tenantId()) || !sale.branchId().equals(session.branchId())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.CASH_SESSION_REQUIRED
                            + ": cash payment requires an open session in the same tenant and branch.");
        }
        return session;
    }

    private Money expectedAmount(CashSession session) {
        BigDecimal cashPayments = repository.findSalesByTenantId(session.tenantId()).stream()
                .filter(sale -> session.branchId().equals(sale.branchId()))
                .flatMap(sale -> repository.findPayments(sale.saleId()).stream())
                .filter(payment -> session.sessionId().equals(payment.sessionId()))
                .map(payment -> payment.amount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Money(session.openingAmount().currency(), session.openingAmount().amount().add(cashPayments));
    }

    private Sale withPayment(Sale sale, Money payment) {
        BigDecimal paid = sale.totals().paidAmount().amount().add(payment.amount());
        BigDecimal outstanding = sale.totals().totalAmount().amount().subtract(paid);
        String status = outstanding.compareTo(BigDecimal.ZERO) == 0 ? Sale.STATUS_PAID : Sale.STATUS_PARTIALLY_PAID;
        SaleTotals totals = new SaleTotals(sale.totals().subtotalAmount(), sale.totals().discountAmount(),
                sale.totals().totalAmount(), new Money(payment.currency(), paid),
                new Money(payment.currency(), outstanding));
        return new Sale(sale.saleId(), sale.tenantId(), sale.laboratoryId(), sale.branchId(), sale.patientId(),
                sale.sourceType(), sale.sourceReferenceId(), totals, status, sale.cancellationReason(),
                sale.actorId(), sale.version() + 1, sale.createdAt(), Instant.now(clock));
    }

    private Sale requireSale(String saleId) {
        return repository.findSaleById(requiredText(saleId, "Sale id is required."))
                .orElseThrow(() -> new CashSalesEntityNotFoundException("Sale was not found."));
    }

    private CashSession requireSession(String sessionId) {
        return repository.findSessionById(requiredText(sessionId, "Cash session id is required."))
                .orElseThrow(() -> new CashSalesEntityNotFoundException("Cash session was not found."));
    }

    private static Money money(String currency, BigDecimal amount) {
        if (amount == null) {
            throw new InvalidCashSalesCommandException("Amount is required.");
        }
        String resolvedCurrency = optionalText(currency) == null ? DEFAULT_CURRENCY : currency.trim();
        return new Money(resolvedCurrency, amount);
    }

    private static Money multiply(Money amount, int quantity) {
        if (amount == null) {
            throw new CashSalesConflictException("Source line must have a priced amount.");
        }
        return new Money(amount.currency(), amount.amount().multiply(BigDecimal.valueOf(quantity)));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
