import type { JournalEntry } from '@/store/types';

export type Reframe = {
  activatingEvent: string;
  beliefs: string;
  consequences: string;
  synopsis: string;
  /** False when the result is the on-device fallback (AI off, not configured, or unreachable). */
  fromAi: boolean;
};

export type SearchResult = { matchingIds: string[]; insight: string; fromAi: boolean };

const MAX_TRANSCRIPTION_CHARS = 6000;
const MAX_SEARCH_ENTRIES = 100;
const MAX_ENTRY_CHARS = 1000;
const TIMEOUT_MS = 45_000;

/** Address of the stateless AI relay (see /proxy). Empty means AI is unavailable in this build. */
export const AI_PROXY_URL = (process.env.EXPO_PUBLIC_AI_PROXY_URL ?? '').trim();

export function isAiConfigured(endpoint: string = AI_PROXY_URL): boolean {
  return /^https:\/\//.test(endpoint) || /^http:\/\/(localhost|127\.0\.0\.1)(:|\/|$)/.test(endpoint);
}

/** Honest on-device result: reflection prompts rather than a pretend AI analysis. */
export function offlineReframe(): Reframe {
  return {
    activatingEvent:
      'What was happening just before this feeling started? Where were you, and what were you doing?',
    beliefs:
      'What thoughts or predictions went through your mind? Write down the scariest one, then ask: what is the evidence for and against it?',
    consequences:
      'What did you feel in your body and emotions? Naming a sensation (tight chest, racing heart) can make it feel more manageable.',
    synopsis:
      'You took a brave step by putting this into words. Thoughts are mental events, not facts. Take a slow breath out — you are safe right now.',
    fromAi: false,
  };
}

function searchableText(e: JournalEntry) {
  return [e.text, e.activatingEvent, e.beliefs, e.consequences, e.synopsis].join(' ');
}

export function keywordSearch(query: string, entries: JournalEntry[]): SearchResult {
  const terms = query.toLowerCase().split(/\s+/).filter(Boolean);
  const matchingIds = terms.length
    ? entries.filter((e) => {
        const text = searchableText(e).toLowerCase();
        return terms.every((t) => text.includes(t));
      }).map((e) => e.id)
    : [];
  return {
    matchingIds,
    insight: matchingIds.length
      ? `Found ${matchingIds.length} ${matchingIds.length === 1 ? 'entry' : 'entries'} mentioning “${query}”.`
      : `No entries mention “${query}”.`,
    fromAi: false,
  };
}

async function post(endpoint: string, path: string, body: unknown): Promise<any> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS);
  try {
    const res = await fetch(endpoint.replace(/\/+$/, '') + path, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify(body),
      signal: controller.signal,
    });
    if (!res.ok) throw new Error(`AI relay returned ${res.status}`);
    return await res.json();
  } finally {
    clearTimeout(timer);
  }
}

const str = (v: unknown) => (typeof v === 'string' ? v.trim() : '');

export async function reframeEntry(
  text: string,
  aiEnabled: boolean,
  endpoint: string = AI_PROXY_URL,
): Promise<Reframe> {
  if (!aiEnabled || !isAiConfigured(endpoint)) return offlineReframe();
  try {
    const out = await post(endpoint, '/v1/reframe', { transcription: text.slice(0, MAX_TRANSCRIPTION_CHARS) });
    const synopsis = str(out?.ai_synopsis);
    if (!synopsis) throw new Error('empty synopsis');
    return {
      activatingEvent: str(out.activating_event),
      beliefs: str(out.beliefs),
      consequences: str(out.consequences),
      synopsis,
      fromAi: true,
    };
  } catch {
    return offlineReframe();
  }
}

export async function searchEntries(
  query: string,
  entries: JournalEntry[],
  aiEnabled: boolean,
  endpoint: string = AI_PROXY_URL,
): Promise<SearchResult> {
  if (!aiEnabled || !isAiConfigured(endpoint) || entries.length === 0) return keywordSearch(query, entries);
  // The relay speaks numeric ids; map them back to ours.
  const subset = entries.slice(0, MAX_SEARCH_ENTRIES);
  try {
    const out = await post(endpoint, '/v1/search', {
      query: query.slice(0, 200),
      entries: subset.map((e, i) => ({ id: i + 1, text: searchableText(e).slice(0, MAX_ENTRY_CHARS) })),
    });
    const ids: unknown[] = Array.isArray(out?.matching_ids) ? out.matching_ids : [];
    const matchingIds = ids
      .filter((n): n is number => Number.isInteger(n) && (n as number) >= 1 && (n as number) <= subset.length)
      .map((n) => subset[n - 1].id);
    return { matchingIds: [...new Set(matchingIds)], insight: str(out?.pattern_insight), fromAi: true };
  } catch {
    return keywordSearch(query, entries);
  }
}
