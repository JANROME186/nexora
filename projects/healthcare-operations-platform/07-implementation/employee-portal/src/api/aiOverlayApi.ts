/**
 * AI Overlay API client (COM-MOD-015-FE-001).
 *
 * Covers the provider-neutral assistant orchestration surface compiled by COM-MOD-015-BE-001/002.
 * The UI intentionally calls the generic assistant endpoint only; the backend rule engine resolves
 * BCM-AI-002..005 from `purpose` and enforces source-context, citation and human-review guardrails.
 */
import { get, post } from "./httpClient";

export type AiAssistantPurpose =
  | "ocr_document_intake"
  | "result_case_summary"
  | "semantic_search"
  | "retrieval_grounding";

export type AiReviewDecision = "accepted" | "rejected";

export interface AiAssistantRequest {
  purpose: AiAssistantPurpose;
  sourceContextType: string;
  sourceContextId: string;
  prompt: string;
}

export interface AiReviewRequest {
  reviewerId: string;
  decision: AiReviewDecision;
  reason: string;
}

export interface AiInteraction {
  sessionId: string;
  tenantId: string;
  actorId: string;
  purpose: string;
  sourceContextType: string;
  sourceContextId: string;
  draftOutput: string;
  citations: string[];
  confidenceBand: string;
  safetyDecision: string;
  reviewStatus: string;
  reviewerId?: string;
  reviewReason?: string;
  modelProviderRef: string;
  modelNameRef: string;
  policyVersion: string;
  lifecycleStatus: string;
  createdAt?: string;
  updatedAt?: string;
}

const ASSISTANT_SESSIONS_BASE = "/api/ai/assistant/sessions";

function encode(value: string): string {
  return encodeURIComponent(value);
}

export function requestAssistantDraft(request: AiAssistantRequest): Promise<AiInteraction> {
  return post<AiInteraction>(ASSISTANT_SESSIONS_BASE, request);
}

export function getAssistantSession(sessionId: string): Promise<AiInteraction> {
  return get<AiInteraction>(`${ASSISTANT_SESSIONS_BASE}/${encode(sessionId)}`);
}

export function reviewAssistantDraft(
  sessionId: string,
  request: AiReviewRequest,
): Promise<AiInteraction> {
  return post<AiInteraction>(`${ASSISTANT_SESSIONS_BASE}/${encode(sessionId)}/review`, request);
}

export function listAssistantAuditRecords(): Promise<AiInteraction[]> {
  return get<AiInteraction[]>(`${ASSISTANT_SESSIONS_BASE}/audit-records`);
}
