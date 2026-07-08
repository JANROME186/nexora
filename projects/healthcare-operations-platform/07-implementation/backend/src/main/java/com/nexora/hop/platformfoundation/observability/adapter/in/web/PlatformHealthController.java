package com.nexora.hop.platformfoundation.observability.adapter.in.web;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform")
class PlatformHealthController {

    private final String version;

    PlatformHealthController(@Value("${hop.platform-foundation.version:0.1.0-SNAPSHOT}") String version) {
        this.version = version;
    }

    @GetMapping("/health")
    ResponseEntity<PlatformHealthResponse> health() {
        return ResponseEntity.ok(new PlatformHealthResponse(
                "UP",
                "MVP-MOD-001",
                version,
                Instant.now()));
    }

    record PlatformHealthResponse(String status, String module, String version, Instant checkedAt) {
    }
}
