export default {
  test: {
    environment: "node",
    coverage: {
      provider: "v8",
      reporter: ["text", "html"],
      reportsDirectory: "coverage",
      exclude: [
        "coverage/**",
        "eslint.config.js",
        "vitest.config.mjs",
        "src/test/**",
        "src/api/types.ts",
      ],
      thresholds: {
        lines: 80,
        functions: 80,
        branches: 75,
        statements: 80,
      },
    },
  },
};
