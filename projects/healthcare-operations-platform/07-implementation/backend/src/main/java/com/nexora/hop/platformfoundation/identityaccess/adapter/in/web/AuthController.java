package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nexora.hop.platformfoundation.identityaccess.application.IdentityAccessService;
import com.nexora.hop.platformfoundation.identityaccess.security.AuthenticatedUserContextHolder;
import com.nexora.hop.platformfoundation.identityaccess.security.HopAuthenticationResolver;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IdentityAccessService service;
    private final HopAuthenticationResolver authenticationResolver;

    public AuthController(IdentityAccessService service, HopAuthenticationResolver authenticationResolver) {
        this.service = service;
        this.authenticationResolver = authenticationResolver;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Locale parsedLocale = Locale.forLanguageTag(request.locale() != null ? request.locale() : "es-MX");
        String token = service.login(
                request.tenantId(), request.username(), request.password(), parsedLocale, request.mfaCode());
        return ResponseEntity.ok(new LoginResponse(token, 3600, parsedLocale.toLanguageTag()));
    }

    @PostMapping("/mfa/enroll")
    public ResponseEntity<MfaEnrollmentResponse> enrollMfa(HttpServletRequest request) {
        var context = authenticationResolver.resolve(request);
        if (context.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String secret = service.enrollMfa(context.get().userId());
        return ResponseEntity.ok(new MfaEnrollmentResponse(secret));
    }

    @PostMapping("/service-token")
    public ResponseEntity<LoginResponse> authenticateServiceAccount(
            @Valid @RequestBody ServiceAccountAuthRequest request) {
        String token = service.authenticateServiceAccount(request.clientId(), request.clientSecret());
        return ResponseEntity.ok(new LoginResponse(token, 3600, Locale.forLanguageTag("es-MX").toLanguageTag()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var context = authenticationResolver.resolve(request);
        if (context.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        service.logout(context.get().userId(), context.get().tenantId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assistance")
    public ResponseEntity<AssistanceResponse> initiateAssistance(@Valid @RequestBody AssistanceRequest request) {
        var context = AuthenticatedUserContextHolder.current();
        if (context.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String assistedToken = service.initiateAssistance(
                request.assistedUserId(),
                request.ticketReference(),
                context.get().userId());
        return ResponseEntity.ok(new AssistanceResponse(assistedToken));
    }

    public record LoginRequest(
            @NotBlank String tenantId,
            @NotBlank String username,
            @NotBlank String password,
            String locale,
            String mfaCode) {
    }

    public record LoginResponse(String token, int expiresIn, String locale) {
    }

    public record AssistanceRequest(
            @NotBlank String assistedUserId,
            @NotBlank String ticketReference) {
    }

    public record AssistanceResponse(String assistedToken) {
    }

    public record MfaEnrollmentResponse(String secret) {
    }

    public record ServiceAccountAuthRequest(
            @NotBlank String clientId,
            @NotBlank String clientSecret) {
    }
}
