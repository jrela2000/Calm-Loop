import type { BreathingSession, GroundingSession, JournalEntry } from '@/store/types';

const dateTime = (t: number) =>
  new Date(t).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });

/** Plain-text version of one entry for the share sheet. Built line by line so multi-line text stays intact. */
export function entryAsText(e: JournalEntry): string {
  return [
    'CalmLoop journal entry',
    dateTime(e.createdAt),
    '',
    e.aiGenerated ? 'AI-generated reflection:' : 'Reflection:',
    e.synopsis,
    '',
    'A · What happened',
    e.activatingEvent,
    '',
    'B · Thoughts',
    e.beliefs,
    '',
    'C · Feelings',
    e.consequences,
    '',
    'In my words',
    e.text,
  ].join('\n');
}

export type Backup = {
  app: 'calmloop';
  format: 1;
  exportedAt: string;
  entries: JournalEntry[];
  breathing: BreathingSession[];
  grounding: GroundingSession[];
};

export function makeBackup(data: Omit<Backup, 'app' | 'format' | 'exportedAt'>, now = new Date()): Backup {
  return { app: 'calmloop', format: 1, exportedAt: now.toISOString(), ...data };
}

const isObj = (v: unknown): v is Record<string, unknown> => typeof v === 'object' && v !== null;
const isStr = (v: unknown): v is string => typeof v === 'string';
const isNum = (v: unknown): v is number => typeof v === 'number' && Number.isFinite(v);

function validEntry(v: unknown): v is JournalEntry {
  return (
    isObj(v) && isStr(v.id) && isNum(v.createdAt) && isStr(v.text) && isStr(v.activatingEvent) &&
    isStr(v.beliefs) && isStr(v.consequences) && isStr(v.synopsis) && typeof v.aiGenerated === 'boolean'
  );
}
function validBreathing(v: unknown): v is BreathingSession {
  return isObj(v) && isStr(v.id) && isNum(v.createdAt) && isNum(v.rounds);
}
function validGrounding(v: unknown): v is GroundingSession {
  return (
    isObj(v) && isStr(v.id) && isNum(v.createdAt) && Array.isArray(v.answers) &&
    v.answers.every((a) => Array.isArray(a) && a.every(isStr))
  );
}

/** Parses a backup file, keeping only well-formed records. Throws a readable error otherwise. */
export function parseBackup(json: string): Pick<Backup, 'entries' | 'breathing' | 'grounding'> {
  let data: unknown;
  try {
    data = JSON.parse(json);
  } catch {
    throw new Error('That file isn’t a CalmLoop backup.');
  }
  if (!isObj(data) || data.app !== 'calmloop') throw new Error('That file isn’t a CalmLoop backup.');
  if (data.format !== 1) throw new Error('This backup was made by a newer version of CalmLoop.');
  const list = (v: unknown) => (Array.isArray(v) ? v : []);
  return {
    entries: list(data.entries).filter(validEntry),
    breathing: list(data.breathing).filter(validBreathing),
    grounding: list(data.grounding).filter(validGrounding),
  };
}
