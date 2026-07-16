import type { Money } from "./types";

/** Renders a Money value as "<currency> <amount with 2 decimals>", or an em dash when absent. */
export function formatMoney(money?: Money): string {
  if (!money) return "—";
  return `${money.currency} ${money.amount.toFixed(2)}`;
}
