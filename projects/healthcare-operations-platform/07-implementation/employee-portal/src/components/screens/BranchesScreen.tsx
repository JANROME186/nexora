import { useEffect, useState, type FormEvent } from "react";
import { createBranch, getBranch } from "../../api/platformFoundationApi";
import type { Branch } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

export function BranchesScreen() {
  const { scope, setBranchId, setTenantId, setLaboratoryId } = useAdminScope();
  const [branches, setBranches] = useState<Branch[]>([]);
  const [laboratoryId, setLaboratoryIdInput] = useState(scope.laboratoryId ?? "");
  const [name, setName] = useState("");
  const [lookupId, setLookupId] = useState("");

  useEffect(() => {
    if (scope.laboratoryId) {
      setLaboratoryIdInput(scope.laboratoryId);
    }
  }, [scope.laboratoryId]);

  const createAction = useAsyncAction(createBranch);
  const lookupAction = useAsyncAction(getBranch);

  function upsertBranch(branch: Branch) {
    setBranches((current) => {
      const withoutExisting = current.filter((item) => item.branchId !== branch.branchId);
      return [branch, ...withoutExisting];
    });
    setBranchId(branch.branchId);
    setLaboratoryId(branch.laboratoryId);
    setTenantId(branch.tenantId);
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await createAction.run({ laboratoryId, name });
    if (result.ok) {
      upsertBranch(result.data);
      setName("");
    }
  }

  async function handleLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await lookupAction.run(lookupId);
    if (result.ok) {
      upsertBranch(result.data);
    }
  }

  return (
    <section aria-labelledby="branches-heading">
      <h2 id="branches-heading">Branch List</h2>
      <ScopeIndicator />

      <form onSubmit={handleCreate}>
        <h3>Create Branch</h3>
        <label htmlFor="branch-laboratory-id">Laboratory id</label>
        <input
          id="branch-laboratory-id"
          value={laboratoryId}
          onChange={(event) => setLaboratoryIdInput(event.target.value)}
          required
        />
        <label htmlFor="branch-name">Branch name</label>
        <input id="branch-name" value={name} onChange={(event) => setName(event.target.value)} required />
        <button type="submit" disabled={createAction.status === "loading"}>
          Create branch
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage="Branch created."
        />
      </form>

      <form onSubmit={handleLookup}>
        <h3>Find Branch</h3>
        <label htmlFor="branch-lookup">Branch id</label>
        <input
          id="branch-lookup"
          value={lookupId}
          onChange={(event) => setLookupId(event.target.value)}
          required
        />
        <button type="submit" disabled={lookupAction.status === "loading"}>
          Find branch
        </button>
        <StatusBanner
          status={lookupAction.status}
          errorMessage={lookupAction.errorMessage}
          successMessage="Branch found."
        />
      </form>

      <table>
        <caption>Branches in this session</caption>
        <thead>
          <tr>
            <th scope="col">Branch id</th>
            <th scope="col">Laboratory id</th>
            <th scope="col">Tenant id</th>
            <th scope="col">Name</th>
            <th scope="col">Status</th>
          </tr>
        </thead>
        <tbody>
          {branches.map((branch) => (
            <tr key={branch.branchId}>
              <td>{branch.branchId}</td>
              <td>{branch.laboratoryId}</td>
              <td>{branch.tenantId}</td>
              <td>{branch.name}</td>
              <td>{branch.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
