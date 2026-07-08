import { useEffect, useState, type FormEvent } from "react";
import { assignRole } from "../../api/platformFoundationApi";
import type { AccessScopeType } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { ConfirmDialog } from "../common/ConfirmDialog";

const SCOPE_TYPES: AccessScopeType[] = ["platform", "tenant", "laboratory", "branch"];

export function RoleAssignmentsScreen() {
  const { scope, setUserId } = useAdminScope();
  const [userId, setUserIdInput] = useState(scope.userId ?? "");
  const [roleCode, setRoleCode] = useState("");
  const [scopeType, setScopeType] = useState<AccessScopeType>("tenant");
  const [scopeId, setScopeId] = useState(scope.tenantId ?? "");
  const [pendingSubmit, setPendingSubmit] = useState(false);

  useEffect(() => {
    if (scope.userId) {
      setUserIdInput(scope.userId);
    }
  }, [scope.userId]);

  const assignAction = useAsyncAction((targetUserId: string) =>
    assignRole(targetUserId, { roleCode, scope: { type: scopeType, id: scopeId } })
  );

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPendingSubmit(true);
  }

  async function handleConfirm() {
    setPendingSubmit(false);
    const result = await assignAction.run(userId);
    if (result.ok) {
      setUserId(userId);
    }
  }

  function handleCancel() {
    setPendingSubmit(false);
  }

  return (
    <section aria-labelledby="role-assignments-heading">
      <h2 id="role-assignments-heading">Role Assignment</h2>
      <ScopeIndicator />

      <form onSubmit={handleSubmit}>
        <h3>Assign Scoped Role</h3>
        <label htmlFor="role-user-id">User id</label>
        <input
          id="role-user-id"
          value={userId}
          onChange={(event) => setUserIdInput(event.target.value)}
          required
        />
        <label htmlFor="role-code">Role code</label>
        <input
          id="role-code"
          value={roleCode}
          onChange={(event) => setRoleCode(event.target.value)}
          required
        />
        <label htmlFor="role-scope-type">Scope type</label>
        <select
          id="role-scope-type"
          value={scopeType}
          onChange={(event) => setScopeType(event.target.value as AccessScopeType)}
        >
          {SCOPE_TYPES.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>
        <label htmlFor="role-scope-id">Scope id</label>
        <input
          id="role-scope-id"
          value={scopeId}
          onChange={(event) => setScopeId(event.target.value)}
          required
        />
        <button type="submit" disabled={assignAction.status === "loading"}>
          Assign role
        </button>
        <StatusBanner
          status={assignAction.status}
          errorMessage={assignAction.errorMessage}
          successMessage="Role assigned."
        />
      </form>

      <ConfirmDialog
        open={pendingSubmit}
        title="Confirm role assignment"
        description={
          <p>
            Assign role <strong>{roleCode}</strong> to user <strong>{userId}</strong> scoped to{" "}
            <strong>
              {scopeType}:{scopeId}
            </strong>
            ? This changes access.
          </p>
        }
        confirmLabel="Assign role"
        onConfirm={handleConfirm}
        onCancel={handleCancel}
      />
    </section>
  );
}
