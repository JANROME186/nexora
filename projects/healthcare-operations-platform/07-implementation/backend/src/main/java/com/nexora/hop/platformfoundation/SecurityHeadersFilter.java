package com.nexora.hop.platformfoundation;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Applies baseline HTTP response security headers to every request across all modules
 * (MIME-sniffing and cross-origin-resource hardening), found missing by the OWASP ZAP API scan
 * run for HOP-QA-ALIGN-004. Lives at the application root package, not inside any single business
 * module, because it is genuine cross-cutting platform infrastructure rather than a capability
 * concern.
 */
@Component
class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        filterChain.doFilter(request, response);
    }
}
