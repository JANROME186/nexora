import { render, type RenderResult } from "@testing-library/react";
import type { ReactElement } from "react";
import { LocaleProvider } from "../i18n/LocaleContext";
import { RouterProvider } from "../router/Router";

export function renderWithProviders(ui: ReactElement, path = "/"): RenderResult {
  window.history.pushState({}, "", path);
  return render(
    <LocaleProvider>
      <RouterProvider>{ui}</RouterProvider>
    </LocaleProvider>,
  );
}
