import { useState } from "react";
import type { PublicCatalogItemKind } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { CatalogItemOption } from "../../state/useCatalogItemOptions";

export interface CatalogItemLine {
  testDefinitionId: string;
  catalogItemKind: PublicCatalogItemKind;
  quantity?: number;
}

interface CatalogItemLinesFieldProps {
  fieldId: string;
  options: CatalogItemOption[];
  lines: CatalogItemLine[];
  onChange: (lines: CatalogItemLine[]) => void;
  showQuantity?: boolean;
  addLabel: string;
  removeLabel: string;
  selectPlaceholder: string;
  quantityLabel?: string;
}

function optionKey(id: string, kind: PublicCatalogItemKind): string {
  return `${kind}:${id}`;
}

export function CatalogItemLinesField({
  fieldId,
  options,
  lines,
  onChange,
  showQuantity = false,
  addLabel,
  removeLabel,
  selectPlaceholder,
  quantityLabel,
}: CatalogItemLinesFieldProps) {
  const { t } = useLocale();
  const [pendingKey, setPendingKey] = useState("");

  const selectedKeys = new Set(
    lines.map((line) => optionKey(line.testDefinitionId, line.catalogItemKind)),
  );
  const availableOptions = options.filter(
    (option) => !selectedKeys.has(optionKey(option.id, option.kind)),
  );

  const handleAdd = () => {
    const option = options.find(
      (candidate) => optionKey(candidate.id, candidate.kind) === pendingKey,
    );
    if (!option) {
      return;
    }
    onChange([
      ...lines,
      {
        testDefinitionId: option.id,
        catalogItemKind: option.kind,
        ...(showQuantity ? { quantity: 1 } : {}),
      },
    ]);
    setPendingKey("");
  };

  const handleRemove = (index: number) => {
    onChange(lines.filter((_, lineIndex) => lineIndex !== index));
  };

  const handleQuantityChange = (index: number, quantity: number) => {
    onChange(lines.map((line, lineIndex) => (lineIndex === index ? { ...line, quantity } : line)));
  };

  const kindLabel = (kind: PublicCatalogItemKind): string =>
    kind === "test" ? t.itemPicker.kindTest : t.itemPicker.kindPanel;

  return (
    <div className="catalog-item-lines">
      <div className="catalog-item-lines__picker">
        <label htmlFor={fieldId} className="sr-only">
          {selectPlaceholder}
        </label>
        <select
          id={fieldId}
          value={pendingKey}
          onChange={(event) => setPendingKey(event.target.value)}
        >
          <option value="">{selectPlaceholder}</option>
          {availableOptions.map((option) => (
            <option
              key={optionKey(option.id, option.kind)}
              value={optionKey(option.id, option.kind)}
            >
              {kindLabel(option.kind)}: {option.label}
            </option>
          ))}
        </select>
        <button
          type="button"
          className="btn btn--secondary"
          onClick={handleAdd}
          disabled={!pendingKey}
        >
          {addLabel}
        </button>
      </div>
      {lines.length > 0 && (
        <ul className="catalog-item-lines__list">
          {lines.map((line, index) => {
            const option = options.find(
              (candidate) =>
                optionKey(candidate.id, candidate.kind) ===
                optionKey(line.testDefinitionId, line.catalogItemKind),
            );
            return (
              <li
                key={optionKey(line.testDefinitionId, line.catalogItemKind)}
                className="catalog-item-lines__item"
              >
                <span>
                  {kindLabel(line.catalogItemKind)}: {option?.label ?? line.testDefinitionId}
                </span>
                {showQuantity && (
                  <label>
                    {quantityLabel}
                    <input
                      type="number"
                      min={1}
                      value={line.quantity ?? 1}
                      onChange={(event) =>
                        handleQuantityChange(index, Math.max(1, Number(event.target.value) || 1))
                      }
                    />
                  </label>
                )}
                <button type="button" className="btn btn--text" onClick={() => handleRemove(index)}>
                  {removeLabel}
                </button>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
