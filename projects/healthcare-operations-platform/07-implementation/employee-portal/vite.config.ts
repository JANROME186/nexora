/// <reference types="vitest/config" />
import type { Plugin } from "vite";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Baseline security response headers for the dev server, matching the headers OWASP ZAP's
// baseline scan checks for (anti-clickjacking, MIME-sniffing, permissions policy, referrer
// policy, cross-origin isolation). Cross-Origin-Opener-Policy and Cross-Origin-Resource-Policy
// are safe to set to same-origin because this app loads no cross-origin subresources (see
// index.html). CSP and Cross-Origin-Embedder-Policy are intentionally NOT set here: Vite's dev
// server relies on eval-based HMR and same-origin module fetches without CORP headers that a
// strict CSP/COEP(require-corp) would break, so those two headers belong to the production
// hosting layer instead (tracked as TD-FE-005) rather than being faked here with a permissive
// dev-only policy.
function securityHeadersPlugin(): Plugin {
  return {
    name: "hop-dev-security-headers",
    configureServer(server) {
      server.middlewares.use((_req, res, next) => {
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        res.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        res.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        next();
      });
    },
  };
}

export default defineConfig({
  plugins: [react(), securityHeadersPlugin()],
  server: {
    // host.docker.internal lets a containerized local DAST scanner (OWASP ZAP) reach this dev
    // server from inside Docker Desktop's Linux VM; harmless for local-only dev tooling.
    allowedHosts: ["localhost", "127.0.0.1", "host.docker.internal"],
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test/setupTests.ts"],
    testTimeout: 20000,
    coverage: {
      provider: "v8",
      reporter: ["text", "html"],
      reportsDirectory: "coverage",
      exclude: ["dist/**", "coverage/**", "vite.config.ts", "src/main.tsx", "src/api/types.ts"],
      thresholds: {
        lines: 65,
        functions: 35,
        branches: 80,
        statements: 65,
      },
    },
  },
});
