/**
 * AI assistant and human-review administration screen (COM-MOD-015-FE-001).
 *
 * Requests provider-neutral assistant drafts and keeps generated text review-gated with visible
 * citations, model references, policy version and audit history for BCM-AI-001..008.
 */
import { useMemo, useState } from "react";
import {
  listAssistantAuditRecords,
  requestAssistantDraft,
  reviewAssistantDraft,
  type AiAssistantPurpose,
  type AiInteraction,
  type AiReviewDecision,
} from "../../api/aiOverlayApi";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type Labels = MessageCatalog["aiOverlay"];

const CAPABILITY_SOURCE_CONTEXTS: Record<AiAssistantPurpose, string[]> = {
  ocr_document_intake: ["referral", "order", "invoice", "external_report"],
  result_case_summary: ["case", "result"],
  semantic_search: ["search_index", "case", "result", "document"],
  retrieval_grounding: ["knowledge_base", "case", "result", "document"],
};

function firstSourceContext(purpose: AiAssistantPurpose): string {
  return CAPABILITY_SOURCE_CONTEXTS[purpose][0];
}

function hasReviewableCitations(interaction: AiInteraction | undefined): boolean {
  return Boolean(interaction?.citations.length);
}

function interactionColumns(labels: Labels): DataTableColumn<AiInteraction>[] {
  return [
    { key: "sessionId", header: labels.sessionId, render: (row) => row.sessionId },
    { key: "purpose", header: labels.purpose, render: (row) => row.purpose },
    { key: "source", header: labels.sourceContext, render: (row) => row.sourceContextId },
    { key: "reviewStatus", header: labels.reviewStatus, render: (row) => row.reviewStatus },
    { key: "safetyDecision", header: labels.safetyDecision, render: (row) => row.safetyDecision },
  ];
}

interface DraftFormProps {
  labels: Labels;
  status: AsyncStatus;
  errorMessage?: string;
  disabled: boolean;
  onSubmit: (fields: {
    purpose: AiAssistantPurpose;
    sourceContextType: string;
    sourceContextId: string;
    prompt: string;
  }) => void;
}

function DraftForm({ labels, status, errorMessage, disabled, onSubmit }: DraftFormProps) {
  const [purpose, setPurpose] = useState<AiAssistantPurpose>("result_case_summary");
  const [sourceContextType, setSourceContextType] = useState(firstSourceContext(purpose));
  const [sourceContextId, setSourceContextId] = useState("result-1001");
  const [prompt, setPrompt] = useState(labels.defaultPrompt);
  const sourceContexts = CAPABILITY_SOURCE_CONTEXTS[purpose];

  function handlePurposeChange(nextPurpose: AiAssistantPurpose) {
    setPurpose(nextPurpose);
    setSourceContextType(firstSourceContext(nextPurpose));
  }

  return (
    <div className="panel">
      <h3>{labels.requestHeading}</h3>
      <label htmlFor="ai-purpose">{labels.purpose}</label>
      <select
        id="ai-purpose"
        value={purpose}
        onChange={(event) => handlePurposeChange(event.target.value as AiAssistantPurpose)}
      >
        {labels.purposes.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      <label htmlFor="ai-source-context-type">{labels.sourceContextType}</label>
      <select
        id="ai-source-context-type"
        value={sourceContextType}
        onChange={(event) => setSourceContextType(event.target.value)}
      >
        {sourceContexts.map((context) => (
          <option key={context} value={context}>
            {context}
          </option>
        ))}
      </select>
      <label htmlFor="ai-source-context-id">{labels.sourceContextId}</label>
      <input
        id="ai-source-context-id"
        value={sourceContextId}
        onChange={(event) => setSourceContextId(event.target.value)}
      />
      <label htmlFor="ai-prompt">{labels.prompt}</label>
      <textarea
        id="ai-prompt"
        rows={5}
        value={prompt}
        onChange={(event) => setPrompt(event.target.value)}
      />
      <button
        type="button"
        disabled={disabled || !sourceContextId.trim() || !prompt.trim()}
        onClick={() => onSubmit({ purpose, sourceContextType, sourceContextId, prompt })}
      >
        {labels.requestDraft}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.draftReady}
      />
    </div>
  );
}

interface DraftOutputProps {
  labels: Labels;
  interaction?: AiInteraction;
}

function DraftOutput({ labels, interaction }: DraftOutputProps) {
  if (!interaction) {
    return <p className="empty-state">{labels.noDraft}</p>;
  }
  return (
    <div className="panel" aria-live="polite">
      <h3>{labels.outputHeading}</h3>
      <p>
        <strong>{labels.aiGeneratedLabel}</strong>
      </p>
      <p className="ai-output">{interaction.draftOutput}</p>
      <dl className="ai-metadata">
        <dt>{labels.confidenceBand}</dt>
        <dd>{interaction.confidenceBand}</dd>
        <dt>{labels.model}</dt>
        <dd>
          {interaction.modelProviderRef} / {interaction.modelNameRef}
        </dd>
        <dt>{labels.policyVersion}</dt>
        <dd>{interaction.policyVersion}</dd>
      </dl>
      <h4>{labels.citations}</h4>
      <ul>
        {interaction.citations.map((citation) => (
          <li key={citation}>{citation}</li>
        ))}
      </ul>
    </div>
  );
}

interface ReviewFormProps {
  labels: Labels;
  interaction?: AiInteraction;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { reviewerId: string; decision: AiReviewDecision; reason: string }) => void;
}

function ReviewForm({ labels, interaction, status, errorMessage, onSubmit }: ReviewFormProps) {
  const [reviewerId, setReviewerId] = useState("reviewer-1");
  const [decision, setDecision] = useState<AiReviewDecision>("accepted");
  const [reason, setReason] = useState(labels.defaultReviewReason);
  const canReview =
    interaction?.reviewStatus === "human_review_required" && hasReviewableCitations(interaction);

  return (
    <div className="panel">
      <h3>{labels.reviewHeading}</h3>
      {!hasReviewableCitations(interaction) ? (
        <p className="empty-state">{labels.citationsRequired}</p>
      ) : null}
      <label htmlFor="ai-reviewer-id">{labels.reviewerId}</label>
      <input
        id="ai-reviewer-id"
        value={reviewerId}
        onChange={(event) => setReviewerId(event.target.value)}
      />
      <label htmlFor="ai-review-decision">{labels.reviewDecision}</label>
      <select
        id="ai-review-decision"
        value={decision}
        onChange={(event) => setDecision(event.target.value as AiReviewDecision)}
      >
        <option value="accepted">{labels.accepted}</option>
        <option value="rejected">{labels.rejected}</option>
      </select>
      <label htmlFor="ai-review-reason">{labels.reviewReason}</label>
      <textarea
        id="ai-review-reason"
        rows={3}
        value={reason}
        onChange={(event) => setReason(event.target.value)}
      />
      <button
        type="button"
        disabled={!canReview || status === "loading" || !reviewerId.trim() || !reason.trim()}
        onClick={() => onSubmit({ reviewerId, decision, reason })}
      >
        {labels.submitReview}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.reviewSaved}
      />
    </div>
  );
}

export function AiAssistantReviewScreen() {
  const { t } = useLocale();
  const labels = t.aiOverlay;
  const { scope } = useAdminScope();
  const [interactions, setInteractions] = useState<AiInteraction[]>([]);
  const [selected, setSelected] = useState<AiInteraction | undefined>();
  const requestAction = useAsyncAction(requestAssistantDraft);
  const reviewAction = useAsyncAction(reviewAssistantDraft);
  const auditAction = useAsyncAction(listAssistantAuditRecords);
  const columns = useMemo(() => interactionColumns(labels), [labels]);
  const tenantReady = Boolean(scope.tenantId);

  async function handleRequest(fields: Parameters<typeof requestAssistantDraft>[0]) {
    const result = await requestAction.run(fields);
    if (result.ok) {
      setSelected(result.data);
      setInteractions((current) => [result.data, ...current]);
    }
  }

  async function handleReview(fields: Parameters<typeof reviewAssistantDraft>[1]) {
    if (!selected) return;
    const result = await reviewAction.run(selected.sessionId, fields);
    if (result.ok) {
      setSelected(result.data);
      setInteractions((current) =>
        current.map((interaction) =>
          interaction.sessionId === result.data.sessionId ? result.data : interaction,
        ),
      );
    }
  }

  async function handleLoadAudit() {
    const result = await auditAction.run();
    if (result.ok) setInteractions(result.data);
  }

  return (
    <section aria-labelledby="ai-overlay-heading">
      <h2 id="ai-overlay-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>
      {!tenantReady ? <p className="empty-state">{labels.tenantRequired}</p> : null}
      <DraftForm
        labels={labels}
        status={requestAction.status}
        errorMessage={requestAction.errorMessage}
        disabled={!tenantReady}
        onSubmit={handleRequest}
      />
      <DraftOutput labels={labels} interaction={selected} />
      <ReviewForm
        labels={labels}
        interaction={selected}
        status={reviewAction.status}
        errorMessage={reviewAction.errorMessage}
        onSubmit={handleReview}
      />
      <button
        type="button"
        disabled={!tenantReady || auditAction.status === "loading"}
        onClick={handleLoadAudit}
      >
        {labels.loadAudit}
      </button>
      <StatusBanner
        status={auditAction.status}
        errorMessage={auditAction.errorMessage}
        successMessage={labels.auditLoaded}
      />
      {interactions.length === 0 ? <p className="empty-state">{labels.noAuditRecords}</p> : null}
      <DataTable
        caption={labels.auditCaption}
        columns={columns}
        rows={interactions}
        rowKey={(interaction) => interaction.sessionId}
        onSelectRow={setSelected}
      />
    </section>
  );
}
