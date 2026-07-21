import "@testing-library/jest-dom/vitest";
import { expect, vi } from "vitest";
import { toHaveNoViolations } from "jest-axe";

expect.extend(toHaveNoViolations);

// jsdom does not implement scrollTo; Router.navigate() calls it on every navigation.
window.scrollTo = vi.fn();
