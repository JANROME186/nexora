package com.nexora.hop.platformfoundation.cashsales.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.cashsales")
class CashSalesExceptionHandler {

    @ExceptionHandler(InvalidCashSalesCommandException.class)
    ResponseEntity<ProblemDetail> invalidCommand(InvalidCashSalesCommandException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid cash sales command");
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(CashSalesConflictException.class)
    ResponseEntity<ProblemDetail> conflict(CashSalesConflictException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Cash sales conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(CashSalesEntityNotFoundException.class)
    ResponseEntity<ProblemDetail> notFound(CashSalesEntityNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Cash sales entity not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
