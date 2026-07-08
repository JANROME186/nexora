import { useState, type FormEvent } from "react";
import { createTenant, getTenant } from "../../api/platformFoundationApi";
import type { Tenant } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

export function TenantsScreen() {
  const { setTenantId } = useAdminScope();
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [name, setName] = useState("");
  const [lookupId, setLookupId] = useState("");

  const createAction = useAsyncAction(createTenant);
  const lookupAction = useAsyncAction(getTenant);

  function upsertTenant(tenant: Tenant) {
    setTenants((current) => {
      const withoutExisting = current.filter((item) => item.tenantId !== tenant.tenantId);
      return [tenant, ...withoutExisting];
    });
    setTenantId(tenant.tenantId);
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await createAction.run({ name });
    if (result.ok) {
      upsertTenant(result.data);
      setName("");
    }
  }

  async function handleLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await lookupAction.run(lookupId);
    if (result.ok) {
      upsertTenant(result.data);
    }
  }

  return (
    <section aria-labelledby="tenants-heading">
      <h2 id="tenants-heading">Platform Tenant List</h2>
      <ScopeIndicator />

      <form onSubmit={handleCreate}>
        <h3>Create Tenant</h3>
        <label htmlFor="tenant-name">Tenant name</label>
        <input
          id="tenant-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
        />
        <button type="submit" disabled={createAction.status === "loading"}>
          Create tenant
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage="Tenant created."
        />
      </form>

      <form onSubmit={handleLookup}>
        <h3>Find Tenant</h3>
        <label htmlFor="tenant-lookup">Tenant id</label>
        <input
          id="tenant-lookup"
          value={lookupId}
          onChange={(event) => setLookupId(event.target.value)}
          required
        />
        <button type="submit" disabled={lookupAction.status === "loading"}>
          Find tenant
        </button>
        <StatusBanner
          status={lookupAction.status}
          errorMessage={lookupAction.errorMessage}
          successMessage="Tenant found."
        />
      </form>

      <table>
        <caption>Tenants in this session</caption>
        <thead>
          <tr>
            <th scope="col">Tenant id</th>
            <th scope="col">Name</th>
            <th scope="col">Status</th>
          </tr>
        </thead>
        <tbody>
          {tenants.map((tenant) => (
            <tr key={tenant.tenantId}>
              <td>{tenant.tenantId}</td>
              <td>{tenant.name}</td>
              <td>{tenant.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
