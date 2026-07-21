import { fireEvent, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { LocaleProvider } from "../../i18n/LocaleContext";
import { CatalogItemLinesField, type CatalogItemLine } from "./CatalogItemLinesField";

const options = [
  { id: "t-1", kind: "test" as const, label: "Glucose" },
  { id: "p-1", kind: "panel" as const, label: "CBC" },
];

function Harness({ initialLines = [] as CatalogItemLine[], showQuantity = false }) {
  const onChangeSpy = vi.fn();
  const Wrapper = () => {
    return (
      <CatalogItemLinesField
        fieldId="picker"
        options={options}
        lines={initialLines}
        onChange={onChangeSpy}
        showQuantity={showQuantity}
        addLabel="Add"
        removeLabel="Remove"
        selectPlaceholder="Select"
        quantityLabel="Quantity"
      />
    );
  };
  return { Wrapper, onChangeSpy };
}

describe("CatalogItemLinesField", () => {
  it("adds the selected option as a new line", async () => {
    const { Wrapper, onChangeSpy } = Harness({});
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <Wrapper />
      </LocaleProvider>,
    );

    await user.selectOptions(screen.getByLabelText("Select"), ["test:t-1"]);
    await user.click(screen.getByRole("button", { name: "Add" }));

    expect(onChangeSpy).toHaveBeenCalledWith([
      { testDefinitionId: "t-1", catalogItemKind: "test" },
    ]);
  });

  it("does not add anything when nothing is selected", async () => {
    const { Wrapper, onChangeSpy } = Harness({});
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <Wrapper />
      </LocaleProvider>,
    );

    expect(screen.getByRole("button", { name: "Add" })).toBeDisabled();
    await user.click(screen.getByRole("button", { name: "Add" }));
    expect(onChangeSpy).not.toHaveBeenCalled();
  });

  it("excludes already-selected options and removes a line", async () => {
    const { Wrapper, onChangeSpy } = Harness({
      initialLines: [{ testDefinitionId: "t-1", catalogItemKind: "test" }],
    });
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <Wrapper />
      </LocaleProvider>,
    );

    expect(screen.queryByRole("option", { name: /Glucose/ })).not.toBeInTheDocument();
    await user.click(screen.getByRole("button", { name: "Remove" }));
    expect(onChangeSpy).toHaveBeenCalledWith([]);
  });

  it("shows a quantity input and updates quantity when showQuantity is set", async () => {
    const { Wrapper, onChangeSpy } = Harness({
      initialLines: [{ testDefinitionId: "p-1", catalogItemKind: "panel", quantity: 1 }],
      showQuantity: true,
    });
    render(
      <LocaleProvider>
        <Wrapper />
      </LocaleProvider>,
    );

    const quantityInput = screen.getByDisplayValue("1");
    fireEvent.change(quantityInput, { target: { value: "5" } });

    expect(onChangeSpy).toHaveBeenLastCalledWith([
      { testDefinitionId: "p-1", catalogItemKind: "panel", quantity: 5 },
    ]);
  });
});
