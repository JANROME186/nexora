import { siteConfig } from "../../config/siteConfig";
import type { Locale } from "../../i18n/LocaleContext";
import { pickLocalized } from "../../i18n/pickLocalized";

export function BranchSelect({
  id,
  label,
  locale,
  value,
  onChange,
}: {
  id: string;
  label: string;
  locale: Locale;
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <div className="form-field">
      <label htmlFor={id}>{label}</label>
      <select id={id} value={value} onChange={(event) => onChange(event.target.value)} required>
        {siteConfig.branches.map((branch) => (
          <option key={branch.branchId} value={branch.branchId}>
            {pickLocalized(locale, branch.nameEs, branch.nameEn)}
          </option>
        ))}
      </select>
    </div>
  );
}
