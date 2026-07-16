import { createRequire } from "node:module";

const require = createRequire(new URL("../employee-portal/node_modules/.eslint-loader.js", import.meta.url));
const js = require("@eslint/js");
const security = require("eslint-plugin-security");
const sonarjs = require("eslint-plugin-sonarjs");
const globals = require("globals");
const tseslint = require("typescript-eslint");

export default tseslint.config(
  {
    ignores: ["coverage", "node_modules"],
  },
  js.configs.recommended,
  ...tseslint.configs.recommended,
  security.configs.recommended,
  sonarjs.configs.recommended,
  {
    files: ["src/**/*.ts"],
    languageOptions: {
      ecmaVersion: "latest",
      globals: {
        ...globals.es2022,
      },
    },
    rules: {
      "complexity": ["warn", 18],
      "max-depth": ["warn", 4],
      "max-lines-per-function": ["warn", { max: 100, skipBlankLines: true, skipComments: true }],
      "security/detect-object-injection": "off",
      "sonarjs/cognitive-complexity": ["warn", 20],
      "sonarjs/no-duplicate-string": "warn",
    },
  },
  {
    files: ["src/test/**/*.ts"],
    languageOptions: {
      globals: {
        ...globals.es2022,
        ...globals.vitest,
      },
    },
    rules: {
      "max-lines-per-function": "off",
      "sonarjs/no-duplicate-string": "off",
    },
  },
);
