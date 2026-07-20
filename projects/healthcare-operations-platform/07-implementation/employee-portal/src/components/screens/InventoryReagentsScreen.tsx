/**
 * Reagent profile administration screen (COM-MOD-010-FE-001, BCM-INV-002).
 *
 * Assigns and displays the reagent profile of an inventory item, backed by ReagentProfileController.
 */
import { useState } from "react";
import { assignReagentProfile, getReagentProfile } from "../../api/inventoryQualityApi";
import type { ReagentProfileRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

export function InventoryReagentsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [linkedTestDefinitionId, setLinkedTestDefinitionId] = useState("");
  const [reagentCategory, setReagentCategory] = useState("");
  const [consumptionUnitRatio, setConsumptionUnitRatio] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [profile, setProfile] = useState<ReagentProfileRecord | undefined>();

  const assignAction = useAsyncAction(async () => {
    const assigned = await assignReagentProfile(inventoryItemId, {
      linkedTestDefinitionId: linkedTestDefinitionId || undefined,
      reagentCategory,
      consumptionUnitRatio,
      actorId,
    });
    setProfile(assigned);
    return assigned;
  });

  const loadAction = useAsyncAction(async () => {
    const loaded = await getReagentProfile(inventoryItemId);
    setProfile(loaded);
    return loaded;
  });

  return (
    <section aria-labelledby="inventory-reagents-heading">
      <h2 id="inventory-reagents-heading">{labels.inventoryReagents.heading}</h2>
      <p>{labels.inventoryReagents.description}</p>

      <div className="panel">
        <form
          onSubmit={(event) => {
            event.preventDefault();
            assignAction.run();
          }}
        >
          <label htmlFor="reagent-item-id">{labels.shared.inventoryItemId}</label>
          <input
            id="reagent-item-id"
            value={inventoryItemId}
            onChange={(e) => setInventoryItemId(e.target.value)}
          />
          <label htmlFor="reagent-linked-test">
            {labels.inventoryReagents.linkedTestDefinitionId}
          </label>
          <input
            id="reagent-linked-test"
            value={linkedTestDefinitionId}
            onChange={(e) => setLinkedTestDefinitionId(e.target.value)}
          />
          <label htmlFor="reagent-category">{labels.inventoryReagents.reagentCategory}</label>
          <input
            id="reagent-category"
            value={reagentCategory}
            onChange={(e) => setReagentCategory(e.target.value)}
          />
          <label htmlFor="reagent-ratio">{labels.inventoryReagents.consumptionUnitRatio}</label>
          <input
            id="reagent-ratio"
            value={consumptionUnitRatio}
            onChange={(e) => setConsumptionUnitRatio(e.target.value)}
          />
          <label htmlFor="reagent-actor-id">{labels.shared.actorId}</label>
          <input
            id="reagent-actor-id"
            value={actorId}
            onChange={(e) => setActorId(e.target.value)}
          />
          <div className="catalog-toolbar">
            <button
              type="submit"
              disabled={!inventoryItemId || !reagentCategory || assignAction.status === "loading"}
            >
              {labels.shared.create}
            </button>
            <button
              type="button"
              disabled={!inventoryItemId || loadAction.status === "loading"}
              onClick={() => loadAction.run()}
            >
              {labels.inventoryReagents.loadProfile}
            </button>
          </div>
        </form>
        <StatusBanner
          status={assignAction.status}
          errorMessage={assignAction.errorMessage}
          successMessage={labels.inventoryReagents.assignSuccess}
        />
        <StatusBanner
          status={loadAction.status}
          errorMessage={loadAction.errorMessage}
          successMessage={labels.shared.loaded}
        />
        {profile ? (
          <table>
            <tbody>
              <tr>
                <th scope="row">{labels.inventoryReagents.reagentCategory}</th>
                <td>{profile.reagentCategory}</td>
              </tr>
              <tr>
                <th scope="row">{labels.inventoryReagents.consumptionUnitRatio}</th>
                <td>{profile.consumptionUnitRatio}</td>
              </tr>
              <tr>
                <th scope="row">{labels.inventoryReagents.linkedTestDefinitionId}</th>
                <td>{profile.linkedTestDefinitionId ?? "-"}</td>
              </tr>
            </tbody>
          </table>
        ) : null}
      </div>
    </section>
  );
}
