import { createContext, useContext, useMemo, useState, type ReactNode } from "react";

export interface AdminScope {
  tenantId?: string;
  laboratoryId?: string;
  branchId?: string;
  userId?: string;
}

interface AdminScopeContextValue {
  scope: AdminScope;
  setTenantId: (tenantId?: string) => void;
  setLaboratoryId: (laboratoryId?: string) => void;
  setBranchId: (branchId?: string) => void;
  setUserId: (userId?: string) => void;
}

const AdminScopeContext = createContext<AdminScopeContextValue | undefined>(undefined);

/**
 * Shares the current tenant/laboratory/branch/user scope across administration
 * screens so every screen can display the active scope, per the UI screen map
 * UX requirement.
 */
export function AdminScopeProvider({ children }: { children: ReactNode }) {
  const [scope, setScope] = useState<AdminScope>({});

  const value = useMemo<AdminScopeContextValue>(
    () => ({
      scope,
      setTenantId: (tenantId) => setScope((current) => ({ ...current, tenantId })),
      setLaboratoryId: (laboratoryId) => setScope((current) => ({ ...current, laboratoryId })),
      setBranchId: (branchId) => setScope((current) => ({ ...current, branchId })),
      setUserId: (userId) => setScope((current) => ({ ...current, userId }))
    }),
    [scope]
  );

  return <AdminScopeContext.Provider value={value}>{children}</AdminScopeContext.Provider>;
}

export function useAdminScope(): AdminScopeContextValue {
  const context = useContext(AdminScopeContext);
  if (!context) {
    throw new Error("useAdminScope must be used within an AdminScopeProvider.");
  }
  return context;
}
