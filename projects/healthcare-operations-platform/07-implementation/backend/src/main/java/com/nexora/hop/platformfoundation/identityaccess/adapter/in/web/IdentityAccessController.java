package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import java.net.URI;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.identityaccess.application.AssignRoleCommand;
import com.nexora.hop.platformfoundation.identityaccess.application.CreateUserCommand;
import com.nexora.hop.platformfoundation.identityaccess.application.IdentityAccessService;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;

@RestController
@RequestMapping("/api")
class IdentityAccessController {

    private final IdentityAccessService service;

    IdentityAccessController(IdentityAccessService service) {
        this.service = service;
    }

    @PostMapping("/identity/users")
    ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserAccount user = service.createUser(
                new CreateUserCommand(request.tenantId(), request.displayName(), request.email()));
        return ResponseEntity.created(URI.create("/api/identity/users/" + user.userId()))
                .body(UserResponse.from(user));
    }

    @GetMapping("/identity/users/{userId}")
    ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(UserResponse.from(service.getUser(userId)));
    }

    @PostMapping("/identity/users/{userId}/role-assignments")
    ResponseEntity<Void> assignRole(@PathVariable String userId, @Valid @RequestBody AssignRoleRequest request) {
        service.assignRole(userId, new AssignRoleCommand(
                request.roleCode(),
                request.scope() == null ? null : request.scope().type(),
                request.scope() == null ? null : request.scope().id()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    record CreateUserRequest(
            @NotBlank String tenantId,
            @NotBlank String displayName,
            @NotBlank @Email String email) {
    }

    record AssignRoleRequest(@NotBlank String roleCode, @NotNull @Valid ScopeRequest scope) {
    }

    record ScopeRequest(@NotBlank String type, @NotBlank String id) {
    }

    record UserResponse(
            String userId,
            String tenantId,
            String displayName,
            String email,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        static UserResponse from(UserAccount user) {
            return new UserResponse(
                    user.userId(),
                    user.tenantId(),
                    user.displayName(),
                    user.email(),
                    user.status(),
                    user.createdAt(),
                    user.updatedAt());
        }
    }
}
