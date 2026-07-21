import "vitest";

interface AxeMatchers<R = unknown> {
  toHaveNoViolations(): R;
}

// Declaration merging with vitest's own `Assertion`/`AsymmetricMatchersContaining` interfaces
// requires `interface extends`, not a type alias, even though each body is otherwise empty.
/* eslint-disable @typescript-eslint/no-empty-object-type -- required shape for module augmentation */
declare module "vitest" {
  interface Assertion<T = unknown> extends AxeMatchers<T> {}
  interface AsymmetricMatchersContaining extends AxeMatchers {}
}
/* eslint-enable @typescript-eslint/no-empty-object-type */
