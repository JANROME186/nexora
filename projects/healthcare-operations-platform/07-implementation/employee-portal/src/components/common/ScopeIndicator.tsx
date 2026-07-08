import { useAdminScope } from "../../state/AdminScopeContext";

/**
 * Displays the current tenant/laboratory/branch/user scope so every
 * administration screen keeps that context visible, per the UI screen map.
 */
export function ScopeIndicator() {
  const { scope } = useAdminScope();
  const entries = [
    ["Tenant", scope.tenantId],
    ["Laboratory", scope.laboratoryId],
    ["Branch", scope.branchId],
    ["User", scope.userId]
  ] as const;

  return (
    <div className="scope-indicator" aria-label="Current administration scope">
      {entries.map(([label, value]) => (
        <span key={label} className="scope-indicator__item">
          <strong>{label}:</strong> {value ?? "none"}
        </span>
      ))}
    </div>
  );
}
