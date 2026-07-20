import type { ReactNode } from "react";

export interface DataTableColumn<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
}

interface DataTableProps<T> {
  caption: string;
  columns: DataTableColumn<T>[];
  rows: T[];
  rowKey: (row: T) => string;
  onSelectRow?: (row: T) => void;
}

/**
 * Generic list table shared by administration screens (TD-FE-010 remediation): moves table
 * markup, the row-select-button pattern and empty-row suppression into one reusable component so
 * per-screen render functions stay small instead of each hand-rolling the same table structure.
 */
export function DataTable<T>({ caption, columns, rows, rowKey, onSelectRow }: DataTableProps<T>) {
  if (rows.length === 0) {
    return null;
  }

  return (
    <table>
      <caption>{caption}</caption>
      <thead>
        <tr>
          {columns.map((column) => (
            <th key={column.key} scope="col">
              {column.header}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={rowKey(row)}>
            {columns.map((column, index) => (
              <td key={column.key}>
                {index === 0 && onSelectRow ? (
                  <button type="button" className="link-button" onClick={() => onSelectRow(row)}>
                    {column.render(row)}
                  </button>
                ) : (
                  column.render(row)
                )}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
