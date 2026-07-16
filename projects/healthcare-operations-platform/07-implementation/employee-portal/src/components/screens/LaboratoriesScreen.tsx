import { useEffect, useState, type FormEvent } from "react";
import { createLaboratory, getLaboratory } from "../../api/platformFoundationApi";
import type { Laboratory } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

export function LaboratoriesScreen() {
  const { scope, setTenantId, setLaboratoryId } = useAdminScope();
  const [laboratories, setLaboratories] = useState<Laboratory[]>([]);
  const [tenantId, setTenantIdInput] = useState(scope.tenantId ?? "");
  const [name, setName] = useState("");
  const [lookupId, setLookupId] = useState("");

  useEffect(() => {
    if (scope.tenantId) {
      setTenantIdInput(scope.tenantId);
    }
  }, [scope.tenantId]);

  const createAction = useAsyncAction(createLaboratory);
  const lookupAction = useAsyncAction(getLaboratory);

  function upsertLaboratory(laboratory: Laboratory) {
    setLaboratories((current) => {
      const withoutExisting = current.filter(
        (item) => item.laboratoryId !== laboratory.laboratoryId,
      );
      return [laboratory, ...withoutExisting];
    });
    setLaboratoryId(laboratory.laboratoryId);
    setTenantId(laboratory.tenantId);
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await createAction.run({ tenantId, name });
    if (result.ok) {
      upsertLaboratory(result.data);
      setName("");
    }
  }

  async function handleLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await lookupAction.run(lookupId);
    if (result.ok) {
      upsertLaboratory(result.data);
    }
  }

  return (
    <section aria-labelledby="laboratories-heading">
      <h2 id="laboratories-heading">Laboratory List</h2>
      <ScopeIndicator />

      <form onSubmit={handleCreate}>
        <h3>Create Laboratory</h3>
        <label htmlFor="laboratory-tenant-id">Tenant id</label>
        <input
          id="laboratory-tenant-id"
          value={tenantId}
          onChange={(event) => setTenantIdInput(event.target.value)}
          required
        />
        <label htmlFor="laboratory-name">Laboratory name</label>
        <input
          id="laboratory-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
        />
        <button type="submit" disabled={createAction.status === "loading"}>
          Create laboratory
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage="Laboratory created."
        />
      </form>

      <form onSubmit={handleLookup}>
        <h3>Find Laboratory</h3>
        <label htmlFor="laboratory-lookup">Laboratory id</label>
        <input
          id="laboratory-lookup"
          value={lookupId}
          onChange={(event) => setLookupId(event.target.value)}
          required
        />
        <button type="submit" disabled={lookupAction.status === "loading"}>
          Find laboratory
        </button>
        <StatusBanner
          status={lookupAction.status}
          errorMessage={lookupAction.errorMessage}
          successMessage="Laboratory found."
        />
      </form>

      <table>
        <caption>Laboratories in this session</caption>
        <thead>
          <tr>
            <th scope="col">Laboratory id</th>
            <th scope="col">Tenant id</th>
            <th scope="col">Name</th>
            <th scope="col">Status</th>
          </tr>
        </thead>
        <tbody>
          {laboratories.map((laboratory) => (
            <tr key={laboratory.laboratoryId}>
              <td>{laboratory.laboratoryId}</td>
              <td>{laboratory.tenantId}</td>
              <td>{laboratory.name}</td>
              <td>{laboratory.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
