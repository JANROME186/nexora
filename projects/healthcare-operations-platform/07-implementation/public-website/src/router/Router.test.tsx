import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it } from "vitest";
import { Link, RouterProvider, useRouter } from "./Router";

function LocationProbe() {
  const { pathname } = useRouter();
  return <span data-testid="pathname">{pathname}</span>;
}

beforeEach(() => {
  window.history.pushState({}, "", "/");
});

describe("Router", () => {
  it("throws when useRouter is used outside a RouterProvider", () => {
    const Broken = () => {
      useRouter();
      return null;
    };
    expect(() => render(<Broken />)).toThrow(/RouterProvider/);
  });

  it("navigates on a plain left click without a full page load", async () => {
    const user = userEvent.setup();
    render(
      <RouterProvider>
        <LocationProbe />
        <Link to="/services">Services</Link>
      </RouterProvider>,
    );

    expect(screen.getByTestId("pathname")).toHaveTextContent("/");
    await user.click(screen.getByText("Services"));
    expect(screen.getByTestId("pathname")).toHaveTextContent("/services");
    expect(window.location.pathname).toBe("/services");
  });

  it("does not navigate when a modifier key is held", async () => {
    const user = userEvent.setup();
    render(
      <RouterProvider>
        <LocationProbe />
        <Link to="/services">Services</Link>
      </RouterProvider>,
    );

    await user.keyboard("[MetaLeft>]");
    await user.click(screen.getByText("Services"));
    await user.keyboard("[/MetaLeft]");
    expect(screen.getByTestId("pathname")).toHaveTextContent("/");
  });

  it("updates pathname on popstate", async () => {
    render(
      <RouterProvider>
        <LocationProbe />
      </RouterProvider>,
    );

    window.history.pushState({}, "", "/tests");
    window.dispatchEvent(new PopStateEvent("popstate"));
    await waitFor(() => expect(screen.getByTestId("pathname")).toHaveTextContent("/tests"));
  });
});
