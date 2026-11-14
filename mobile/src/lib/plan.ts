import type { PanicPlan, PlanStepId } from '@/store/types';

// The panic plan: a short, personal list written while calm, so nothing has to be decided in the moment.

export type PlanStep = {
  id: PlanStepId;
  title: string;
  body: string;
  /** Always in the plan and always first. */
  locked?: boolean;
  /** Where the step's button goes, if it has one. */
  href?: '/breathing' | '/grounding' | '/journal/new';
};

export const DEFAULT_REMINDER = 'This is panic. It is uncomfortable, not dangerous, and it will pass.';

/** Catalogue order is plan order. */
export const PLAN_STEPS: PlanStep[] = [
  { id: 'safe', title: 'Get somewhere safe first', body: 'If I’m driving, I pull over and stop. If I’m somewhere busy or unsafe, I move to a quieter spot.', locked: true },
  { id: 'breathe', title: 'Breathe with the orb', body: 'A few rounds of box breathing to slow everything down.', href: '/breathing' },
  { id: 'ground', title: 'Ground myself: 5-4-3-2-1', body: 'Notice what I can see, touch, hear, smell and taste.', href: '/grounding' },
  { id: 'reminder', title: 'Tell myself the truth', body: 'Read my own words back to myself.' },
  { id: 'contact', title: 'Reach out to my person', body: 'Call or text the person I chose.' },
  { id: 'water', title: 'Cool down', body: 'Sip cold water, or run cold water over my wrists.' },
  { id: 'journal', title: 'When it eases, write it down', body: 'Get it out of my head while it’s fresh.', href: '/journal/new' },
];

export const DEFAULT_PLAN: PanicPlan = { steps: ['safe', 'breathe', 'ground', 'reminder', 'contact'], reminder: DEFAULT_REMINDER };

const IDS = new Set<string>(PLAN_STEPS.map((s) => s.id));

/** Keeps only known steps, in catalogue order, always starting with "get somewhere safe". */
export function cleanPlan(value: unknown): PanicPlan {
  const v = (value ?? {}) as Partial<PanicPlan>;
  const chosen = new Set<PlanStepId>(Array.isArray(v.steps) ? v.steps.filter((s): s is PlanStepId => IDS.has(s)) : DEFAULT_PLAN.steps);
  chosen.add('safe');
  const reminder = typeof v.reminder === 'string' ? v.reminder.slice(0, 280) : DEFAULT_REMINDER;
  return { steps: PLAN_STEPS.map((s) => s.id).filter((id) => chosen.has(id)), reminder };
}

export function togglePlanStep(plan: PanicPlan, id: PlanStepId): PanicPlan {
  if (id === 'safe') return plan;
  const steps = plan.steps.includes(id) ? plan.steps.filter((s) => s !== id) : [...plan.steps, id];
  return cleanPlan({ ...plan, steps });
}

/** Digits and a leading + only. Returns '' when it can't be a phone number. */
export function cleanPhone(raw: string): string {
  const trimmed = raw.trim();
  const digits = trimmed.replace(/\D/g, '');
  if (digits.length < 3 || digits.length > 15) return '';
  return (trimmed.startsWith('+') ? '+' : '') + digits;
}

export const PANIC_TEXT = 'I’m having a panic attack. Can you call me or stay with me for a bit?';
