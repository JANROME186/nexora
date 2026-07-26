/**
 * AI Overlay bounded context compiled from COM-MOD-015 (BCM-AI-001 through BCM-AI-008).
 * The module exposes provider-neutral assistant orchestration, safety policy and audit outputs.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "AI Overlay",
        allowedDependencies = {"sharedkernel", "auditcompliance", "identityaccess"})
package com.nexora.hop.platformfoundation.aioverlay;
