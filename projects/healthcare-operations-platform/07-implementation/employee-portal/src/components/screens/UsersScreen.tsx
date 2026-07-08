import { useEffect, useState, type FormEvent } from "react";
import { createUser, getUser } from "../../api/platformFoundationApi";
import type { UserAccount } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

export function UsersScreen() {
  const { scope, setUserId, setTenantId } = useAdminScope();
  const [users, setUsers] = useState<UserAccount[]>([]);
  const [tenantId, setTenantIdInput] = useState(scope.tenantId ?? "");
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [lookupId, setLookupId] = useState("");

  useEffect(() => {
    if (scope.tenantId) {
      setTenantIdInput(scope.tenantId);
    }
  }, [scope.tenantId]);

  const createAction = useAsyncAction(createUser);
  const lookupAction = useAsyncAction(getUser);

  function upsertUser(user: UserAccount) {
    setUsers((current) => {
      const withoutExisting = current.filter((item) => item.userId !== user.userId);
      return [user, ...withoutExisting];
    });
    setUserId(user.userId);
    setTenantId(user.tenantId);
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await createAction.run({ tenantId, displayName, email });
    if (result.ok) {
      upsertUser(result.data);
      setDisplayName("");
      setEmail("");
    }
  }

  async function handleLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await lookupAction.run(lookupId);
    if (result.ok) {
      upsertUser(result.data);
    }
  }

  return (
    <section aria-labelledby="users-heading">
      <h2 id="users-heading">User Management</h2>
      <ScopeIndicator />

      <form onSubmit={handleCreate}>
        <h3>Create User</h3>
        <label htmlFor="user-tenant-id">Tenant id</label>
        <input
          id="user-tenant-id"
          value={tenantId}
          onChange={(event) => setTenantIdInput(event.target.value)}
          required
        />
        <label htmlFor="user-display-name">Display name</label>
        <input
          id="user-display-name"
          value={displayName}
          onChange={(event) => setDisplayName(event.target.value)}
          required
        />
        <label htmlFor="user-email">Email</label>
        <input
          id="user-email"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
        />
        <button type="submit" disabled={createAction.status === "loading"}>
          Create user
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage="User created."
        />
      </form>

      <form onSubmit={handleLookup}>
        <h3>Find User</h3>
        <label htmlFor="user-lookup">User id</label>
        <input
          id="user-lookup"
          value={lookupId}
          onChange={(event) => setLookupId(event.target.value)}
          required
        />
        <button type="submit" disabled={lookupAction.status === "loading"}>
          Find user
        </button>
        <StatusBanner
          status={lookupAction.status}
          errorMessage={lookupAction.errorMessage}
          successMessage="User found."
        />
      </form>

      <table>
        <caption>Users in this session</caption>
        <thead>
          <tr>
            <th scope="col">User id</th>
            <th scope="col">Tenant id</th>
            <th scope="col">Display name</th>
            <th scope="col">Email</th>
            <th scope="col">Status</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.userId}</td>
              <td>{user.tenantId}</td>
              <td>{user.displayName}</td>
              <td>{user.email}</td>
              <td>{user.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
