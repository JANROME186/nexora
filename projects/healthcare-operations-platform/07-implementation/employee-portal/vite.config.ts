/// <reference types="vitest/config" />
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test/setupTests.ts"],
    coverage: {
      provider: "v8",
      reporter: ["text", "html"],
      reportsDirectory: "coverage",
      exclude: ["dist/**", "coverage/**", "vite.config.ts", "src/main.tsx", "src/api/types.ts"],
      thresholds: {
        lines: 65,
        functions: 35,
        branches: 80,
        statements: 65
      }
    }
  }
});
