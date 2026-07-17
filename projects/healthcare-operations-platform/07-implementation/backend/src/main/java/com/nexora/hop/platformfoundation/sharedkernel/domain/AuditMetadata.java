package com.nexora.hop.platformfoundation.sharedkernel.domain;

import java.time.LocalDateTime;

public record AuditMetadata(
    String createdBy,
    LocalDateTime createdAt,
    String updatedBy,
    LocalDateTime updatedAt
) {}
